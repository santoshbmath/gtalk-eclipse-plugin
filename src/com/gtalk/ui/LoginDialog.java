package com.gtalk.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;

import com.gtalk.GTalkPlugin;
import com.gtalk.ui.listeners.BuddyListener;
import com.gtalk.ui.listeners.IncomingMessageListener;
import com.gtalk.util.Constants;
import com.gtalk.util.Util;
import com.gtalk.views.GTalkChatView;

public class LoginDialog extends Dialog {
	private static Text userNameTxt;
	private Text passwordTxt;
	private Text statusText;
	private Combo statusCombo;
	private Label statusLabel;
	
	private GTalkChatView mainView;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(GTalkPlugin.getString("LoginDialog.title"));
	}

	protected Control createDialogArea(Composite parent) {
		IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		mainView = (GTalkChatView) page.findView(GTalkChatView.ID);
		
		Composite container = (Composite) super.createDialogArea(parent);
		
		statusLabel = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 277;
		statusLabel.setLayoutData(gd_lblNewLabel);
		statusLabel.setText(GTalkPlugin.getString("LoginDialog.statusLabel"));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 106;
		gd_composite.widthHint = 278;
		composite.setLayoutData(gd_composite);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText(GTalkPlugin.getString("LoginDialog.userName"));
		
		userNameTxt = new Text(composite, SWT.BORDER);
		userNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		userNameTxt.setText(store.getString(Constants.LOGIN_ID));
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setText(GTalkPlugin.getString("LoginDialog.password"));
		
		passwordTxt = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setText(GTalkPlugin.getString("LoginDialog.status"));
		
		statusCombo = new Combo(composite, SWT.NONE);
		statusCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
		Label lblNewLabel_4 = new Label(composite, SWT.NONE);
		lblNewLabel_4.setText(GTalkPlugin.getString("LoginDialog.statusMessage"));
		
		statusText = new Text(composite, SWT.BORDER);
		statusText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		statusText.setText(store.getString(Constants.STATUS_MSG));
		
		statusCombo.add(Constants.STATUS_AVAILABLE);
		statusCombo.add(Constants.STATUS_BUSY);
		statusCombo.add(Constants.STATUS_AWAY);
		statusCombo.add(Constants.STATUS_INVISIBLE);
		statusCombo.select(0);
		statusCombo.select(store.getInt(Constants.STATUS));

		return container;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Login", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
	}

	protected Point getInitialSize() {
		return new Point(300, 220);
	}
	
	protected void buttonPressed(int buttonId) {
		if(buttonId == Dialog.OK){
			userNameTxt.setEnabled(false);
			passwordTxt.setEnabled(false);
			
			statusLabel.setText(Constants.CONNECTING);
			String user = "";
				
			if(userNameTxt.getText().endsWith(Constants.CHAR_AT + Constants.GMAIL_DOT_COM))
				user = userNameTxt.getText();
			else
				user = userNameTxt.getText() + Constants.CHAR_AT + Constants.GMAIL_DOT_COM;
			
			String status = Util.connect();
			statusLabel.setText(status);
			mainView.loginAction.setEnabled(false);
			
			storeValues();
			
			if(Util.isConnected())
				status = Util.login(user, passwordTxt.getText());
			
			statusLabel.setText(status);
			
			Util.setStatus(statusCombo.getSelectionIndex(), statusText.getText());
			
			if(status.equals(Constants.LOGIN_FAILED) || status.equals(Constants.CONNECTION_FAILED)){
				userNameTxt.setEnabled(true);
				passwordTxt.setEnabled(true);
				
				Util.disconnect();
				mainView.loginAction.setEnabled(true);
				return;
			}
			
			GTalkChatView.updateCombo(statusCombo.getSelectionIndex());

			GTalkChatView.refreshViewer();
			close();
			
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            Util.getConnection().addPacketListener(new IncomingMessageListener(), filter);
            
            Roster roster = Util.getRoster();        
            roster.addRosterListener(new BuddyListener());
		}else if(buttonId == Dialog.CANCEL){
			close();
		}
	}
	
	private void storeValues() {
		IPreferenceStore store = GTalkPlugin.getDefault().getPreferenceStore();
		store.setValue(Constants.STATUS, statusCombo.getSelectionIndex());
		store.setValue(Constants.STATUS_MSG, statusText.getText());
		store.setValue(Constants.LOGIN_ID, userNameTxt.getText());
	}
}
