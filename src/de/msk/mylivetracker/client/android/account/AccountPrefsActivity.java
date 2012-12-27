package de.msk.mylivetracker.client.android.account;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * AccountPrefsActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 002
 * 
 * history
 * 002	2012-12-24 	revised for v1.5.x.
 * 001  2012-02-18 	phoneNumber added.
 * 000 	2011-08-11 	initial.
 * 
 */
public class AccountPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private AccountPrefsActivity activity;
		private EditText etAccountPrefs_DeviceId;
		private EditText etAccountPrefs_Username;
		private EditText etAccountPrefs_Password;
		private EditText etAccountPrefs_TrackName;
		private EditText etAccountPrefs_PhoneNumber;
		
		public OnClickButtonSaveListener(
			AccountPrefsActivity activity,
			EditText etAccountPrefs_DeviceId,
			EditText etAccountPrefs_Username, 
			EditText etAccountPrefs_Password,
			EditText etAccountPrefs_TrackName,
			EditText etAccountPrefs_PhoneNumber) {
			this.activity = activity;
			this.etAccountPrefs_DeviceId = etAccountPrefs_DeviceId;
			this.etAccountPrefs_Username = etAccountPrefs_Username;
			this.etAccountPrefs_Password = etAccountPrefs_Password;
			this.etAccountPrefs_TrackName = etAccountPrefs_TrackName;
			this.etAccountPrefs_PhoneNumber = etAccountPrefs_PhoneNumber;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdAccountPrefs_DeviceId, 
					etAccountPrefs_DeviceId, 7, 20, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdAccountPrefs_Username, 
					etAccountPrefs_Username, 
					0, 20, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdAccountPrefs_Password, 
					etAccountPrefs_Password, 
					0, 20, true) &&	
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdAccountPrefs_TrackName, 
					etAccountPrefs_TrackName, 
					1, 20, true) &&			
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdAccountPrefs_PhoneNumber, 
					etAccountPrefs_PhoneNumber, 					
					0, 25, true);
			
			if (valid) {
				AccountPrefs prefs = PrefsRegistry.get(AccountPrefs.class);
				prefs.setDeviceId(etAccountPrefs_DeviceId.getText().toString());
				prefs.setUsername(etAccountPrefs_Username.getText().toString());
				prefs.setPassword(etAccountPrefs_Password.getText().toString());
				prefs.setTrackName(etAccountPrefs_TrackName.getText().toString());
				prefs.setPhoneNumber(etAccountPrefs_PhoneNumber.getText().toString());
				PrefsRegistry.save(AccountPrefs.class);
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private AccountPrefsActivity activity;
		
		private OnClickButtonCancelListener(AccountPrefsActivity activity) {
			this.activity = activity;
		}
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_prefs);
     
        this.setTitle(R.string.tiAccountPrefs);
        
        AccountPrefs prefs = PrefsRegistry.get(AccountPrefs.class);
        
        EditText etAccountPrefs_DeviceId = 
        	(EditText)findViewById(R.id.etAccountPrefs_DeviceId);
        etAccountPrefs_DeviceId.setText(prefs.getDeviceId());
        etAccountPrefs_DeviceId.setEnabled(false);
        etAccountPrefs_DeviceId.setFocusable(false);
        etAccountPrefs_DeviceId.setClickable(false);
        EditText etAccountPrefs_Username = (EditText) findViewById(R.id.etAccountPrefs_Username);
        etAccountPrefs_Username.setText(String.valueOf(prefs.getUsername()));
        EditText etAccountPrefs_Password = (EditText) findViewById(R.id.etAccountPrefs_Password);
        etAccountPrefs_Password.setText(String.valueOf(prefs.getPassword()));
        EditText etAccountPrefs_TrackName = (EditText) findViewById(R.id.etAccountPrefs_TrackName);
        etAccountPrefs_TrackName.setText(String.valueOf(prefs.getTrackName()));
        EditText etAccountPrefs_PhoneNumber = (EditText) findViewById(R.id.etAccountPrefs_PhoneNumber);
        etAccountPrefs_PhoneNumber.setText(String.valueOf(prefs.getPhoneNumber()));
        
        Button btnAccountPrefs_Save = (Button) findViewById(R.id.btAccountPrefs_Save);
        Button btnAccountPrefs_Cancel = (Button) findViewById(R.id.btAccountPrefs_Cancel);
                
        btnAccountPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this,
				etAccountPrefs_DeviceId,
				etAccountPrefs_Username,
				etAccountPrefs_Password,
				etAccountPrefs_TrackName,
				etAccountPrefs_PhoneNumber));
		
        btnAccountPrefs_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
