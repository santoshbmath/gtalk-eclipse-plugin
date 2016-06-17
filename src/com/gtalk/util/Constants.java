package com.gtalk.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * This class lists all the constants
 */
public class Constants {
	// Constants
	public static String CHAR_AT = "@";
	public static String CHAR_OPEN_ROUND_BRACKET = "    (";
	public static String CHAR_CLOSE_ROUND_BRACKET = ")";
	public static String ROOT_FOLDER = "Not logged in";
	public static String NEW_LINE = "\r\n";
	public static String COLON = ": ";
	public static String CHAR_OPEN_BRACKET = " [";
	public static String CHAR_CLOSE_BRACKET = "] ";
	
	// Font styles
	public static FontData FONT_ARIAL_9 = new FontData("Arial", 9, SWT.NONE);
	public static FontData FONT_ARIAL_9_BOLD = new FontData("Arial", 9, SWT.BOLD);
	public static FontData FONT_ARIAL_7_ITALIC = new FontData("Arial", 7, SWT.ITALIC);
	
	// Icons
	public static String ICON_AWAY = "icons//idle.png";
	public static String ICON_AVAILABLE = "icons//available.png";
	public static String ICON_BUSY = "icons//busy.png";
	public static String ICON_OFFLINE = "icons//offline.png";
	public static String ICON_CONNECT = "icons//connect.ico";
	public static String ICON_DISCONNECT = "icons//disconnect.ico";
	public static String ICON_SETTINGS = "icons//settings.ico";
	public static String ICON_CHAT = "icons//chat.ico";
	
	// Enter
	public static int    KEYPAD_ENTER = 13;
	public static int    NUMPAD_ENTER = 16777296;
	
	// Status messages
	public static String STATUS_AVAILABLE = "Available";
	public static String STATUS_BUSY = "Busy";
	public static String STATUS_AWAY = "Away";
	public static String STATUS_INVISIBLE = "Invisible";
	
	// Gmail server related constants
	public static String GMAIL_DOT_COM = "gmail.com";
	public static int    GMAIL_PORT = 5222;
	public static String GMAIL_HOST = "talk.google.com";
	
	// Connection status
	public static String CONNECTING = "Connecting to server...";
	public static String CONNECTION_SUCCESSFULL = "Connected. Now signing in...";
	public static String CONNECTION_FAILED = "Connection failed. Check your internet/proxy settings.";
	public static String LOGIN_FAILED = "Login Failed. Check your username/password.";
	
	// Preferences page store 
	public static String AUTOREPLY = "autoReply";
	public static String AUTOREPLY_MSG = "autoReplyMessage";
	public static String STATUS = "status";
	public static String STATUS_MSG = "statusMessage";
	public static String OFFLINE_FRIENDS = "showOfflineFriends";
	public static String AUTO_OPEN = "autoOpenWindow";
	public static String SHOW_CHAT_CLIENT = "showChatClient";
	public static String ENABLE_PROXY = "enableProxy";
	public static String PROXY_HOST = "proxyHost";
	public static String PROXY_PORT = "proxyPort";
	public static String REQUIRE_AUTH = "requireAuth";
	public static String AUTH_USER = "authUser";
	public static String AUTH_PWD = "authPwd";
	public static String LOGIN_ID = "loginID";
	
	// Id's
	public static String PREFERENCES_PAGE_ID = "com.gtalk.ui.preferencePage";
	
	// COLOR (RGB) COMBINATION
	public static int COLOR_R_SENDER = 240;
	public static int COLOR_G_SENDER = 248;
	public static int COLOR_B_SENDER = 255;
}
