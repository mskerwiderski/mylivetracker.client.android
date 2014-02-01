package de.msk.mylivetracker.client.android.emergency;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
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
public class EmergencyPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private EmergencyPrefsActivity activity;
		private EditText etEmergencyPrefs_MessageText;
		
		public OnClickButtonSaveListener(
			EmergencyPrefsActivity activity,
			EditText etEmergencyPrefs_MessageText) {
			this.activity = activity;
			this.etEmergencyPrefs_MessageText = etEmergencyPrefs_MessageText;
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
        
        
        Button btnEmergencyPrefs_Save = (Button) findViewById(R.id.btEmergencyPrefs_Save);
        Button btnEmergencyPrefs_Cancel = (Button) findViewById(R.id.btEmergencyPrefs_Cancel);
                
        btnEmergencyPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this,
				etEmergencyPrefs_MessageText));
		
        btnEmergencyPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
