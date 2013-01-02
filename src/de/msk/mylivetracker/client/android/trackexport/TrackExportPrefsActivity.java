package de.msk.mylivetracker.client.android.trackexport;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: TrackExportPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-02	revised for v1.5.x.
 * 
 */
public class TrackExportPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private TrackExportPrefsActivity activity;
		private EditText etTrackExportPrefs_FilenamePrefix;
		private CheckBox cbTrackExportPrefs_FilenameAppendTrackName;
		private CheckBox cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime;
		private CheckBox cbTrackExportPrefs_FilenameAppendSequenceNumber;
		private EditText etTrackExportPrefs_FilenameNextSequenceNumber;
		
		public OnClickButtonSaveListener(
			TrackExportPrefsActivity activity,
			EditText etTrackExportPrefs_FilenamePrefix,
			CheckBox cbTrackExportPrefs_FilenameAppendTrackName,
			CheckBox cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime,
			CheckBox cbTrackExportPrefs_FilenameAppendSequenceNumber,
			EditText etTrackExportPrefs_FilenameNextSequenceNumber) {
			this.activity = activity;
			this.etTrackExportPrefs_FilenamePrefix = etTrackExportPrefs_FilenamePrefix;
			this.cbTrackExportPrefs_FilenameAppendTrackName = cbTrackExportPrefs_FilenameAppendTrackName;
			this.cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime = cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime;
			this.cbTrackExportPrefs_FilenameAppendSequenceNumber = cbTrackExportPrefs_FilenameAppendSequenceNumber;
			this.etTrackExportPrefs_FilenameNextSequenceNumber = etTrackExportPrefs_FilenameNextSequenceNumber;
		}

		@Override
		public void onClick() {
			TrackExportPrefs prefs = PrefsRegistry.get(TrackExportPrefs.class);
			
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdTrackExportPrefs_FilenamePrefix, 
					etTrackExportPrefs_FilenamePrefix, 3, 10, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdTrackExportPrefs_FilenameNextSequenceNumber, 
					etTrackExportPrefs_FilenameNextSequenceNumber, 
					true, 
					1, prefs.getMaxDigitsOfSequenceNumber(), true);			
			
			if (valid) {
				prefs.setFilenameAppendSequenceNumber(
					cbTrackExportPrefs_FilenameAppendSequenceNumber.isChecked());
				prefs.setFilenameAppendTimestampOfTrackStartTime(
					cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime.isChecked());
				prefs.setFilenameAppendTrackName(
					cbTrackExportPrefs_FilenameAppendTrackName.isChecked());
				prefs.setFilenameNextSequenceNumber(Integer.valueOf(
					etTrackExportPrefs_FilenameNextSequenceNumber.getText().toString()));
				prefs.setFilenamePrefix(
					etTrackExportPrefs_FilenamePrefix.getText().toString());
				PrefsRegistry.save(TrackExportPrefs.class);
				this.activity.finish();
			}
		}		
	}

	private static final class OnClickButtonFilenameResetSequenceNumberListener extends ASafeOnClickListener {
		private EditText etTrackExportPrefs_FilenameNextSequenceNumber;
		
		public OnClickButtonFilenameResetSequenceNumberListener(
			EditText etTrackExportPrefs_FilenameNextSequenceNumber) {
			this.etTrackExportPrefs_FilenameNextSequenceNumber = 
				etTrackExportPrefs_FilenameNextSequenceNumber;
		}

		@Override
		public void onClick() {
			etTrackExportPrefs_FilenameNextSequenceNumber.setText("0");
		}		
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_export_prefs);
        this.setTitle(R.string.tiTrackExportPrefs);

        TrackExportPrefs prefs = PrefsRegistry.get(TrackExportPrefs.class);
        
        EditText etTrackExportPrefs_FilenamePrefix = (EditText)
        	findViewById(R.id.etTrackExportPrefs_FilenamePrefix);
        etTrackExportPrefs_FilenamePrefix.setText(prefs.getFilenamePrefix());
        CheckBox cbTrackExportPrefs_FilenameAppendTrackName = (CheckBox)findViewById(R.id.cbTrackExportPrefs_FilenameAppendTrackName);
        cbTrackExportPrefs_FilenameAppendTrackName.setChecked(prefs.isFilenameAppendTrackName());
        CheckBox cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime = (CheckBox)findViewById(R.id.cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime);
        cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime.setChecked(prefs.isFilenameAppendTimestampOfTrackStartTime());
        CheckBox cbTrackExportPrefs_FilenameAppendSequenceNumber = (CheckBox)findViewById(R.id.cbTrackExportPrefs_FilenameAppendSequenceNumber);
        cbTrackExportPrefs_FilenameAppendSequenceNumber.setChecked(prefs.isFilenameAppendSequenceNumber());
        EditText etTrackExportPrefs_FilenameNextSequenceNumber = (EditText)
        	findViewById(R.id.etTrackExportPrefs_FilenameNextSequenceNumber);
        etTrackExportPrefs_FilenameNextSequenceNumber.setText(
        	String.valueOf(prefs.getFilenameNextSequenceNumber()));
        etTrackExportPrefs_FilenameNextSequenceNumber.setEnabled(false);
        etTrackExportPrefs_FilenameNextSequenceNumber.setFocusable(false);
        etTrackExportPrefs_FilenameNextSequenceNumber.setClickable(false);
        
        Button btTrackExportPrefs_Save = (Button)
        	findViewById(R.id.btTrackExportPrefs_Save);
        Button btTrackExportPrefs_Cancel = (Button)
        	findViewById(R.id.btTrackExportPrefs_Cancel);
        Button btTrackExportPrefs_FilenameResetSequenceNumber = (Button)
        	findViewById(R.id.btTrackExportPrefs_FilenameResetSequenceNumber);
        
        btTrackExportPrefs_Save.setOnClickListener(
        	new OnClickButtonSaveListener(
    			this,
		        etTrackExportPrefs_FilenamePrefix,
		        cbTrackExportPrefs_FilenameAppendTrackName,
		        cbTrackExportPrefs_FilenameAppendTimestampOfTrackStartTime,
		        cbTrackExportPrefs_FilenameAppendSequenceNumber,
		        etTrackExportPrefs_FilenameNextSequenceNumber));
        btTrackExportPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
        btTrackExportPrefs_FilenameResetSequenceNumber.setOnClickListener(
        	new OnClickButtonFilenameResetSequenceNumberListener(
        		etTrackExportPrefs_FilenameNextSequenceNumber));
    }
}
