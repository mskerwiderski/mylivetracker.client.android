package de.msk.mylivetracker.client.android.emergency;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.PrefsActivity;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: EmergencyPrefsActivity
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-01	origin.
 * 
 */
public class EmergencyPrefsActivity extends PrefsActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private EmergencyPrefsActivity activity;
		private EditText etEmergencyPrefs_MessageText;
		private CheckBox lbEmergencyPrefs_SendEmergencyMessageAsSms;
		
		public OnClickButtonSaveListener(
			EmergencyPrefsActivity activity,
			EditText etEmergencyPrefs_MessageText,
			CheckBox lbEmergencyPrefs_SendEmergencyMessageAsSms) {
			this.activity = activity;
			this.etEmergencyPrefs_MessageText = etEmergencyPrefs_MessageText;
			this.lbEmergencyPrefs_SendEmergencyMessageAsSms = lbEmergencyPrefs_SendEmergencyMessageAsSms;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdEmergencyPrefs_MessageText, 
					etEmergencyPrefs_MessageText, 3, 20, true);
			if (valid) {
				EmergencyPrefs prefs = PrefsRegistry.get(EmergencyPrefs.class);
				prefs.setMessageText(etEmergencyPrefs_MessageText.getText().toString());
				prefs.setSendAsSms(lbEmergencyPrefs_SendEmergencyMessageAsSms.isChecked());
				PrefsRegistry.save(EmergencyPrefs.class);
				this.activity.finish();
			}
		}		
	}
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_prefs);
     
        this.setTitle(R.string.tiEmergencyPrefs);
        
        EmergencyPrefs prefs = PrefsRegistry.get(EmergencyPrefs.class);
        
        EditText etEmergencyPrefs_MessageText = 
        	(EditText)findViewById(R.id.etEmergencyPrefs_MessageText);
        etEmergencyPrefs_MessageText.setText(prefs.getMessageText());
        CheckBox lbEmergencyPrefs_SendEmergencyMessageAsSms = 
        	(CheckBox)findViewById(R.id.cbEmergencyPrefs_SendEmergencyMessageAsSms);
        lbEmergencyPrefs_SendEmergencyMessageAsSms.setChecked(prefs.isSendAsSms());
        
        Button btnEmergencyPrefs_Save = (Button) findViewById(R.id.btEmergencyPrefs_Save);
        Button btnEmergencyPrefs_Cancel = (Button) findViewById(R.id.btEmergencyPrefs_Cancel);
                
        btnEmergencyPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this,
				etEmergencyPrefs_MessageText,
				lbEmergencyPrefs_SendEmergencyMessageAsSms));
		
        btnEmergencyPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
