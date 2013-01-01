package de.msk.mylivetracker.client.android.trackexport;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask.DoInBackgroundInitializer;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask.PostProcessor;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.LogInfo;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
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
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class TrackExportPrefsActivity extends AbstractActivity {

	private static final class UploadToDropboxDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public UploadToDropboxDialog(Activity activity) {
			super(activity, R.string.txTrackExportPrefs_QuestionUploadTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {	
		 	final String gpxFileName = LogInfo.createGpxFileNameOfCurrentTrack();
			DropboxUploadTask.execute(this.activity, null, 
				gpxFileName, 
				new DoInBackgroundInitializer() {
					@Override
					public void init(String fileName) {
						LogInfo.createGpxFileOfCurrentTrack(gpxFileName);
					}
				},
				new PostProcessor() {
					@Override
					public void postProcess() {
						TrackExportPrefs prefs = PrefsRegistry.get(TrackExportPrefs.class);
						EditText etTrackExportPrefs_FilenameNextSequenceNumber = (EditText)
				        	activity.findViewById(R.id.etTrackExportPrefs_FilenameNextSequenceNumber);
				        etTrackExportPrefs_FilenameNextSequenceNumber.setText(
				        	String.valueOf(prefs.getFilenameNextSequenceNumber()));
					}
				}
			);
		}	
		
	}
	
	private static final class OnClickButtonUploadToDropbox extends ASafeOnClickListener {
		private Activity activity;
		
		private OnClickButtonUploadToDropbox(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick() {	
			if (!LogInfo.logFileExists()) {
				SimpleInfoDialog.show(this.activity, R.string.txTrackExportPrefs_NoTrackExists);
			} else {
				UploadToDropboxDialog dlg = new UploadToDropboxDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

	private static class ExportTrackProgressDialog extends AbstractProgressDialog<TrackExportPrefsActivity> {
		@Override
		public void doTask(TrackExportPrefsActivity activity) {
			LogInfo.exportGpxFileOfCurrentTrackToExternalStorage();
		}
		@Override
		public void cleanUp(TrackExportPrefsActivity activity) {
			TrackExportPrefs prefs = PrefsRegistry.get(TrackExportPrefs.class);
			EditText etTrackExportPrefs_FilenameNextSequenceNumber = (EditText)
	        	activity.findViewById(R.id.etTrackExportPrefs_FilenameNextSequenceNumber);
	        etTrackExportPrefs_FilenameNextSequenceNumber.setText(
	        	String.valueOf(prefs.getFilenameNextSequenceNumber()));
		}
	}
	
	private static final class ExportToExternalStorageTrackDialog extends AbstractYesNoDialog {
		private TrackExportPrefsActivity activity;
				
		public ExportToExternalStorageTrackDialog(TrackExportPrefsActivity activity) {
			super(activity, R.string.txTrackExportPrefs_QuestionExportTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			ExportTrackProgressDialog dlg = new ExportTrackProgressDialog();
			dlg.run(this.activity, 
				R.string.lbTrackExportPrefs_ExportProgressDialog);
			SimpleInfoDialog.show(this.activity, 
				R.string.txTrackExportPrefs_InfoExportTrackDone);
		}	
	}
	
	private static final class OnClickButtonExportToExternalStorage implements OnClickListener {
		private TrackExportPrefsActivity activity;
		
		private OnClickButtonExportToExternalStorage(TrackExportPrefsActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View view) {	
			if (!LogInfo.logFileExists()) {
				SimpleInfoDialog.show(this.activity, R.string.txTrackExportPrefs_NoTrackExists);
			} else if (!FileUtils.externalStorageUsable()) {
				SimpleInfoDialog.show(this.activity, R.string.txTrackExportPrefs_NoExternalStorageAvailable);
			} else {
				ExportToExternalStorageTrackDialog dlg = new ExportToExternalStorageTrackDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

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
        this.setTitle(R.string.tiTrackExport);

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
        
        Button btTrackExportPrefs_UploadToDropbox = (Button)
        	findViewById(R.id.btTrackExportPrefs_UploadToDropbox);
        Button btTrackExportPrefs_ExportToExternalStorage = (Button)
        	findViewById(R.id.btTrackExportPrefs_ExportToExternalStorage);
        Button btTrackExportPrefs_Save = (Button)
        	findViewById(R.id.btTrackExportPrefs_Save);
        Button btTrackExportPrefs_Cancel = (Button)
        	findViewById(R.id.btTrackExportPrefs_Cancel);
        Button btTrackExportPrefs_FilenameResetSequenceNumber = (Button)
        	findViewById(R.id.btTrackExportPrefs_FilenameResetSequenceNumber);
        
        btTrackExportPrefs_UploadToDropbox.setOnClickListener(
			new OnClickButtonUploadToDropbox(this));
        btTrackExportPrefs_ExportToExternalStorage.setOnClickListener(
			new OnClickButtonExportToExternalStorage(this));
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
