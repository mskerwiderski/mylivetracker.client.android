package de.msk.mylivetracker.client.android.mainview;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadService;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: OnClickButtonResetListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonResetListener extends ASafeOnClickListener {
	
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
	
	public void onClick() {
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) return;
		final MainActivity activity = MainActivity.get();
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMedium()) {
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
			AbstractService.stopService(UploadService.class);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			activity.getUiChronometer().setBase(SystemClock.elapsedRealtime());
			AbstractService.stopService(LocalizationService.class);
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
