package de.msk.mylivetracker.client.android.remoteaccess;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * RemoteAccessPrefsActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 002
 * 
 * history
 * 002	2012-12-25 	revised for v1.5.x.
 * 001  2012-02-18 	phoneNumber added.
 * 000 	2011-08-11 	initial.
 * 
 */
public class RemoteAccessPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private RemoteAccessPrefsActivity activity;
		private CheckBox cbRemoteAccessPrefs_Enabled;
		private EditText etRemoteAccessPrefs_Password;
		private CheckBox cbRemoteAccessPrefs_UseReceiver;
		private EditText etRemoteAccessPrefs_Receiver;
		
		public OnClickButtonSaveListener(
			RemoteAccessPrefsActivity activity,
			CheckBox cbRemoteAccessPrefs_Enabled,
			EditText etRemoteAccessPrefs_Password,
			CheckBox cbRemoteAccessPrefs_UseReceiver,
			EditText etRemoteAccessPrefs_Receiver) {
			this.activity = activity;
			this.cbRemoteAccessPrefs_Enabled = cbRemoteAccessPrefs_Enabled;
			this.etRemoteAccessPrefs_Password = etRemoteAccessPrefs_Password;
			this.cbRemoteAccessPrefs_UseReceiver = cbRemoteAccessPrefs_UseReceiver;
			this.etRemoteAccessPrefs_Receiver = etRemoteAccessPrefs_Receiver;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
			
			boolean remoteAccessEnabled = cbRemoteAccessPrefs_Enabled.isChecked();
			boolean useReceiver = cbRemoteAccessPrefs_UseReceiver.isChecked();
			
			if (remoteAccessEnabled) {
				valid = 
					ValidatorUtils.validateEditTextString(
						this.activity, 
						R.string.fdRemoteAccessPrefs_Password, 
						etRemoteAccessPrefs_Password, 
						4, 8, true);
				if (valid && useReceiver) {
					valid = ValidatorUtils.validateIfPhoneNumber(
						this.activity, 
						R.string.fdRemoteAccessPrefs_Receiver, 
						etRemoteAccessPrefs_Receiver, 
						true);	
				}
			}
			
			if (valid) {
				RemoteAccessPrefs prefs = PrefsRegistry.get(RemoteAccessPrefs.class);
				prefs.setRemoteAccessEnabled(cbRemoteAccessPrefs_Enabled.isChecked());
				if (cbRemoteAccessPrefs_Enabled.isChecked()) {
					prefs.setRemoteAccessPassword(etRemoteAccessPrefs_Password.getText().toString());
				} else {
					prefs.setRemoteAccessPassword("");
				}
				prefs.setRemoteAccessUseReceiver(cbRemoteAccessPrefs_UseReceiver.isChecked());
				if (cbRemoteAccessPrefs_UseReceiver.isChecked()) {
					prefs.setRemoteAccessReceiver(etRemoteAccessPrefs_Receiver.getText().toString());
				} else {
					prefs.setRemoteAccessReceiver("");
				}
				PrefsRegistry.save(RemoteAccessPrefs.class);
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private RemoteAccessPrefsActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(RemoteAccessPrefsActivity activity) {
			this.activity = activity;
		}
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_access_prefs);
     
        this.setTitle(R.string.tiRemoteAccessPrefs);
        
        RemoteAccessPrefs prefs = PrefsRegistry.get(RemoteAccessPrefs.class);
        
        CheckBox cbRemoteAccessPrefs_Enable = (CheckBox)findViewById(R.id.cbRemoteAccessPrefs_Enable);
        cbRemoteAccessPrefs_Enable.setChecked(prefs.isRemoteAccessEnabled());
        EditText etPrefsRemote_Password = (EditText) findViewById(R.id.etRemoteAccessPrefs_Password);
        etPrefsRemote_Password.setText(String.valueOf(prefs.getRemoteAccessPassword()));
        
        CheckBox cbPrefsRemote_UseReceiver = (CheckBox)findViewById(R.id.cbRemoteAccessPrefs_UseReceiver);
        cbPrefsRemote_UseReceiver.setChecked(prefs.isRemoteAccessUseReceiver());
        EditText etPrefsRemote_Receiver = (EditText) findViewById(R.id.etRemoteAccessPrefs_Receiver);
        etPrefsRemote_Receiver.setText(String.valueOf(prefs.getRemoteAccessReceiver()));
        
        Button btnRemoteAccessPrefs_Save = (Button) findViewById(R.id.btRemoteAccessPrefs_Save);
        Button btnRemoteAccessPrefs_Cancel = (Button) findViewById(R.id.btRemoteAccessPrefs_Cancel);
                
        btnRemoteAccessPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				cbRemoteAccessPrefs_Enable,
				etPrefsRemote_Password,
				cbPrefsRemote_UseReceiver,
				etPrefsRemote_Receiver));
		
        btnRemoteAccessPrefs_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
