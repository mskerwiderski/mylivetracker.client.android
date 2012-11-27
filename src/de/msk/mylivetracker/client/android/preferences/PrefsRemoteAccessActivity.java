package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PrefsAccountActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001  2012-02-18 phoneNumber added.
 * 000 	2011-08-11 initial.
 * 
 */
public class PrefsRemoteAccessActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsRemoteAccessActivity activity;
		private Preferences preferences;
		private CheckBox cbPrefsRemoteAccess_Enabled;
		private EditText etPrefsRemoteAccess_Password;
		private CheckBox cbPrefsRemoteAccess_UseReceiver;
		private EditText etPrefsRemoteAccess_Receiver;
		
		public OnClickButtonSaveListener(
			PrefsRemoteAccessActivity activity,
			Preferences preferences, 
			CheckBox cbPrefsRemoteAccess_Enabled,
			EditText etPrefsRemoteAccess_Password,
			CheckBox cbPrefsRemoteAccess_UseReceiver,
			EditText etPrefsRemoteAccess_Receiver) {
			this.activity = activity;
			this.preferences = preferences;
			this.cbPrefsRemoteAccess_Enabled = cbPrefsRemoteAccess_Enabled;
			this.etPrefsRemoteAccess_Password = etPrefsRemoteAccess_Password;
			this.cbPrefsRemoteAccess_UseReceiver = cbPrefsRemoteAccess_UseReceiver;
			this.etPrefsRemoteAccess_Receiver = etPrefsRemoteAccess_Receiver;
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			boolean valid = true;
			
			boolean remoteAccessEnabled = cbPrefsRemoteAccess_Enabled.isChecked();
			boolean useReceiver = cbPrefsRemoteAccess_UseReceiver.isChecked();
			
			if (remoteAccessEnabled) {
				valid = 
					ValidatorUtils.validateEditTextString(
						this.activity, 
						R.string.fdPrefsRemoteAccess_Password, 
						etPrefsRemoteAccess_Password, 
						4, 8, true);
				if (valid && useReceiver) {
					valid = ValidatorUtils.validateIfPhoneNumber(
						this.activity, 
						R.string.fdPrefsRemoteAccess_Receiver, 
						etPrefsRemoteAccess_Receiver, 
						true);	
				}
			}
			
			if (valid) {
				preferences.setRemoteAccessEnabled(cbPrefsRemoteAccess_Enabled.isChecked());
				if (cbPrefsRemoteAccess_Enabled.isChecked()) {
					preferences.setRemoteAccessPassword(etPrefsRemoteAccess_Password.getText().toString());
				} else {
					preferences.setRemoteAccessPassword("");
				}
				preferences.setRemoteAccessUseReceiver(cbPrefsRemoteAccess_UseReceiver.isChecked());
				if (cbPrefsRemoteAccess_UseReceiver.isChecked()) {
					preferences.setRemoteAccessReceiver(etPrefsRemoteAccess_Receiver.getText().toString());
				} else {
					preferences.setRemoteAccessReceiver("");
				}
				Preferences.save();
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsRemoteAccessActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(PrefsRemoteAccessActivity activity) {
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
        setContentView(R.layout.preferences_remote_access);
     
        this.setTitle(R.string.tiPrefsRemoteAccess);
        
        Preferences prefs = Preferences.get();
        
        CheckBox cbPrefsRemoteAccess_Enable = (CheckBox)findViewById(R.id.cbPrefsRemoteAccess_Enable);
        cbPrefsRemoteAccess_Enable.setChecked(prefs.isRemoteAccessEnabled());
        EditText etPrefsRemote_Password = (EditText) findViewById(R.id.etPrefsRemoteAccess_Password);
        etPrefsRemote_Password.setText(String.valueOf(prefs.getRemoteAccessPassword()));
        
        CheckBox cbPrefsRemote_UseReceiver = (CheckBox)findViewById(R.id.cbPrefsRemoteAccess_UseReceiver);
        cbPrefsRemote_UseReceiver.setChecked(prefs.isRemoteAccessUseReceiver());
        EditText etPrefsRemote_Receiver = (EditText) findViewById(R.id.etPrefsRemoteAccess_Receiver);
        etPrefsRemote_Receiver.setText(String.valueOf(prefs.getRemoteAccessReceiver()));
        
        Button btnPrefsRemoteAccess_Save = (Button) findViewById(R.id.btPrefsRemoteAccess_Save);
        Button btnPrefsRemoteAccess_Cancel = (Button) findViewById(R.id.btPrefsRemoteAccess_Cancel);
                
        btnPrefsRemoteAccess_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs,
				cbPrefsRemoteAccess_Enable,
				etPrefsRemote_Password,
				cbPrefsRemote_UseReceiver,
				etPrefsRemote_Receiver));
		
        btnPrefsRemoteAccess_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
