package de.msk.mylivetracker.client.android.trackexport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask.DoInBackgroundInitializer;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.status.LogInfo;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;

/**
 * classname: TrackExportActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class TrackExportActivity extends AbstractActivity {

	private static final class UploadToDropboxDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public UploadToDropboxDialog(Activity activity) {
			super(activity, R.string.txTrackExport_QuestionUploadTrack);
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
				null
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
				SimpleInfoDialog.show(this.activity, R.string.txTrackExport_NoTrackExists);
			} else {
				UploadToDropboxDialog dlg = new UploadToDropboxDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

	private static class ExportTrackProgressDialog extends AbstractProgressDialog<TrackExportActivity> {

		private String gpxFileName;
		
		public ExportTrackProgressDialog(String gpxFileName) {
			this.gpxFileName = gpxFileName;
		}

		@Override
		public void doTask(TrackExportActivity activity) {
			LogInfo.exportGpxFileOfCurrentTrackToExternalStorage(
				this.gpxFileName);
		}
	}
	
	private static void doExportToExternalStorage(
		TrackExportActivity activity, String gpxFileName) {
		ExportTrackProgressDialog dlg = 
			new ExportTrackProgressDialog(gpxFileName);
		dlg.run(activity, 
			R.string.lbTrackExport_ExportProgressDialog);
		SimpleInfoDialog.show(activity, 
			R.string.txTrackExport_InfoExportTrackDone);
	}
	
	private static final class ExportToExternalStorageCheckIfFileExistsDialog extends AbstractYesNoDialog {
		private TrackExportActivity activity;
		private String gpxFileName;
		
		public ExportToExternalStorageCheckIfFileExistsDialog(
			TrackExportActivity activity, String gpxFileName) {
			super(activity, R.string.txTrackExport_QuestionOverwriteExistingFile);
			this.activity = activity;			
			this.gpxFileName = gpxFileName;
		}

		@Override
		public void onYes() {			
			doExportToExternalStorage(this.activity, this.gpxFileName);
		}	
	}
	
	private static final class ExportToExternalStorageTrackDialog extends AbstractYesNoDialog {
		private TrackExportActivity activity;
				
		public ExportToExternalStorageTrackDialog(TrackExportActivity activity) {
			super(activity, R.string.txTrackExport_QuestionExportTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			String gpxFileName = LogInfo.createGpxFileNameOfCurrentTrack();
			if (FileUtils.fileExists(gpxFileName, PathType.ExternalStorage)) {
				ExportToExternalStorageCheckIfFileExistsDialog dlg = 
					new ExportToExternalStorageCheckIfFileExistsDialog(
						this.activity, gpxFileName);
				dlg.show();
			} else {
				doExportToExternalStorage(this.activity, gpxFileName);
			}
		}	
	}
	
	private static final class OnClickButtonExportToExternalStorage extends ASafeOnClickListener {
		private TrackExportActivity activity;
		
		private OnClickButtonExportToExternalStorage(TrackExportActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick() {	
			if (!LogInfo.logFileExists()) {
				SimpleInfoDialog.show(this.activity, R.string.txTrackExport_NoTrackExists);
			} else if (!FileUtils.externalStorageUsable()) {
				SimpleInfoDialog.show(this.activity, R.string.txTrackExport_NoExternalStorageAvailable);
			} else {
				ExportToExternalStorageTrackDialog dlg = new ExportToExternalStorageTrackDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

	private static final class OnClickButtonFilenamePrefs extends ASafeOnClickListener {
		private TrackExportActivity activity;
		
		private OnClickButtonFilenamePrefs(TrackExportActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick() {	
			this.activity.startActivity(
				new Intent(this.activity, TrackExportPrefsActivity.class));
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_export);
        this.setTitle(R.string.tiTrackExport);

        Button btTrackExportPrefs_UploadToDropbox = (Button)
        	findViewById(R.id.btTrackExport_UploadToDropbox);
        Button btTrackExportPrefs_ExportToExternalStorage = (Button)
        	findViewById(R.id.btTrackExport_ExportToExternalStorage);
        Button btTrackExport_FilenamePrefs = (Button)
        	findViewById(R.id.btTrackExport_FilenamePrefs);
        Button btTrackExportPrefs_Back = (Button)
        	findViewById(R.id.btTrackExport_Back);
        
        btTrackExportPrefs_UploadToDropbox.setOnClickListener(
			new OnClickButtonUploadToDropbox(this));
        btTrackExportPrefs_ExportToExternalStorage.setOnClickListener(
			new OnClickButtonExportToExternalStorage(this));
        btTrackExport_FilenamePrefs.setOnClickListener(
        	new OnClickButtonFilenamePrefs(this));
        btTrackExportPrefs_Back.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
