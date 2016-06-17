package com.gtalk.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import com.gtalk.util.Constants;
import com.gtalk.util.Util;

public class ViewNameSorter extends ViewerSorter {		
	public int compare(Viewer viewer, Object e1, Object e2) {
		XMPPConnection connection = Util.getConnection();
		if(connection != null){
			Roster roster = connection.getRoster();
			
			if(roster == null)
				return -1;
			
			String name1 = e1.toString();
			String name2 = e2.toString();
			
			int len1 = name1.indexOf(Constants.CHAR_OPEN_ROUND_BRACKET);
			int len2 = name2.indexOf(Constants.CHAR_OPEN_ROUND_BRACKET);
			
			if(len1 != -1)
				name1 = name1.substring(0, len1);
			
			if(len2 != -1)
				name2 = name2.substring(0, len2);
				
			RosterEntry re1 = Util.getEntryByName(name1);
			RosterEntry re2 = Util.getEntryByName(name2);
			
			Presence presence1 = roster.getPresence(re1 != null ? re1.getUser() : "");
			Presence presence2 = roster.getPresence(re2 != null ? re2.getUser() : "");
			
			Presence.Mode mode1 = presence1.getMode();
			Presence.Mode mode2 = presence2.getMode();
			
			if(mode1 != null && mode2 != null){
				boolean mode1Available = mode1.equals(Presence.Mode.available);
				boolean mode2Available = mode2.equals(Presence.Mode.available);
				
				boolean mode1DND = mode1.equals(Presence.Mode.dnd);
				boolean mode2DND = mode2.equals(Presence.Mode.dnd);
				
				boolean mode1Away = mode1.equals(Presence.Mode.away);
				boolean mode2Away = mode2.equals(Presence.Mode.away);
				
				if(mode1Available && mode2Available) 
					return 0;
				else if (mode1DND && mode2DND)
					return 0;
				else if(mode1Away && mode2Away)
					return 0;
				else if(mode1Available && mode2DND)
					return 1;
				else if(mode1Available && mode2Away)
					return 1;
				else if(mode2Available && mode1DND)
					return -1;
				else if(mode2Available && mode1Away)
					return -1;
				else if(mode1Away && mode2DND)
					return -1;
				else if(mode2Away && mode1DND)
					return 1;
			}
			else if(mode2 == null)
				return 1;
			else if(mode1 == null)
				return -1;
		}
		return 1;
	}
}
