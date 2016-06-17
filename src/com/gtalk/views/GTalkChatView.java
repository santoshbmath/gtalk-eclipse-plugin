package com.gtalk.views;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;

import com.gtalk.GTalkPlugin;
import com.gtalk.ui.LoginDialog;
import com.gtalk.ui.listeners.SendListener;
import com.gtalk.util.Constants;
import com.gtalk.util.Util;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class GTalkChatView extends ViewPart {
	public static final String ID = "com.gtalk.views.mainView";
	
	public Action loginAction;
	private Action logoutAction;
	private Action settingsAction;
	private Action removeAction;
	private Action clearAction;
	private Action doubleClickAction;
	private Action saveAction;
	private static CTabFolder tabFolder;
	private static TableViewer viewer;
	
	private static StyledText chatText;
	private static Text sendText;
	private static Combo statusCombo;
	private static StyledText historyText;
	
	static SendListener keyListener = new SendListener();

	public void createPartControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		
		Composite composite = new Composite(sashForm, SWT.NULL);
        
		GridLayout layout = new GridLayout();
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        composite.setLayout(layout);
        
		statusCombo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		statusCombo.setLayoutData(gridData);
		
		statusCombo.add(Constants.STATUS_AVAILABLE);
		statusCombo.add(Constants.STATUS_BUSY);
		statusCombo.add(Constants.STATUS_AWAY);
		statusCombo.add(Constants.STATUS_INVISIBLE);
		
		statusCombo.select(0);
		
		statusCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();
				Util.setStatus(statusCombo.getSelectionIndex(), store.getString(Constants.STATUS_MSG));
				store.setValue(Constants.STATUS, statusCombo.getSelectionIndex());
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

        final Text searchText = new Text(composite, SWT.SEARCH | SWT.BORDER);
        searchText.setLayoutData(gridData);
        searchText.setText(GTalkPlugin.getString("MainChatView.findFriend"));
        
        final ViewFilter filter = new ViewFilter();
        
        searchText.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				searchText.setText(GTalkPlugin.getString("MainChatView.findFriend"));
				filter.setSearchText("");
				viewer.refresh();	
			}
			
			public void focusGained(FocusEvent e) {
				searchText.setText("");
			}
		});
        
        searchText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				filter.setSearchText(searchText.getText());
				viewer.refresh();				
			}
			
			public void keyPressed(KeyEvent e) {
			}
		});
        
		gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;

		Table table = new Table(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		table.setLayoutData(gridData);
		
		Font font = new Font(table.getDisplay(), Constants.FONT_ARIAL_9);
		table.setFont(font);
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER );
		
		tabFolder.setSimple(false);
		tabFolder.setUnselectedCloseVisible(false);
		
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = (CTabItem)e.item;
				Font font = new Font(item.getDisplay(), Constants.FONT_ARIAL_9);
				item.setFont(font);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		sashForm.setWeights(new int[] {25, 75});
		
		viewer = new TableViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ViewNameSorter());
		viewer.setInput(getViewSite());
		
		viewer.addFilter(filter);
		
		makeActions(parent);
		hookDoubleClickAction();
		contributeToActionBars();
		createDefaultTabItem(GTalkPlugin.getString("MainChatView.firstTab"));
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(removeAction);
		manager.add(clearAction);
		manager.add(new Separator());
		manager.add(saveAction);
		manager.add(new Separator());
		manager.add(settingsAction);
		manager.add(new Separator());
		manager.add(loginAction);
		manager.add(logoutAction);
		manager.add(new Separator());
	}

	@SuppressWarnings("static-access")
	private void makeActions(final Composite composite) {
		removeAction = new Action() {
			public void run() {
				closeAllTabs();
			}
		};
		
		removeAction.setToolTipText(GTalkPlugin.getString("MainChatView.removeTabs"));
		removeAction.setImageDescriptor(GTalkPlugin.getDefault().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
		
		clearAction = new Action() {
			public void run() {
				if(tabFolder.getSelection() != null){
					if(tabFolder.getSelection().getText().equals(GTalkPlugin.getString("MainChatView.firstTab"))){
						historyText.setText("");
					}else{
						((StyledText)getChatTextArea(tabFolder.getSelection().getText(), 1)).setText("");
					}
				}
			}
		};
		
		clearAction.setToolTipText(GTalkPlugin.getString("MainChatView.clearText"));
		clearAction.setImageDescriptor(GTalkPlugin.getDefault().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		
		saveAction = new Action() {
			public void run() {
				if (tabFolder.getSelection() != null) {
					String chatText = ((StyledText)getChatTextArea(tabFolder.getSelection().getText(), 1)).getText();
					
					if(chatText.length() <= 0){
						MessageDialog.openInformation(tabFolder.getShell(), "Save", "There is nothing to save in "+tabFolder.getSelection().getText()+" chat window.");
						return;
					}
					
					DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy-hh-mm");
					FileDialog fd = new FileDialog(tabFolder.getShell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath("C:/");
					fd.setFilterExtensions(new String[]{"*.txt"});
					fd.setFileName(tabFolder.getSelection().getText() + "-" + formatter.format(new Date()) + ".txt");
					
					String selected = fd.open();
					if(selected != null){
						try {
							FileWriter fstream = new FileWriter(selected);
							BufferedWriter out = new BufferedWriter(fstream);
							out.write(chatText);
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		
		saveAction.setToolTipText(GTalkPlugin.getString("MainChatView.saveText"));
		saveAction.setImageDescriptor(GTalkPlugin.getDefault().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		
		final LoginDialog ld = new LoginDialog(composite.getShell());
		loginAction = new Action() {
			public void run() {
				ld.open();
				updateTitle();
			}
		};
		
		loginAction.setToolTipText(GTalkPlugin.getString("MainChatView.loginDesc"));
		loginAction.setImageDescriptor(GTalkPlugin.getDefault().getImageDescriptor(Constants.ICON_CONNECT));
		
		logoutAction = new Action() {
			@SuppressWarnings("deprecation")
			public void run() {				
				Util.disconnect();
				loginAction.setEnabled(true);
				closeAllTabs();
				viewer.refresh();
				setTitle("");
			}
		};
		
		logoutAction.setToolTipText(GTalkPlugin.getString("MainChatView.logoutDesc"));
		logoutAction.setImageDescriptor(GTalkPlugin.getDefault().getImageDescriptor(Constants.ICON_DISCONNECT));
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				
				String currentUser = Util.getCurrentUser();
				
				if(obj.toString().equals(currentUser))
					return;
				
				if(obj.toString().equalsIgnoreCase(Constants.ROOT_FOLDER))
					return;
				
				createTabItem(obj.toString());
			}
		};
		
		settingsAction = new Action() {
			public void run() {
				Shell shell = composite.getShell();	
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(shell, Constants.PREFERENCES_PAGE_ID, null, null);
				dialog.open();
			}
		};

		settingsAction.setToolTipText(GTalkPlugin.getString("MainChatView.prefDesc"));
		settingsAction.setImageDescriptor(GTalkPlugin.getDefault().getImageDescriptor(Constants.ICON_SETTINGS));
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	public static void refreshViewer(){
		viewer.refresh();
	}

	public void setFocus() {
	}
	
	public static Image getImage(String title){
		TableItem tabItems[] = viewer.getTable().getItems();
		Image img = null;
		
		for(TableItem tabItem:tabItems){
			String text = tabItem.getText();
			int len = text.indexOf(Constants.CHAR_OPEN_ROUND_BRACKET);
			
			if(len != -1)
				text = text.substring(0, len);
			
			if(text.equals(title))
				img = tabItem.getImage();
		}
		return img;
	}
		
	/**
	 * Creates new tab item if there is no tab available with the given title.
	 * 
	 * @param title title of the tab
	 */
	private static void createTabItem(String title){
		Color color = new Color(tabFolder.getDisplay(), Constants.COLOR_R_SENDER, Constants.COLOR_G_SENDER, Constants.COLOR_B_SENDER);
		
		TableItem tabItems[] = viewer.getTable().getItems();
		Image img = null;
		
		for(TableItem tabItem:tabItems){
			if(tabItem.getText().equals(title))
				img = tabItem.getImage();
		}
		
		CTabItem items[] = tabFolder.getItems();
		
		int len = title.indexOf(Constants.CHAR_OPEN_ROUND_BRACKET);
		
		if(len != -1)
			title = title.substring(0, len);
		
		for(CTabItem item:items){
			if(item.getText().equals(title)){
				item.setImage(img);
				tabFolder.setSelection(item);
				return;
			}
		}
		
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText(title);
		
		if(img != null)
			tabItem.setImage(img);
		
		Font font = new Font(tabItem.getDisplay(), Constants.FONT_ARIAL_9);
		tabItem.setFont(font);
		
		SashForm sashForm = new SashForm(tabFolder, SWT.VERTICAL);
		tabItem.setControl(sashForm);
		tabItem.setShowClose(true);
		
		chatText = new StyledText(sashForm, SWT.MULTI 
				| SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
		chatText.setBackground(color);
		
		sendText = new Text(sashForm, SWT.BORDER);
		sendText.setEditable(true);
		sendText.setFocus();
		sendText.addKeyListener(keyListener);
		sashForm.setWeights(new int[] {80, 20});
		
		chatText.setFont(font);
		sendText.setFont(font);
		
		tabFolder.setSelection(tabItem);
	}
	
	private static void createDefaultTabItem(String title){
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText(title);
		
		Color color = new Color(tabFolder.getDisplay(), Constants.COLOR_R_SENDER, Constants.COLOR_G_SENDER, Constants.COLOR_B_SENDER);
		
		Font font = new Font(tabItem.getDisplay(), Constants.FONT_ARIAL_9);
		tabItem.setFont(font);
		
		historyText = new StyledText(tabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		historyText.setBackground(color);
		historyText.setEditable(false);
		
		historyText.setFont(font);
		
		tabItem.setControl(historyText);
		tabItem.setShowClose(false);
		
		tabFolder.setSelection(tabItem);
	}
	
	/**
	 * Returns the control (StyledText or Text) of the tab.
	 * 
	 * @param title title of the tab
	 * @param controlType 1 for StyledText and 2 for Text
	 * @return Control
	 */
	public static Control getChatTextArea(String title, int controlType){
		CTabItem items[] = tabFolder.getItems();
		
		for(CTabItem item:items){
			if(item.getText().equals(title)){
				if(title.equals(GTalkPlugin.getString("MainChatView.firstTab"))){
					return item.getControl(); 
				}
				SashForm sf = (SashForm) item.getControl();
				Control controls[] = sf.getTabList();
				for(Control control:controls){
					if(control instanceof StyledText && controlType == 1)
						return control;
					else if(control instanceof Text && controlType == 2)
						return control;
				}
			}
		}
		createTabItem(title);
		return getChatTextArea(title, controlType);
	}
	
	/**
	 * Closes all the open tabs
	 */
	public void closeAllTabs(){
		CTabItem items[] = tabFolder.getItems();
		for(int i = 1; i < items.length; i++){
			CTabItem item = items[i];
			item.dispose();
		}
	}
	
	/**
	 * Updates the title of the view
	 */
	@SuppressWarnings("deprecation")
	public void updateTitle(){
		if(!Util.isConnected())
			return;
		
		IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();

		StringBuffer title = new StringBuffer();
		title.append("Logged in as: "+Util.getCurrentUser());
		
		if(store.getString(Constants.STATUS_MSG).length() != 0)
			title.append(" | Status Message: "+store.getString(Constants.STATUS_MSG));
		
		if(store.getBoolean(Constants.OFFLINE_FRIENDS))
			title.append(" | Showing offline friends");
		else
			title.append(" | Hiding offline friends ");
			
		if(store.getBoolean(Constants.AUTOREPLY))
			title.append(" | Auto reply enabled ("+store.getString(Constants.AUTOREPLY_MSG)+")");
		else
			title.append(" | Auto reply disabled");
			
		setTitle(title.toString());
	}

	/**
	 * Returns the CTabItem from the given title. If TabItem is not present, then it
	 * will create one tabItem and returns.
	 * 
	 * @param title	title of the tab
	 * @return CTabItem
	 */
	public static CTabItem getTabItem(String title) {
		CTabItem items[] = tabFolder.getItems();
		
		for(CTabItem item:items){
			if(item.getText().equals(title)){
				item.setImage(getImage(title));
				return item;
			}
		}
		createTabItem(title);
		return getTabItem(title);	
	}
	
	/**
	 * Returns the CTabItem from the given title. If create is true then tabitem will be created
	 * else no tab item is created.  
	 * 
	 * @param title title of the tab
	 * @param create if true, creates tabItem 
	 * @return CTabItem
	 */
	public static CTabItem getTabItem(String title, boolean create) {
		if(create)
			return getTabItem(title);
		else{
			CTabItem items[] = tabFolder.getItems();
		
			for(CTabItem item:items){
				if(item.getText().equals(title))
					return item;
			}
		}
		return null;
	}
	
/*	public static int getTableIndex(String name){
		Table table = viewer.getTable();
		
		int count = table.getItemCount();
		for(int i=1; i <= count; i++){
			if(table.getItem(i).getText().equals(name))
				return i;
		}
		return -1;
	}
	
	public static void selectItem(int index){
		viewer.getTable().select(index);
	}*/
	
	/**
	 * Updates the status
	 */
	public static void updateCombo(int status){
		statusCombo.select(status);
	}
	
	public static void addToHistory(String user, String message){
		String time = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date());
				
		historyText.append(Constants.CHAR_OPEN_BRACKET + time + Constants.CHAR_CLOSE_BRACKET + Constants.COLON);
		
		int start = historyText.getText().length();
	
		historyText.append(user+" ");
		historyText.append(message);
		historyText.append(Constants.NEW_LINE);
		
		historyText.setStyleRange(Util.getStyleRange(start, user.length()));

		
		// START : Code for auto-scroll
		int top = historyText.getTopIndex();
		int bottom = historyText.getBottomMargin();
		
		if ( start < top + 1 ) {
			historyText.setTopIndex(start - 1 > 0 ? start - 1 : 0);
		} else if ( historyText.getText().length() > bottom -1 ) {
			historyText.setTopIndex(top + start - bottom + 1);
		}
		// END : Code for auto-scroll
	}
	
	public static CTabFolder getTabFolder(){
		return tabFolder;
	}
}
