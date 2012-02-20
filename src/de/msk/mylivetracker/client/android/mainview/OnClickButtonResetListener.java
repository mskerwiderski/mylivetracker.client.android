package de.msk.mylivetracker.client.android.mainview;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.updater.StatusBarUpdater;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadManager;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonResetListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2011-02-20 If in auto mode, resetTrack is rejected.
 * 000 	2011-08-11 initial.
 * 
 */
public class OnClickButtonResetListener implements OnClickListener {
	
	private static final class ResetTrackDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public ResetTrackDialog(MainActivity activity) {
			super(activity, R.string.txMain_QuestionResetTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			resetTrack(activity);							
		}	
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) return;
		final MainActivity activity = MainActivity.get();
		if (Preferences.get().getConfirmLevel().isMedium()) {
			ResetTrackDialog dlg = new ResetTrackDialog(activity);
			dlg.show();
		} else {
			resetTrack(activity);
		}
	}	
	
	public static void resetTrack(MainActivity activity) {		
		UploadManager.stopUploadManager();			
		activity.stopLocationListener();
		Chronometer chronometer = activity.getUiChronometer();
		chronometer.stop();			
		chronometer.setBase(SystemClock.elapsedRealtime());
		activity.stopAntPlusHeartrateListener();
		TrackStatus.reset();
		activity.getUiBtStartStop().setChecked(false);
		activity.getUiBtLocationListenerOnOff().setChecked(false);
		activity.getUiBtConnectDisconnectAnt().setChecked(false);
		activity.updateView();
		StatusBarUpdater.updateAppStatus();
		Toast.makeText(activity.getApplicationContext(), 
			activity.getString(R.string.txMain_InfoResetTrackDone),
			Toast.LENGTH_LONG).show();
	}
}
