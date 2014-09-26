package de.msk.mylivetracker.client.android.protocol;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.PrefsActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.BufferSize;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.TransferProtocol;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.UploadDistanceTrigger;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.UploadTimeTrigger;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.UploadTriggerLogic;
import de.msk.mylivetracker.client.android.status.PositionBufferInfo;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;

/**
 * classname: ProtocolPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ProtocolPrefsActivity extends PrefsActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private ProtocolPrefsActivity activity;
		private Spinner spProtocolPrefs_TransferProtocol;
		private Spinner spProtocolPrefs_TimeTriggerInSecs;
		private Spinner spProtocolPrefs_TriggerLogic;
		private Spinner spProtocolPrefs_DistanceTriggerInMtr;	
		private CheckBox cbProtocolPrefs_CloseConnectionAfterEveryUpload;
		private CheckBox cbProtocolPrefs_FinishEveryUploadWithALinefeed;
		private CheckBox cbProtocolPrefs_UploadPositionOnlyOnce;
		private Spinner spProtocolPrefs_MaxPositionBufferSize;
		private CheckBox cbProtocolPrefs_LogTrackData;
		
		public OnClickButtonSaveListener(
			ProtocolPrefsActivity activity,
			Spinner spProtocolPrefs_TransferProtocol, 
			Spinner spProtocolPrefs_TimeTriggerInSecs,
			Spinner spProtocolPrefs_TriggerLogic,
			Spinner spProtocolPrefs_DistanceTriggerInMtr,
			CheckBox cbProtocolPrefs_CloseConnectionAfterEveryUpload,
			CheckBox cbProtocolPrefs_FinishEveryUploadWithALinefeed,
			CheckBox cbProtocolPrefs_UploadPositionOnlyOnce,
			Spinner spProtocolPrefs_MaxPositionBufferSize,
			CheckBox cbProtocolPrefs_LogTrackData) {
			this.activity = activity;
			this.spProtocolPrefs_TransferProtocol = spProtocolPrefs_TransferProtocol;
			this.spProtocolPrefs_TimeTriggerInSecs = spProtocolPrefs_TimeTriggerInSecs;
			this.spProtocolPrefs_TriggerLogic = spProtocolPrefs_TriggerLogic;
			this.spProtocolPrefs_DistanceTriggerInMtr =spProtocolPrefs_DistanceTriggerInMtr;
			this.cbProtocolPrefs_CloseConnectionAfterEveryUpload = cbProtocolPrefs_CloseConnectionAfterEveryUpload;
			this.cbProtocolPrefs_FinishEveryUploadWithALinefeed = cbProtocolPrefs_FinishEveryUploadWithALinefeed;
			this.cbProtocolPrefs_UploadPositionOnlyOnce = cbProtocolPrefs_UploadPositionOnlyOnce;
			this.spProtocolPrefs_MaxPositionBufferSize = spProtocolPrefs_MaxPositionBufferSize;
			this.cbProtocolPrefs_LogTrackData = cbProtocolPrefs_LogTrackData;
		}

		@Override
		public void onClick() {
			boolean valid = true;
						
			if (valid) {
				ProtocolPrefs prefs = PrefsRegistry.get(ProtocolPrefs.class);
				prefs.setTransferProtocol(TransferProtocol.values()
					[spProtocolPrefs_TransferProtocol.getSelectedItemPosition()]);
				prefs.setUplTimeTrigger(UploadTimeTrigger.values()
					[spProtocolPrefs_TimeTriggerInSecs.getSelectedItemPosition()]);
				prefs.setUplTriggerLogic(UploadTriggerLogic.values()
					[spProtocolPrefs_TriggerLogic.getSelectedItemPosition()]);
				prefs.setUplDistanceTrigger(UploadDistanceTrigger.values()
					[spProtocolPrefs_DistanceTriggerInMtr.getSelectedItemPosition()]);
				prefs.setCloseConnectionAfterEveryUpload(
					cbProtocolPrefs_CloseConnectionAfterEveryUpload.isChecked());
				prefs.setFinishEveryUploadWithALinefeed(
					cbProtocolPrefs_FinishEveryUploadWithALinefeed.isChecked());
				prefs.setUploadPositionsOnlyOnce(
					cbProtocolPrefs_UploadPositionOnlyOnce.isChecked());
				prefs.setUplPositionBufferSize(BufferSize.values()[
                   spProtocolPrefs_MaxPositionBufferSize.getSelectedItemPosition()]);
				if (prefs.getUplPositionBufferSize().isDisabled()) {
					PositionBufferInfo.reset();
				}
				prefs.setLogTrackData(cbProtocolPrefs_LogTrackData.isChecked());
				PrefsRegistry.save(ProtocolPrefs.class);
				this.activity.finish();
			}
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protocol_prefs);
     
        this.setTitle(R.string.tiProtocolPrefs);
        
        ProtocolPrefs prefs = PrefsRegistry.get(ProtocolPrefs.class);
        
        Spinner spProtocolPrefs_TransferProtocol = (Spinner) findViewById(R.id.spProtocolPrefs_TransferProtocol);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.transferProtocols, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProtocolPrefs_TransferProtocol.setAdapter(adapter);
        spProtocolPrefs_TransferProtocol.setSelection(prefs.getTransferProtocol().ordinal());        
        
        Spinner spProtocolPrefs_TimeTriggerInSecs = (Spinner) findViewById(R.id.spProtocolPrefs_TimeTriggerInSecs);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.uploadTimeTrigger, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProtocolPrefs_TimeTriggerInSecs.setAdapter(adapter);
        spProtocolPrefs_TimeTriggerInSecs.setSelection(prefs.getUplTimeTrigger().ordinal());        
        
        Spinner spProtocolPrefs_TriggerLogic = (Spinner) findViewById(R.id.spProtocolPrefs_TriggerLogic);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.uploadTriggerLogic, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProtocolPrefs_TriggerLogic.setAdapter(adapter);
        spProtocolPrefs_TriggerLogic.setSelection(prefs.getUplTriggerLogic().ordinal());        
        
        Spinner spProtocolPrefs_DistanceTriggerInMtr = (Spinner) findViewById(R.id.spProtocolPrefs_DistanceTriggerInMtr);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.uploadDistanceTrigger, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProtocolPrefs_DistanceTriggerInMtr.setAdapter(adapter);
        spProtocolPrefs_DistanceTriggerInMtr.setSelection(prefs.getUplDistanceTrigger().ordinal());        
                       
        CheckBox cbProtocolPrefs_CloseConnectionAfterEveryUpload = (CheckBox)
        	findViewById(R.id.cbProtocolPrefs_CloseConnectionAfterEveryUpload);
        cbProtocolPrefs_CloseConnectionAfterEveryUpload.setChecked(prefs.isCloseConnectionAfterEveryUpload());
		CheckBox cbProtocolPrefs_FinishEveryUploadWithALinefeed = (CheckBox)
			findViewById(R.id.cbProtocolPrefs_FinishEveryUploadWithALinefeed);
		cbProtocolPrefs_FinishEveryUploadWithALinefeed.setChecked(prefs.isFinishEveryUploadWithALinefeed());
		CheckBox cbProtocolPrefs_UploadPositionOnlyOnce = (CheckBox)
			findViewById(R.id.cbProtocolPrefs_UploadPositionOnlyOnce);
		cbProtocolPrefs_UploadPositionOnlyOnce.setChecked(prefs.isUploadPositionsOnlyOnce());
		
		Spinner spProtocolPrefs_MaxPositionBufferSize = 
			(Spinner)findViewById(R.id.spProtocolPrefs_MaxPositionBufferSize);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.bufferSizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProtocolPrefs_MaxPositionBufferSize.setAdapter(adapter);
        spProtocolPrefs_MaxPositionBufferSize.setSelection(prefs.getUplPositionBufferSize().ordinal());
        
        CheckBox cbProtocolPrefs_LogTrackData = (CheckBox)findViewById(R.id.cbProtocolPrefs_LogTrackData);
        cbProtocolPrefs_LogTrackData.setChecked(prefs.isLogTrackData());
        
        Button btProtocolPrefs_Save = (Button) findViewById(R.id.btProtocolPrefs_Save);
        Button btProtocolPrefs_Cancel = (Button) findViewById(R.id.btProtocolPrefs_Cancel);
                
        btProtocolPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				spProtocolPrefs_TransferProtocol,
				spProtocolPrefs_TimeTriggerInSecs,
				spProtocolPrefs_TriggerLogic,
				spProtocolPrefs_DistanceTriggerInMtr,
				cbProtocolPrefs_CloseConnectionAfterEveryUpload,
				cbProtocolPrefs_FinishEveryUploadWithALinefeed,
				cbProtocolPrefs_UploadPositionOnlyOnce,
				spProtocolPrefs_MaxPositionBufferSize,
				cbProtocolPrefs_LogTrackData));
		
        btProtocolPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));        
    }
}
