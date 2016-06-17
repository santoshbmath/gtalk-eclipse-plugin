package com.gtalk.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.gtalk.GTalkPlugin;
import com.gtalk.util.Constants;
import com.gtalk.util.Util;
import com.gtalk.views.GTalkChatView;

public class GTalkPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Button autoReplyChkBox;
	private Text replyText;
	private Combo statusCombo;
	private Text statusText;
	private Button showOfflineFriendsChkBox;
	private Button autoOpenChatWindowChkBox;
	private Button showChatClientChkBox;
	
	private Button enableProxyChkBox;
	private Button requiresAuthChkBox;
	private Text hostText;
	private Text portText;
	private Text userText;
	private Text pwdText;
	
	public GTalkPreferencePage() {
	}

	public GTalkPreferencePage(String title) {
		super(title);
	}

	public GTalkPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected Control createContents(Composite parent) {
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 8;
		parent.setLayout(gridLayout);

		noDefaultAndApplyButton();
		
		createOptions(parent);
		createProxyArea(parent);
		createStatusArea(parent);
		createAutoReplyArea(parent);
		
		return parent;
	}

	public void init(IWorkbench workbench) {
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return GTalkPlugin.getDefault().getPreferenceStore();
	}
	
	public boolean performOk() {
		storeValues();
		GTalkChatView.refreshViewer();
		GTalkChatView.updateCombo(statusCombo.getSelectionIndex());
		
		GTalkChatView part = (GTalkChatView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(GTalkChatView.ID);
		part.updateTitle();
		
		return true;
	}
		
	private void storeValues() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(Constants.AUTOREPLY, autoReplyChkBox.getSelection());
		store.setValue(Constants.AUTOREPLY_MSG, replyText.getText());
		store.setValue(Constants.STATUS, statusCombo.getSelectionIndex());
		store.setValue(Constants.STATUS_MSG, statusText.getText());
		store.setValue(Constants.OFFLINE_FRIENDS, showOfflineFriendsChkBox.getSelection());
		store.setValue(Constants.AUTO_OPEN, autoOpenChatWindowChkBox.getSelection());
		store.setValue(Constants.SHOW_CHAT_CLIENT, showChatClientChkBox.getSelection());
		store.setValue(Constants.ENABLE_PROXY, enableProxyChkBox.getSelection());
		store.setValue(Constants.PROXY_HOST, hostText.getText());
		store.setValue(Constants.PROXY_PORT, portText.getText());
		store.setValue(Constants.REQUIRE_AUTH, requiresAuthChkBox.getSelection());
		store.setValue(Constants.AUTH_USER, userText.getText());
		store.setValue(Constants.AUTH_PWD, pwdText.getText());
		
		Util.setStatus(statusCombo.getSelectionIndex(), statusText.getText());
		autoReplyChkBox.setSelection(autoReplyChkBox.getSelection());
		replyText.setText(replyText.getText());
		
		statusCombo.select(statusCombo.getSelectionIndex());
		statusText.setText(statusText.getText());
	}


	private void createProxyArea(final Composite parent) {
		Group dirgroup = new Group(parent, SWT.NONE);
		dirgroup.setLayout(new GridLayout(2, true));
		dirgroup.setText(GTalkPlugin.getString("PreferencesPage.group2.title"));

		GridData dirgriddata = new GridData(GridData.FILL_HORIZONTAL);
		dirgriddata.horizontalSpan = 2;
		dirgroup.setLayoutData(dirgriddata);

		GridLayout dirLayout = new GridLayout();
		dirLayout.numColumns = 4;
		dirgroup.setLayout(dirLayout);

		enableProxyChkBox = new Button(dirgroup, SWT.CHECK);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		enableProxyChkBox.setData(gridData);
		enableProxyChkBox.setText(GTalkPlugin.getString("PreferencesPage.group2.enableProxy"));
		enableProxyChkBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(Util.isConnected()){
					MessageDialog.openInformation(parent.getShell(), GTalkPlugin.getString("PreferencesPage.group2.dialog.title"), GTalkPlugin.getString("PreferencesPage.group2.dialog.desc"));
				}
				if(enableProxyChkBox.getSelection()){
					hostText.setEnabled(true);
					portText.setEnabled(true);
				}else{
					hostText.setEnabled(false);
					portText.setEnabled(false);
				}
					
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		enableProxyChkBox.setSelection(getPreferenceStore().getBoolean(Constants.ENABLE_PROXY));
		
		boolean result = enableProxyChkBox.getSelection();
		
		requiresAuthChkBox = new Button(dirgroup, SWT.CHECK);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		requiresAuthChkBox.setText(GTalkPlugin.getString("PreferencesPage.group2.requiresAuth"));
		requiresAuthChkBox.setData(gridData);
		requiresAuthChkBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(Util.isConnected()){
					MessageDialog.openInformation(parent.getShell(), GTalkPlugin.getString("PreferencesPage.group2.dialog.title"), GTalkPlugin.getString("PreferencesPage.group2.dialog.desc"));
				}
				if(requiresAuthChkBox.getSelection()){
					userText.setEnabled(true);
					pwdText.setEnabled(true);
				}else{
					userText.setEnabled(false);
					pwdText.setEnabled(false);
				}
					
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		}); 
		requiresAuthChkBox.setSelection(getPreferenceStore().getBoolean(Constants.REQUIRE_AUTH));
		
		boolean result1 = requiresAuthChkBox.getSelection();
		
		new Label(dirgroup, SWT.NONE).setText("");
		new Label(dirgroup, SWT.NONE).setText("");
		
		Label nameLabel = new Label(dirgroup, SWT.NONE);
		gridData = new GridData();
		nameLabel.setData(gridData);
		nameLabel.setText(GTalkPlugin.getString("PreferencesPage.group2.hostLabel"));

		hostText = new Text(dirgroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.WRAP);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		hostText.setLayoutData(gridData);
		hostText.setText(getPreferenceStore().getString(Constants.PROXY_HOST));

		Label nameLabel1 = new Label(dirgroup, SWT.NONE);
		gridData = new GridData();
		nameLabel1.setData(gridData);
		nameLabel1.setText(GTalkPlugin.getString("PreferencesPage.group2.portLabel"));

		portText = new Text(dirgroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.WRAP);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		portText.setLayoutData(gridData);
		portText.setText(getPreferenceStore().getString(Constants.PROXY_PORT));
		
		Label userLabel = new Label(dirgroup, SWT.NONE);
		gridData = new GridData();
		userLabel.setData(gridData);
		userLabel.setText(GTalkPlugin.getString("PreferencesPage.group2.userLabel"));

		userText = new Text(dirgroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.WRAP);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		userText.setLayoutData(gridData);
		userText.setText(getPreferenceStore().getString(Constants.AUTH_USER));

		Label pwdLabel = new Label(dirgroup, SWT.NONE);
		gridData = new GridData();
		pwdLabel.setData(gridData);
		pwdLabel.setText(GTalkPlugin.getString("PreferencesPage.group2.pwdLabel"));

		pwdText = new Text(dirgroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.WRAP);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		pwdText.setLayoutData(gridData);
		pwdText.setText(getPreferenceStore().getString(Constants.AUTH_PWD));
		
		if(!result){
			hostText.setEnabled(result);
			portText.setEnabled(result);
		}
		
		if(!result1){
			userText.setEnabled(result1);
			pwdText.setEnabled(result1);
		}

	}
	
	private void createAutoReplyArea(Composite parent) {
		Group dirgroup = new Group(parent, SWT.NONE);
		dirgroup.setLayout(new GridLayout(2, true));
		dirgroup.setText(GTalkPlugin.getString("PreferencesPage.group.title"));

		GridData dirgriddata = new GridData(GridData.FILL_HORIZONTAL);
		dirgriddata.horizontalSpan = 4;

		dirgroup.setLayoutData(dirgriddata);

		GridLayout dirLayout = new GridLayout();
		dirLayout.numColumns = 2;
		dirgroup.setLayout(dirLayout);

		autoReplyChkBox = new Button(dirgroup, SWT.CHECK);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		autoReplyChkBox.setText(GTalkPlugin.getString("PreferencesPage.group.autoReply"));
		autoReplyChkBox.setData(gridData);
		autoReplyChkBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				if (autoReplyChkBox.getSelection())
					replyText.setEnabled(true);
				else
					replyText.setEnabled(false);
			}
		});
		autoReplyChkBox.setSelection(getPreferenceStore().getBoolean(Constants.AUTOREPLY));
		
		new Label(dirgroup, SWT.NONE).setText("");

		Label nameLabel = new Label(dirgroup, SWT.NONE);
		gridData = new GridData();
		nameLabel.setData(gridData);
		nameLabel.setText(GTalkPlugin.getString("PreferencesPage.group.autoReplyMsg"));

		replyText = new Text(dirgroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.WRAP);
		GridData data7 = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		data7.heightHint = 40;
		data7.widthHint = 260;
		replyText.setLayoutData(data7);
		replyText.setText(getPreferenceStore().getString(Constants.AUTOREPLY_MSG));
		
		if(autoReplyChkBox.getSelection())
			replyText.setEnabled(true);
		else
			replyText.setEnabled(false);
	}

	private void createStatusArea(Composite parent) {
		Group dirgroup = new Group(parent, SWT.NONE);
		dirgroup.setLayout(new GridLayout(2, true));
		dirgroup.setText(GTalkPlugin.getString("PreferencesPage.group1.title"));

		GridData dirgriddata = new GridData(GridData.FILL_HORIZONTAL);
		dirgriddata.horizontalSpan = 4;

		dirgroup.setLayoutData(dirgriddata);

		GridLayout dirLayout = new GridLayout();
		dirLayout.numColumns = 2;
		dirgroup.setLayout(dirLayout);

		new Label(dirgroup, SWT.NONE).setText(GTalkPlugin.getString("PreferencesPage.group1.status"));

		statusCombo = new Combo(dirgroup, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		statusCombo.add(Constants.STATUS_AVAILABLE);
		statusCombo.add(Constants.STATUS_BUSY);
		statusCombo.add(Constants.STATUS_AWAY);
		statusCombo.add(Constants.STATUS_INVISIBLE);
		//statusCombo.select(0);
		statusCombo.select(getPreferenceStore().getInt(Constants.STATUS));

		Label nameLabel = new Label(dirgroup, SWT.NONE);
		GridData gridData = new GridData();
		nameLabel.setData(gridData);
		nameLabel.setText(GTalkPlugin.getString("PreferencesPage.group1.statusMsg"));

		statusText = new Text(dirgroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.WRAP);
		GridData data7 = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		data7.heightHint = 40;
		data7.widthHint = 260;
		statusText.setLayoutData(data7);
		statusText.setText(getPreferenceStore().getString(Constants.STATUS_MSG));
	}
	
	private void createOptions(Composite parent){
		GridData dirgriddata = new GridData(GridData.FILL_HORIZONTAL);
		dirgriddata.horizontalSpan = 4;

		parent.setLayoutData(dirgriddata);

		GridLayout dirLayout = new GridLayout();
		dirLayout.numColumns = 1;
		parent.setLayout(dirLayout);

		showOfflineFriendsChkBox = new Button(parent, SWT.CHECK);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		showOfflineFriendsChkBox.setText(GTalkPlugin.getString("PreferencesPage.0"));
		showOfflineFriendsChkBox.setData(gridData);
		showOfflineFriendsChkBox.setSelection(getPreferenceStore().getBoolean(Constants.OFFLINE_FRIENDS));
		
		autoOpenChatWindowChkBox = new Button(parent, SWT.CHECK);
		GridData gridData1 = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		autoOpenChatWindowChkBox.setText(GTalkPlugin.getString("PreferencesPage.1"));
		autoOpenChatWindowChkBox.setData(gridData1);
		autoOpenChatWindowChkBox.setSelection(getPreferenceStore().getBoolean(Constants.AUTO_OPEN));
		
		showChatClientChkBox = new Button(parent, SWT.CHECK);
		GridData gridData2 = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		showChatClientChkBox.setText(GTalkPlugin.getString("PreferencesPage.2"));
		showChatClientChkBox.setData(gridData2);
		showChatClientChkBox.setSelection(getPreferenceStore().getBoolean(Constants.SHOW_CHAT_CLIENT));
	}
}
