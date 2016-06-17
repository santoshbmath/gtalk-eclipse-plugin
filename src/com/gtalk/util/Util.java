package com.gtalk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.util.StringUtils;

import com.gtalk.GTalkPlugin;
import com.gtalk.views.GTalkChatView;

/**
 * Utility class
 * 
 * @author maths
 *
 */
public class Util {
	private static XMPPConnection connection;
	private static Map<String, RosterEntry> buddiesMap = new HashMap<String, RosterEntry>();
	
	/**
	 * returns current connection
	 * 
	 * @return XMPPConnection
	 */
	public static XMPPConnection getConnection() {
		return connection;
	}
	
	
	/**
	 * closes current connection
	 * 
	 * @return XMPPConnection
	 */
	public static void closeConnection() {
		if(connection != null)
			connection.disconnect();
	}
	
	/**
	 * Checks the connection.
	 * 
	 * @return true	if connected else returns false
	 */
	public static boolean isConnected(){
		if(connection == null)
			return false;
		return connection.isConnected();
	}

	/**
	 * This method connects to gmail server
	 * 
	 * @return returns whether connection is successful or failed.
	 */
	public static String connect(){
		ConnectionConfiguration config = new ConnectionConfiguration(Constants.GMAIL_HOST, Constants.GMAIL_PORT, Constants.GMAIL_DOT_COM);
		
		IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();
		if(store.getString(Constants.ENABLE_PROXY).equals("true")){
			String host = store.getString(Constants.PROXY_HOST);
			String port = store.getString(Constants.PROXY_PORT);
			String user = null;
			String pwd = null;
			
			if(store.getString(Constants.REQUIRE_AUTH).equals("true")){
				user = store.getString(Constants.AUTH_USER);
				pwd = store.getString(Constants.AUTH_PWD);
			}
			
			ProxyInfo proxyInfo = new ProxyInfo(ProxyInfo.ProxyType.HTTP, host, Integer.parseInt(port), user, pwd);
			config.setSocketFactory(proxyInfo.getSocketFactory());
		}
		
		connection = new XMPPConnection(config);

		try {
			connection.connect();
		} catch (XMPPException e) {
			logException("connect(): "+e.getMessage());
			return Constants.CONNECTION_FAILED;
		}

		return Constants.CONNECTION_SUCCESSFULL;
	}
	
	/**
	 * Returns the currently logged user
	 * 
	 * @return user name (without @gmail.com)
	 */
	public static String getCurrentUser(){
		if(!isConnected())
			return "";
		
		return StringUtils.parseName(connection.getUser());
	}
	
	/**
	 * This method logins the given user.
	 * 
	 * @param userName username
	 * @param password password
	 * @return returns "" if successful else returns the error message 
	 */
	public static String login(String userName, String password){
		try {
			connection.login(userName, password, "ecl-plugin");
			setStatus(0, "");
			initializeBuddies();
			GTalkChatView.addToHistory(userName, "logged in");
		} catch (XMPPException e) {
			logException("login(): "+e.getMessage());
			return Constants.LOGIN_FAILED;
		}
		return "";
	}
	
	/**
	 * This method set's the status for the user. User information is taken from the connection.
	 * 
	 * @param type type of status i.e. 0 (available), 1(busy), 2(away), 3(invisible)
	 * @param status status message of the user
	 */
	public static void setStatus(int type, String status){
		Presence presence = null;
		switch (type){
			case 0: // Available
				presence = new Presence(Presence.Type.available, status, 1, Presence.Mode.available);
				break;
			case 1: // Busy
				presence = new Presence(Presence.Type.available, status, 1, Presence.Mode.dnd);
				break;
			case 2: // AWAY
				presence = new Presence(Presence.Type.available, status, 1, Presence.Mode.away);
				break;
			case 3: // Invisible
				presence = new Presence(Presence.Type.unavailable, status, 1, Presence.Mode.away);
				break;
		}
		if(connection != null && isConnected())
			connection.sendPacket(presence);
	}
	
	/**
	 * This method returns the collection of buddies. This will not check whether its already 
	 * connected or not. Its callers duty to check connection before calling this method.
	 */
	public static Collection<RosterEntry> getBuddyList(){
		final Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		
/*		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logException("getBuddyList(): "+e.getMessage());
		}*/
		
		return entries;
	}
	
	/**
	 * returns the roster for this connection
	 */
	public static Roster getRoster(){
		return connection.getRoster();
	}
	
	/**
	 * Disconnects the user as well as changes the status of user to unavailable
	 */
	public static void disconnect(){
		Presence offlinePres = new Presence(Presence.Type.unavailable, "", 1, Presence.Mode.away);
		
		if(isConnected())
			connection.disconnect(offlinePres);
	}
	
	/**
	 * returns the chat manager
	 */
	public static ChatManager getChatManager(){
		XMPPConnection connection = Util.getConnection();
		return connection.getChatManager();
	}
	
	/**
	 * This method will return the status of the given user
	 * 
	 * @param user
	 * @return -1 for offline, 0 for available, 1 for busy, 2 for away
	 */
	public static int getStatus(String user){
		if(connection != null && !connection.isConnected())
			return -1;
		else if(connection != null){
			Roster roster = connection.getRoster();
			roster.reload();

			Presence presence = roster.getPresence(user);

			if(presence != null){
				if(presence.getType().equals(Presence.Type.available) && presence.getMode() == null)
					return 0;
				if(presence.getType().equals(Presence.Type.available) && presence.getMode() != null && presence.getMode().equals(Presence.Mode.dnd))
					return 1;
				if(presence.getType().equals(Presence.Type.available) && presence.getMode() != null && presence.getMode().equals(Presence.Mode.away))
					return 2;
			}
		}
		return -1;
	}
	
	/**
	 * This method populates the tree view in main view with the users.
	 * 
	 * @param show if true, will show all the friends else it will show 
	 * 			only online friends
	 * @param initialLogin true, if it called immediately after login
	 * 			else it is false
	 */
	public static Collection<RosterEntry> getBuddies(boolean showOffine){
		if(!isConnected())
			return null;
		List<RosterEntry> list = new ArrayList<RosterEntry>();
		
		Collection<RosterEntry> entries = getBuddyList();
		getRoster();
		
		if(showOffine){
			for(RosterEntry r:entries){
					list.add(r);
			}
		}else{		
			for(RosterEntry r:entries){
				if(Util.getStatus(r.getUser()) != -1) // -1 if offline
					list.add(r);
			}
		}
		return list;
	}
	
	/**
	 * Sends message the specified user
	 * 
	 * @param message message to be sent
	 * @param to username to which message to be sent
	 */
	public static void sendMessage(String message, String to){
		Chat chat = Util.getChatManager().createChat(to, new MessageListener() {
		    public void processMessage(final Chat chat, final Message message) {
				if(message.getType() == Message.Type.chat){
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			        	public void run() {
			        	}
					});
				}
		    }
		});
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			logException("sendMessage(): "+e.getMessage());
		}
	}
	
	/**
	 * Returns the RosterEntry by the username
	 * 
	 * @param name username
	 * @return RosterEntry
	 */
	public static RosterEntry getEntryByName(String name){
		return buddiesMap.get(name);
	}
	
	/**
	 * Returns the name of the user from the username
	 * 
	 * @param user username
	 * @return name(if present) else returns username without @gmail.com
	 */
	public static String getNameByUser(String user){
		Collection<RosterEntry> buddies = buddiesMap.values();
		
		Iterator<RosterEntry> itr = buddies.iterator();
		String name = "";
		
		while(itr.hasNext()){
			RosterEntry re = itr.next();
			
			if(re.getUser().equals(user)){
				name = re.getName();
				
				if(name == null)
					name = StringUtils.parseName(re.getUser());
				
				return name;
			}
		}
		return null;
	}
	
	/**
	 * Initializes the buddies
	 */
	public static void initializeBuddies(){
		Collection<RosterEntry> buddies = getBuddyList();
		Iterator<RosterEntry> itr = buddies.iterator();
		
		while(itr.hasNext()){
			RosterEntry re = itr.next();
			String name = re.getName();
			
			if(name == null)
				name = StringUtils.parseName(re.getUser());
			
			buddiesMap.put(name, re);
		}
	}
	
	/**
	 * Returns the StyleRange object for the mentioned argument
	 * 
	 * @param start starting position
	 * @param end	end position
	 * @return StyleRange
	 */
	public static StyleRange getStyleRange(int start, int end){
		StyleRange textStyle = new StyleRange();
		textStyle.start = start;
		textStyle.length = end;
		textStyle.fontStyle = SWT.BOLD;
		
		return textStyle;
	}
	
	public static StyleRange getStyleRangeForTime(Display display, int start, int end){
		Font font = new Font(display, Constants.FONT_ARIAL_7_ITALIC);
		StyleRange textStyle = new StyleRange();
		textStyle.start = start;
		textStyle.length = end;
		textStyle.font = font;
		
		return textStyle;
	}
	
	public static String getStatusFromInt(String user){
		int status = getStatus(user);
		switch (status) {
			case 0:	return Constants.STATUS_AVAILABLE;
			case 1: return Constants.STATUS_BUSY;
			case 2: return Constants.STATUS_AWAY;
			case 3: return Constants.STATUS_INVISIBLE;
			case -1: return "Offline";
		}
		return "";
	}
	
	/**
	 * Method to log the exception to console
	 * 
	 * @param message
	 */
	public static void logException(String message){
		System.out.println("GTALK::EXCEPTION:: "+message);
	}
	
	/**
	 * Returns the chat client the user is using
	 * 
	 * @param from
	 * @return
	 */
	public static String getChatClient(String client){
		String status = "";
		
		// If offline
		if(client.length() == 0)
			return status;
		
		status = Constants.CHAR_OPEN_ROUND_BRACKET;
		
		if(client.startsWith("Talk"))
			status = status + "logged in from google talk";
		else if(client.startsWith("gmail"))
			status = status + "logged in from gmail";
		else if(client.startsWith("android"))
			status = status + "logged in from android";
		else if(client.startsWith("eBuddy"))
			status = status + "logged in from eBuddy";
		else if(client.startsWith("orkut"))
			status = status + "logged in from orkut";
		else if(client.startsWith("Meebo"))
			status = status + "logged in from meebo";
		else if(client.startsWith("ecl-plugin"))
			status = status + "logged in from eclipse plugin";
		else
			status = status + "logged in from other chat client";
		
		status = status + Constants.CHAR_CLOSE_ROUND_BRACKET;
		
		return status;
	}
	
}
