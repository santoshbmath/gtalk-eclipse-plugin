package com.gtalk.ui.listeners;

import java.text.DateFormat;	
import java.util.Date;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.gtalk.GTalkPlugin;
import com.gtalk.util.Constants;
import com.gtalk.util.Util;
import com.gtalk.views.GTalkChatView;

/**
 * Listener class for the incoming message from the buddies
 * 
 * @author maths
 *
 */
public class IncomingMessageListener implements PacketListener {
	public void processPacket(Packet packet) {
		final Message message = (Message) packet;
		if (message.getBody() != null) {			
			final String fromName = Util.getNameByUser(StringUtils.parseBareAddress(message.getFrom()));
			final String recievedMessage = fromName + Constants.COLON + message.getBody();

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {			
						IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();
						if(store.getBoolean(Constants.AUTO_OPEN)){
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(GTalkChatView.ID);
							} catch (PartInitException e1) {
								Util.logException("IncomingMessageListener.processPacket():"+e1.getMessage());
								e1.printStackTrace();
							}
						}
						
						String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
						StyledText chatText = (StyledText) GTalkChatView.getChatTextArea(fromName, 1);
						
						CTabItem item = GTalkChatView.getTabItem(fromName);
						CTabItem itemSelected = item.getParent().getSelection();
						
						if(!item.getText().equals(itemSelected.getText())){
							Font font = new Font(item.getDisplay(), Constants.FONT_ARIAL_9_BOLD);
							item.setFont(font);
						}

						int start = chatText.getText().length();

						chatText.append(recievedMessage);
						int timeStart = chatText.getText().length();
						chatText.append(Constants.CHAR_OPEN_BRACKET + time + Constants.CHAR_CLOSE_BRACKET);
						int timeEnd = time.length() + 2;
						chatText.append(Constants.NEW_LINE);						
						
						chatText.setStyleRange(Util.getStyleRangeForTime(chatText.getDisplay(), timeStart, timeEnd));
						chatText.setStyleRange(Util.getStyleRange(start, fromName.length()+1));
						
						boolean autoReply = store.getBoolean(Constants.AUTOREPLY);
						
						if(autoReply){ // If auto reply is selected
							String msg = store.getString(Constants.AUTOREPLY_MSG);

							start = chatText.getText().length();
							chatText.append("me" + Constants.COLON + msg);
							chatText.append(Constants.CHAR_OPEN_BRACKET + time + Constants.CHAR_CLOSE_BRACKET);
							chatText.append(Constants.NEW_LINE);						
							
							chatText.setStyleRange(Util.getStyleRange(start, 3));

							Util.sendMessage(msg, message.getFrom());
						}	
						
						// START : Code for auto-scroll
						int top = chatText.getTopIndex();
						int bottom = chatText.getBottomMargin();
						
						if ( start < top + 1 ) {
							chatText.setTopIndex(start - 1 > 0 ? start - 1 : 0);
						} else if ( chatText.getText().length() > bottom -1 ) {
							chatText.setTopIndex(top + start - bottom + 1);
						}
						// END : Code for auto-scroll
					} catch (Exception e) {
						Util.logException("IncomingMessageListener.processPacket():"+e.getMessage());
						e.printStackTrace();
					}
				}
			});
		}
	}
}
