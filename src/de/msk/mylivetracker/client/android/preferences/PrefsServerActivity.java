package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences.TransferProtocol;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PrefsServerActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class PrefsServerActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsServerActivity activity;
		private Preferences preferences;
		private Spinner spPrefsServer_TransferProtocol;
		private EditText etPrefsServer_ServerAddress;
		private EditText etPrefsServer_ServerPort;
		private EditText etPrefsServer_ServerPath;
		private EditText etPrefsServer_TimeTriggerInSecs;
		private EditText etPrefsServer_DistanceTriggerInMtr;	
		private CheckBox cbPrefsServer_CloseConnectionAfterEveryUpload;
		private CheckBox cbPrefsServer_FinishEveryUploadWithALinefeed;
		
		public OnClickButtonSaveListener(
			PrefsServerActivity activity,
			Preferences preferences, 
			Spinner spPrefsServer_TransferProtocol, 
			EditText etPrefsServer_ServerAddress,
			EditText etPrefsServer_ServerPort, 
			EditText etPrefsServer_ServerPath,
			EditText etPrefsServer_TimeTriggerInSecs,
			EditText etPrefsServer_DistanceTriggerInMtr,
			CheckBox cbPrefsServer_CloseConnectionAfterEveryUpload,
			CheckBox cbPrefsServer_FinishEveryUploadWithALinefeed) {
			super();
			this.activity = activity;
			this.preferences = preferences;
			this.spPrefsServer_TransferProtocol = spPrefsServer_TransferProtocol;
			this.etPrefsServer_ServerAddress = etPrefsServer_ServerAddress;
			this.etPrefsServer_ServerPort = etPrefsServer_ServerPort;
			this.etPrefsServer_TimeTriggerInSecs = etPrefsServer_TimeTriggerInSecs;
			this.etPrefsServer_DistanceTriggerInMtr = etPrefsServer_DistanceTriggerInMtr;			
			this.etPrefsServer_ServerPath = etPrefsServer_ServerPath;
			this.cbPrefsServer_CloseConnectionAfterEveryUpload = cbPrefsServer_CloseConnectionAfterEveryUpload;
			this.cbPrefsServer_FinishEveryUploadWithALinefeed = cbPrefsServer_FinishEveryUploadWithALinefeed;
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
					R.string.fdPrefsServer_ServerAddress, 
					etPrefsServer_ServerAddress, 3, 50, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsServer_ServerPort, 
					etPrefsServer_ServerPort, 
					true, 
					80, 99999, true) &&	
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPrefsServer_ServerPath, 
					etPrefsServer_ServerPath, 0, 50, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsServer_TimeTriggerInSecs, 
					etPrefsServer_TimeTriggerInSecs, 
					true, 
					3, 3600, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsServer_DistanceTriggerInMtr, 
					etPrefsServer_DistanceTriggerInMtr, 
					true, 
					0, 10000, true);			
			
			if (valid) {
				preferences.setTransferProtocol(TransferProtocol.values()[spPrefsServer_TransferProtocol.getSelectedItemPosition()]);
				preferences.setServer(etPrefsServer_ServerAddress.getText().toString());
				preferences.setPort(Integer.parseInt(etPrefsServer_ServerPort.getText().toString()));
				preferences.setPath(etPrefsServer_ServerPath.getText().toString());
				preferences.setUplTimeTriggerInSeconds(Integer.parseInt(etPrefsServer_TimeTriggerInSecs.getText().toString()));
				preferences.setUplDistanceTriggerInMeter(Integer.parseInt(etPrefsServer_DistanceTriggerInMtr.getText().toString()));
				preferences.setCloseConnectionAfterEveryUpload(cbPrefsServer_CloseConnectionAfterEveryUpload.isChecked());
				preferences.setFinishEveryUploadWithALinefeed(cbPrefsServer_FinishEveryUploadWithALinefeed.isChecked());
				Preferences.save();
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsServerActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(PrefsServerActivity activity) {
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

	private static final class OnTransferProtocolSelectedListener implements OnItemSelectedListener {

		private long currentItemId;
		private EditText etPrefsServer_ServerAddress;
		private EditText etPrefsServer_ServerPort;
		private EditText etPrefsServer_ServerPath;
		
		public OnTransferProtocolSelectedListener(
			long currentItemId,	
			EditText etPrefsServer_ServerAddress,
			EditText etPrefsServer_ServerPort,
			EditText etPrefsServer_ServerPath) {
			this.currentItemId = currentItemId;
			this.etPrefsServer_ServerAddress = etPrefsServer_ServerAddress;
			this.etPrefsServer_ServerPort = etPrefsServer_ServerPort;
			this.etPrefsServer_ServerPath = etPrefsServer_ServerPath;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent,
			View view, int position, long id) {
			if (this.currentItemId != id) {
				if (TransferProtocol.mltHttpPlain.ordinal() == id) {
					this.etPrefsServer_ServerAddress.setText(R.string.txPrefs_Def_MyLiveTracker_Server);
					this.etPrefsServer_ServerPort.setText(R.string.txPrefs_Def_MyLiveTracker_PortHttp);
					this.etPrefsServer_ServerPath.setText(R.string.txPrefs_Def_MyLiveTracker_PathHttp);
				} else if (TransferProtocol.mltTcpEncrypted.ordinal() == id) {
					this.etPrefsServer_ServerAddress.setText(R.string.txPrefs_Def_MyLiveTracker_Server);
					this.etPrefsServer_ServerPort.setText(R.string.txPrefs_Def_MyLiveTracker_PortTcp);
					this.etPrefsServer_ServerPath.setText(R.string.txPrefs_Def_MyLiveTracker_PathTcp);
				}
				this.currentItemId = id;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// noop.
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_server);
     
        this.setTitle(R.string.tiPrefsServer);
        
        Preferences prefs = Preferences.get();
        
        Spinner spPrefsServer_TransferProtocol = (Spinner) findViewById(R.id.spPrefsServer_TransferProtocol);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
                this, R.array.transferProtocols, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsServer_TransferProtocol.setAdapter(adapter);
        spPrefsServer_TransferProtocol.setSelection(prefs.getTransferProtocol().ordinal());        
        
        EditText etPrefsServer_ServerAddress = (EditText)findViewById(R.id.etPrefsServer_ServerAddress);
        etPrefsServer_ServerAddress.setText(prefs.getServer());
        EditText etPrefsServer_ServerPort = (EditText)findViewById(R.id.etPrefsServer_ServerPort);
        etPrefsServer_ServerPort.setText(String.valueOf(prefs.getPort()));
        EditText etPrefsServer_ServerPath = (EditText)findViewById(R.id.etPrefsServer_ServerPath);
        etPrefsServer_ServerPath.setText(prefs.getPath());
        EditText etPrefsServer_TimeTriggerInSecs = (EditText)findViewById(R.id.etPrefsServer_TimeTriggerInSecs);
        etPrefsServer_TimeTriggerInSecs.setText(String.valueOf(prefs.getUplTimeTriggerInSeconds()));
        EditText etPrefsServer_DistanceTriggerInMtr = (EditText)findViewById(R.id.etPrefsServer_DistanceTriggerInMtr);
        etPrefsServer_DistanceTriggerInMtr.setText(String.valueOf(prefs.getUplDistanceTriggerInMeter()));                
                  
        CheckBox cbPrefsServer_CloseConnectionAfterEveryUpload = (CheckBox)findViewById(R.id.cbPrefsServer_CloseConnectionAfterEveryUpload);
        cbPrefsServer_CloseConnectionAfterEveryUpload.setChecked(prefs.isCloseConnectionAfterEveryUpload());
		CheckBox cbPrefsServer_FinishEveryUploadWithALinefeed = (CheckBox)findViewById(R.id.cbPrefsServer_FinishEveryUploadWithALinefeed);
		cbPrefsServer_FinishEveryUploadWithALinefeed.setChecked(prefs.isFinishEveryUploadWithALinefeed());
		
        spPrefsServer_TransferProtocol.setOnItemSelectedListener(
        	new OnTransferProtocolSelectedListener(
        		prefs.getTransferProtocol().ordinal(),	
        		etPrefsServer_ServerAddress,
        		etPrefsServer_ServerPort,
        		etPrefsServer_ServerPath));
        
        Button btPrefsServer_Save = (Button) findViewById(R.id.btPrefsServer_Save);
        Button btPrefsServer_Cancel = (Button) findViewById(R.id.btPrefsServer_Cancel);
                
        btPrefsServer_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs,
				spPrefsServer_TransferProtocol,
				etPrefsServer_ServerAddress,
				etPrefsServer_ServerPort,
				etPrefsServer_ServerPath,
				etPrefsServer_TimeTriggerInSecs,
				etPrefsServer_DistanceTriggerInMtr,
				cbPrefsServer_CloseConnectionAfterEveryUpload,
				cbPrefsServer_FinishEveryUploadWithALinefeed));
		
        btPrefsServer_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));        
    }
}
