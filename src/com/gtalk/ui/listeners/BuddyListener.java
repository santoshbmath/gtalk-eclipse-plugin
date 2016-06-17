package com.gtalk.ui.listeners;

import java.util.Collection;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.ui.PlatformUI;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.gtalk.util.Util;
import com.gtalk.views.GTalkChatView;

/**
 * Listener class which listens to the buddies presence
 * 
 * @author maths
 *
 */
public class BuddyListener implements RosterListener {

	public void entriesAdded(Collection<String> arg0) {
	}

	public void entriesDeleted(Collection<String> arg0) {
	}

	public void entriesUpdated(Collection<String> arg0) {
	}

	public void presenceChanged(final Presence arg0) {
		final String name = StringUtils.parseName(arg0.getFrom());
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try{
						GTalkChatView.refreshViewer();

						CTabItem item = GTalkChatView.getTabItem(name, false);
						
						String status = arg0.getStatus();
						
						String message = " has become "+Util.getStatusFromInt(arg0.getFrom());
						
						if(status != null && status.length() > 0)
							message = message + " and says (" + status +")";
						
						GTalkChatView.addToHistory(Util.getNameByUser(StringUtils.parseBareAddress(arg0.getFrom())), message);
						
						if(item != null)
							item.setImage(GTalkChatView.getImage(name));
					}catch(Exception e){
						Util.logException("BuddyListener.presenceChanged():"+e.getMessage());
					}
				}
    	});
	}
}
