package com.gtalk.views;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.gtalk.GTalkPlugin;
import com.gtalk.util.Constants;
import com.gtalk.util.Util;

public class ViewContentProvider implements IStructuredContentProvider {

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		if(Util.isConnected()){
			IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();
			
			Collection<RosterEntry> buddies = Util.getBuddies(store.getBoolean(Constants.OFFLINE_FRIENDS));
			String s[] = new String[buddies.size()];
			
			Iterator<RosterEntry> itr = buddies.iterator();
			int i = 0;
			while(itr.hasNext()){
				RosterEntry re = itr.next();
				String name = re.getName();

				if(name == null)
					name = StringUtils.parseName(re.getUser());
				
				Presence p = Util.getRoster().getPresence(re.getUser());
				
				if(p != null && p.getStatus() != null && p.getStatus() != "")
					name = name + Constants.CHAR_OPEN_ROUND_BRACKET + p.getStatus() + Constants.CHAR_CLOSE_ROUND_BRACKET;

				if(store.getBoolean(Constants.SHOW_CHAT_CLIENT))
					name = name + Util.getChatClient(StringUtils.parseResource(p.getFrom()));
				
				s[i++] = name;
			}
			
			return s;
		}
		return new Object[0];	
	}
}