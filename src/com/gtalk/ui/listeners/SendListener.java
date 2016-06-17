package com.gtalk.ui.listeners;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;
import org.jivesoftware.smack.RosterEntry;

import com.gtalk.util.Constants;
import com.gtalk.util.Util;
import com.gtalk.views.GTalkChatView;

public class SendListener implements KeyListener {

	public void keyPressed(KeyEvent e) {
		if(e.keyCode == Constants.KEYPAD_ENTER || e.keyCode == Constants.NUMPAD_ENTER){
			CTabFolder tabFolder = GTalkChatView.getTabFolder();
			StyledText chatText = (StyledText) GTalkChatView.getChatTextArea(tabFolder.getSelection().getText(), 1);
			Text sendText = (Text) GTalkChatView.getChatTextArea(tabFolder.getSelection().getText(), 2);
			
			if(sendText.getText().trim().length() == 0)
				return;
		    
			String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

			int startLineCount = chatText.getLineCount() - 1;

			int start = chatText.getText().length();
			chatText.append("me" + Constants.COLON + sendText.getText()); 

			int timeStart = chatText.getText().length();
			chatText.append(Constants.CHAR_OPEN_BRACKET + time + Constants.CHAR_CLOSE_BRACKET);
			
			int timeEnd = time.length() + 2;
			chatText.append(Constants.NEW_LINE);
			
			int endLineCount = chatText.getLineCount() - 1;
			
		    chatText.setLineBackground(startLineCount, endLineCount - startLineCount, chatText.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			chatText.setStyleRange(Util.getStyleRangeForTime(chatText.getDisplay(), timeStart, timeEnd));
			chatText.setStyleRange(Util.getStyleRange(start, 3));
		    
			// START : Code for auto-scroll
			int top = chatText.getTopIndex();
			int bottom = chatText.getBottomMargin();
			
			if ( start < top + 1 ) {
				chatText.setTopIndex(start - 1 > 0 ? start - 1 : 0);
			} else if ( chatText.getText().length() > bottom -1 ) {
				chatText.setTopIndex(top + start - bottom + 1);
			}
			// END : Code for auto-scroll
			
			RosterEntry re = Util.getEntryByName(tabFolder.getSelection().getText());
			
			Util.sendMessage(sendText.getText(), re.getUser());
			sendText.setText("");
		}
	}

	public void keyReleased(KeyEvent e) {
	}
}
