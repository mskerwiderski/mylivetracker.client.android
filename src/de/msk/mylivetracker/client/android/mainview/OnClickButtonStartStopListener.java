package de.msk.mylivetracker.client.android.mainview;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.updater.StatusBarUpdater;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadManager;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonStartStopListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class OnClickButtonStartStopListener implements OnClickListener {
	
	private static final class StartStopTrackDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public StartStopTrackDialog(MainActivity activity) {
			super(activity,
				TrackStatus.get().trackIsRunning() ? 	
				R.string.txMain_QuestionStopTrack :
				R.string.txMain_QuestionStartTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			startStopTrack(activity);							
		}	
		
		/* (non-Javadoc)
		 * @see de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog#onNo()
		 */
		@Override
		public void onNo() {
			this.activity.getUiBtStartStop().setChecked(
				TrackStatus.get().trackIsRunning());
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {				
		MainActivity activity = MainActivity.get();
		if (Preferences.get().getConfirmLevel().isHigh()) {
			StartStopTrackDialog dlg = new StartStopTrackDialog(activity);
			dlg.show();
		} else {
			startStopTrack(activity);
		}
	}
	
	private static void startStopTrack(final MainActivity activity) {
		final boolean stopTrack = TrackStatus.get().trackIsRunning();
		
		String message = stopTrack ?
			activity.getText(R.string.txMain_InfoStoppingTracking).toString() :
			activity.getText(R.string.txMain_InfoStartingTracking).toString();
			
		final ProgressDialog dialog = 
			ProgressDialog.show(activity, "", message, true);
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Chronometer chronometer = activity.getUiChronometer();
				if (stopTrack) { 										
					chronometer.stop();
				} else {
					chronometer.setBase(
						SystemClock.elapsedRealtime() -
						TrackStatus.get().getRuntimeInMSecs(false));
					chronometer.start();			
				}
				
				StatusBarUpdater.updateAppStatus();
				activity.updateView();
				dialog.dismiss();
		    }
		};
				
		Thread startStopThread = new Thread() {  
			public void run() {
				if (stopTrack) {
					UploadManager.stopUploadManager();
				} else {
					UploadManager.startUploadManager();
				}
				handler.sendEmptyMessage(0);
		     }
		};
		
		startStopThread.start();				
	}
}
