package de.msk.mylivetracker.client.android.trackexport;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask;
import de.msk.mylivetracker.client.android.dropbox.DropboxUploadTask.DoInBackgroundInitializer;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.status.LogInfo;
import de.msk.mylivetracker.client.android.util.FileUtils;
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
				SimpleInfoDialog.show(this.activity, R.string.txTrackExport_NoTrackExists);
			} else {
				UploadToDropboxDialog dlg = new UploadToDropboxDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

	private static class ExportTrackProgressDialog extends AbstractProgressDialog<TrackExportActivity> {
		@Override
		public void doTask(TrackExportActivity activity) {
			LogInfo.exportGpxFileOfCurrentTrackToExternalStorage();
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
			ExportTrackProgressDialog dlg = new ExportTrackProgressDialog();
			dlg.run(this.activity, R.string.lbTrackExport_ExportProgressDialog);
		}	
	}
	
	private static final class OnClickButtonExportToExternalStorage implements OnClickListener {
		private TrackExportActivity activity;
		
		private OnClickButtonExportToExternalStorage(TrackExportActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View view) {	
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
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_export);
        this.setTitle(R.string.tiTrackExport);

        Button btTrackExport_UploadToDropbox = (Button)
        	findViewById(R.id.btTrackExport_UploadToDropbox);
        Button btTrackExport_ExportToExternalStorage = (Button)
        	findViewById(R.id.btTrackExport_ExportToExternalStorage);
        Button btTrackExport_Back = (Button)
        	findViewById(R.id.btTrackExport_Back);
        
        btTrackExport_UploadToDropbox.setOnClickListener(
			new OnClickButtonUploadToDropbox(this));
        btTrackExport_ExportToExternalStorage.setOnClickListener(
			new OnClickButtonExportToExternalStorage(this));
        btTrackExport_Back.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
