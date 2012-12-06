package de.msk.mylivetracker.client.android.track;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.client2.android.AndroidAuthSession;

import de.msk.mylivetracker.client.android.dropbox.DropboxUtils;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * TrackActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 000 	2012-12-04 initial.
 * 
 */
public class TrackActivity extends AbstractActivity {

	private static AndroidAuthSession dropBoxSession = null;
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private TrackActivity activity;
		
		private OnClickButtonCancelListener(TrackActivity activity) {
			this.activity = activity;
		}
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	private static final class UploadTrackDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public UploadTrackDialog(Activity activity) {
			super(activity, R.string.txTrack_QuestionUploadTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			DropboxUtils.uploadTrack(this.activity);
			SimpleInfoDialog dlg = new SimpleInfoDialog(
				this.activity, R.string.txTrack_InfoUploadTrackDone);
			dlg.show();
		}	
	}
	
	private static final class OnClickButtonUploadToDropbox implements OnClickListener {
		private Activity activity;
		
		private OnClickButtonUploadToDropbox(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View view) {	
			dropBoxSession = DropboxUtils.checkAuth(this.activity);
			if (dropBoxSession == null) {
				UploadTrackDialog dlg = new UploadTrackDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

	@Override
	protected void onResume() {
		super.onResume();
		DropboxUtils.finishAuth(dropBoxSession);
		dropBoxSession = null;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track);
        this.setTitle(R.string.tiTrack);

        Button btTrack_UploadToDropbox = (Button)findViewById(R.id.btTrack_UploadToDropbox);
        Button btTrack_Cancel = (Button)findViewById(R.id.btTrack_Cancel);
        
        btTrack_UploadToDropbox.setOnClickListener(
			new OnClickButtonUploadToDropbox(this));
        btTrack_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
