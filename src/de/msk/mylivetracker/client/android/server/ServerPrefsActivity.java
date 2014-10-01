package de.msk.mylivetracker.client.android.server;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.PrefsActivity;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: ServerPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ServerPrefsActivity extends PrefsActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private ServerPrefsActivity activity;
		private EditText etServerPrefs_ServerAddress;
		private EditText etServerPrefs_ServerPort;
		private EditText etServerPrefs_ServerPath;
		private EditText etServerPrefs_SmsReceivers;
		
		public OnClickButtonSaveListener(
			ServerPrefsActivity activity,
			EditText etServerPrefs_ServerAddress,
			EditText etServerPrefs_ServerPort, 
			EditText etServerPrefs_ServerPath,
			EditText etServerPrefs_SmsReceivers) {
			this.activity = activity;
			this.etServerPrefs_ServerAddress = etServerPrefs_ServerAddress;
			this.etServerPrefs_ServerPort = etServerPrefs_ServerPort;
			this.etServerPrefs_ServerPath = etServerPrefs_ServerPath;
			this.etServerPrefs_SmsReceivers = etServerPrefs_SmsReceivers;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdServerPrefs_ServerAddress, 
					etServerPrefs_ServerAddress, 3, 50, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdServerPrefs_ServerPort, 
					etServerPrefs_ServerPort, 
					true, 
					80, 99999, true) &&	
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdServerPrefs_ServerPath, 
					etServerPrefs_ServerPath, 0, 50, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdServerPrefs_SmsReceivers, 
					etServerPrefs_SmsReceivers, 0, 50, true) &&
				ValidatorUtils.validateIfStringArrayLimitReached(
					this.activity, 
					R.string.fdServerPrefs_SmsReceivers, 
					etServerPrefs_SmsReceivers, 3, true) &&
				ValidatorUtils.validateIfPhoneNumbers(
					this.activity, 
					R.string.fdServerPrefs_SmsReceivers, 
					etServerPrefs_SmsReceivers, true);
			
			if (valid) {
				ServerPrefs prefs = PrefsRegistry.get(ServerPrefs.class);
				prefs.setServer(etServerPrefs_ServerAddress.getText().toString());
				prefs.setPort(Integer.parseInt(etServerPrefs_ServerPort.getText().toString()));
				prefs.setPath(etServerPrefs_ServerPath.getText().toString());
				prefs.setSmsReceivers(etServerPrefs_SmsReceivers.getText().toString());
				PrefsRegistry.save(ServerPrefs.class);
				this.activity.finish();
			}
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_prefs);
     
        this.setTitle(R.string.tiServerPrefs);
        
        ServerPrefs prefs = PrefsRegistry.get(ServerPrefs.class);
        
        EditText etServerPrefs_ServerAddress = (EditText)findViewById(R.id.etServerPrefs_ServerAddress);
        etServerPrefs_ServerAddress.setText(prefs.getServer());
        EditText etServerPrefs_ServerPort = (EditText)findViewById(R.id.etServerPrefs_ServerPort);
        etServerPrefs_ServerPort.setText(String.valueOf(prefs.getPort()));
        EditText etServerPrefs_ServerPath = (EditText)findViewById(R.id.etServerPrefs_ServerPath);
        etServerPrefs_ServerPath.setText(prefs.getPath());
        EditText etServerPrefs_SmsReceivers = (EditText)findViewById(R.id.etServerPrefs_SmsReceivers);
        etServerPrefs_SmsReceivers.setText(prefs.getSmsReceivers());
        
        Button btServerPrefs_Save = (Button) findViewById(R.id.btServerPrefs_Save);
        Button btServerPrefs_Cancel = (Button) findViewById(R.id.btServerPrefs_Cancel);
                
        btServerPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				etServerPrefs_ServerAddress,
				etServerPrefs_ServerPort,
				etServerPrefs_ServerPath,
				etServerPrefs_SmsReceivers));
		
        btServerPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));        
    }
}
