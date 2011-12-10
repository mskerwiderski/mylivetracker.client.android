package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PrefsAccountActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class PrefsAccountActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsAccountActivity activity;
		private Preferences preferences;
		private EditText etPrefsAccount_DeviceId;
		private EditText etPrefsAccount_Username;
		private EditText etPrefsAccount_Password;
		private EditText etPrefsAccount_TrackName;
		
		public OnClickButtonSaveListener(
			PrefsAccountActivity activity,
			Preferences preferences, 
			EditText etPrefsAccount_DeviceId,
			EditText etPrefsAccount_Username, 
			EditText etPrefsAccount_Password,
			EditText etPrefsAccount_TrackName) {
			this.activity = activity;
			this.preferences = preferences;
			this.etPrefsAccount_DeviceId = etPrefsAccount_DeviceId;
			this.etPrefsAccount_Username = etPrefsAccount_Username;
			this.etPrefsAccount_Password = etPrefsAccount_Password;
			this.etPrefsAccount_TrackName = etPrefsAccount_TrackName;
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPrefsAccount_DeviceId, 
					etPrefsAccount_DeviceId, 7, 20, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPrefsAccount_Username, 
					etPrefsAccount_Username, 
					0, 20, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPrefsAccount_Password, 
					etPrefsAccount_Password, 
					0, 20, true) &&	
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPrefsAccount_TrackName, 
					etPrefsAccount_TrackName, 
					1, 20, true);			
			
			if (valid) {
				preferences.setDeviceId(etPrefsAccount_DeviceId.getText().toString());
				preferences.setUsername(etPrefsAccount_Username.getText().toString());
				preferences.setPassword(etPrefsAccount_Password.getText().toString());
				preferences.setTrackName(etPrefsAccount_TrackName.getText().toString());
				Preferences.save();
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsAccountActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(PrefsAccountActivity activity) {
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
        setContentView(R.layout.preferences_account);
     
        this.setTitle(R.string.tiPrefsAccount);
        
        Preferences prefs = Preferences.get();
        
        EditText etPrefsAccount_DeviceId = (EditText) findViewById(R.id.etPrefsAccount_DeviceId);
        etPrefsAccount_DeviceId.setText(prefs.getDeviceId());
        EditText etPrefsAccount_Username = (EditText) findViewById(R.id.etPrefsAccount_Username);
        etPrefsAccount_Username.setText(String.valueOf(prefs.getUsername()));
        EditText etPrefsAccount_Password = (EditText) findViewById(R.id.etPrefsAccount_Password);
        etPrefsAccount_Password.setText(String.valueOf(prefs.getPassword()));
        EditText etPrefsAccount_TrackName = (EditText) findViewById(R.id.etPrefsAccount_TrackName);
        etPrefsAccount_TrackName.setText(String.valueOf(prefs.getTrackName()));
        
        Button btnPrefsAccount_Save = (Button) findViewById(R.id.btPrefsAccount_Save);
        Button btnPrefsAccount_Cancel = (Button) findViewById(R.id.btPrefsAccount_Cancel);
                
        btnPrefsAccount_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs,
				etPrefsAccount_DeviceId,
				etPrefsAccount_Username,
				etPrefsAccount_Password,
				etPrefsAccount_TrackName));
		
        btnPrefsAccount_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
