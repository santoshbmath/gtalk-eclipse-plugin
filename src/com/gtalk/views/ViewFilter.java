package com.gtalk.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.gtalk.util.Constants;

public class ViewFilter extends ViewerFilter {
	private String searchString;

	public void setSearchText(String s) {
		this.searchString = s.toLowerCase(); // If you use  matches, then change this to "*."+s+".*"
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		
		String name = element.toString().toLowerCase();
		int len = name.indexOf(Constants.CHAR_OPEN_ROUND_BRACKET);
		
		if(len != -1)
			name = name.substring(0, len);
		
		if(name.contains(searchString))
			return true;
		
		return false;
	}

}
