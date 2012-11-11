package de.msk.mylivetracker.client.android.mainview;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.service.ServiceUtils;
import de.msk.mylivetracker.client.android.util.service.ServiceUtils.ServiceName;

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
	
	private static class ResetTrackProgressDialog extends AbstractProgressDialog<MainActivity> {
		@Override
		public void beforeTask(MainActivity activity) {
			activity.getUiChronometer().stop();
		}
		@Override
		public void doTask(MainActivity activity) {
			ServiceUtils.stopService(ServiceName.UploadService);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			activity.getUiChronometer().setBase(SystemClock.elapsedRealtime());
			activity.stopLocationListener();
			activity.stopAntPlusHeartrateListener();
			activity.getUiBtStartStop().setChecked(false);
			activity.getUiBtLocationListenerOnOff().setChecked(false);
			activity.getUiBtConnectDisconnectAnt().setChecked(false);
			activity.getUiBtStartStop().setChecked(false);
			TrackStatus.reset();
			activity.updateView();			
		}
	}
	
	public static void resetTrack(final MainActivity activity) {		
		ResetTrackProgressDialog resetTrackDialog = new ResetTrackProgressDialog();
		resetTrackDialog.run(activity, 
			R.string.txMain_InfoResettingTracking, 
			R.string.txMain_InfoResetTrackDone);
	}
}
