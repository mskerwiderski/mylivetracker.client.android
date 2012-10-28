package de.msk.mylivetracker.client.android.mainview;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.Preferences.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadService;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonStartStopListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2012-02-20 
 *     	o TrackingOneTouch mode implemented.
 *      o startStopTrack adapted, that it can be used by AutoModeManager.
 *      o If in auto mode, startStopTrack is rejected.
 * 000 	2011-08-11 initial.
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
			startStopTrack(activity, !TrackStatus.get().trackIsRunning(), true);							
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
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			activity.getUiBtStartStop().setChecked(
				TrackStatus.get().trackIsRunning());
			return;
		}
		
		if (Preferences.get().getConfirmLevel().isHigh()) {
			StartStopTrackDialog dlg = new StartStopTrackDialog(activity);
			dlg.show();
		} else {
			startStopTrack(activity, !TrackStatus.get().trackIsRunning(), true);
		}
	}
	
	public static void startStopTrack(final MainActivity activity, boolean start, boolean oneTouchMode) {
		final boolean stopTrack = !start;
		
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
				activity.getUiBtStartStop().setChecked(
					TrackStatus.get().trackIsRunning());
				activity.updateView();
				dialog.dismiss();
		    }
		};
				
		Thread startStopThread = new Thread() {  
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (stopTrack) {
					UploadService.stop();
				} else {
					UploadService.start();
				}
				handler.sendEmptyMessage(0);
		     }
		};
		
		if (oneTouchMode) {
			TrackingOneTouchMode mode = Preferences.get().getTrackingOneTouchMode();
			switch (mode) {
				case TrackingLocalizationHeartrate:
					OnClickButtonAntPlusListener.
						startStopAntPlus(activity, !stopTrack);
				case TrackingLocalization:
					OnClickButtonLocationListenerOnOffListener.
						startStopLocationListener(activity, !stopTrack);
				case TrackingOnly:
				default:
					break;
			}
		}
		
		startStopThread.start();				
	}
}
