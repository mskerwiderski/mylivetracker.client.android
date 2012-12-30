package de.msk.mylivetracker.client.android.mainview;

import android.os.SystemClock;
import android.widget.Chronometer;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadService;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: OnClickButtonStartStopListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonStartStopListener extends ASafeOnClickListener {
	
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
		
		@Override
		public void onNo() {
			this.activity.getUiBtStartStop().setChecked(
				TrackStatus.get().trackIsRunning());
		}
	}
	
	@Override
	public void onClick() {
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			activity.getUiBtStartStop().setChecked(
				TrackStatus.get().trackIsRunning());
			return;
		}
		
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopTrackDialog dlg = new StartStopTrackDialog(activity);
			dlg.show();
		} else {
			startStopTrack(activity, !TrackStatus.get().trackIsRunning(), true);
		}
	}
	
	private static class StartTrackProgressDialog extends AbstractProgressDialog<MainActivity> {
		private boolean oneTouchMode;
		@Override
		public void beforeTask(MainActivity activity) {
			if (this.oneTouchMode) {
				TrackingOneTouchMode mode = PrefsRegistry.get(OtherPrefs.class).
					getTrackingOneTouchMode();
				switch (mode) {
					case TrackingLocalizationHeartrate:
						if (AntPlusHardware.initialized() && 
							PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable()) { 
							OnClickButtonAntPlusListener.
								startStopAntPlus(activity, true);
						}
					case TrackingLocalization:
						OnClickButtonLocationListenerOnOffListener.
							startStopLocationListener(activity, true);
					case TrackingOnly:
					default:
						break;
				}
			}
			Chronometer chronometer = 
				activity.getUiChronometer();
			chronometer.setBase(
				SystemClock.elapsedRealtime() -
				TrackStatus.get().getRuntimeInMSecs(false));
			chronometer.start();
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.startService(UploadService.class);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			activity.getUiBtStartStop().setChecked(true);
			activity.updateView();			
		}
		public void run(MainActivity activity, 
			int progressMsgId, int doneMsgId,
			boolean oneTouchMode) {
			this.oneTouchMode = oneTouchMode;
			super.run(activity, progressMsgId, doneMsgId);
		}
	}
	
	private static class StopTrackProgressDialog extends AbstractProgressDialog<MainActivity> {
		private boolean oneTouchMode;
		@Override
		public void beforeTask(MainActivity activity) {
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.stopService(UploadService.class);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			activity.getUiChronometer().stop();
			if (this.oneTouchMode) {
				TrackingOneTouchMode mode = PrefsRegistry.get(OtherPrefs.class).
					getTrackingOneTouchMode();
				switch (mode) {
					case TrackingLocalizationHeartrate:
						OnClickButtonAntPlusListener.
							startStopAntPlus(activity, false);
					case TrackingLocalization:
						OnClickButtonLocationListenerOnOffListener.
							startStopLocationListener(activity, false);
					case TrackingOnly:
					default:
						break;
				}
			}
			activity.getUiBtStartStop().setChecked(false);
			activity.updateView();			
		}
		public void run(MainActivity activity, 
			int progressMsgId, int doneMsgId,
			boolean oneTouchMode) {
			this.oneTouchMode = oneTouchMode;
			super.run(activity, progressMsgId, doneMsgId);
		}
	}
	
	public static void startStopTrack(final MainActivity activity, boolean start, boolean oneTouchMode) {
		if (start) {
			LogUtils.info(OnClickButtonStartStopListener.class, "start button pressed.");
			StartTrackProgressDialog startTrackDialog = new StartTrackProgressDialog();
			startTrackDialog.run(activity, 
			R.string.txMain_InfoStartingTracking, 
			R.string.txMain_InfoStartTrackDone,
			oneTouchMode);
		} else {
			LogUtils.info(OnClickButtonStartStopListener.class, "stop button pressed.");
			StopTrackProgressDialog stopTrackDialog = new StopTrackProgressDialog();
			stopTrackDialog.run(activity, 
			R.string.txMain_InfoStoppingTracking, 
			R.string.txMain_InfoStopTrackDone,
			oneTouchMode);
		}
	}
}
