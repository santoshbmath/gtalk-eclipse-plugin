package com.gtalk.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jivesoftware.smack.RosterEntry;

import com.gtalk.GTalkPlugin;
import com.gtalk.util.Constants;
import com.gtalk.util.Util;

class ViewLabelProvider extends LabelProvider {
	private static GTalkPlugin plugin = GTalkPlugin.getDefault();

	public String getText(Object obj) {
		return obj.toString();
	}
	public Image getImage(Object obj) {	
		String name = obj.toString();
		int len = name.indexOf(Constants.CHAR_OPEN_ROUND_BRACKET);
		
		if(len != -1)
			name = name.substring(0, len);
		
		RosterEntry entry = Util.getEntryByName(name);
		
		int status = entry != null ? Util.getStatus(entry.getUser()): -1;

		switch (status){
			case 0:
				return plugin.getImage(Constants.ICON_AVAILABLE);
			case 1:
				return plugin.getImage(Constants.ICON_BUSY);
			case 2:
				return plugin.getImage(Constants.ICON_AWAY);
			case -1:
				return plugin.getImage(Constants.ICON_OFFLINE);
		}
		return plugin.getImage(Constants.ICON_OFFLINE);
	}

}

