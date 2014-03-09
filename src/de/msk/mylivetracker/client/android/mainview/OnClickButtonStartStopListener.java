package de.msk.mylivetracker.client.android.mainview;

import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ToggleButton;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
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
	
	private ToggleButton btMain_StartStopTrack;
	
	public OnClickButtonStartStopListener(ToggleButton btMain_StartStopTrack) {
		this.btMain_StartStopTrack = btMain_StartStopTrack;
	}

	private static final class StartStopTrackDialog extends AbstractYesNoDialog {

		private MainActivity activity;
		private ToggleButton btMain_StartStopTrack;
		
		public StartStopTrackDialog(MainActivity activity,
			ToggleButton btMain_StartStopTrack) {
			super(activity,
				TrackStatus.get().trackIsRunning() ? 	
				(TrackingModePrefs.isCheckpoint() ? 
					R.string.txMain_QuestionStopCheckpoint : 
					R.string.txMain_QuestionStopTrack) :
				(TrackingModePrefs.isCheckpoint() ?
					R.string.txMain_QuestionStartCheckpoint :
					R.string.txMain_QuestionStartTrack));
			this.activity = activity;			
			this.btMain_StartStopTrack = btMain_StartStopTrack;
		}

		@Override
		public void onYes() {		
			startStopTrack(activity, !TrackStatus.get().trackIsRunning(), true);							
		}	
		
		@Override
		public void onNo() {
			this.btMain_StartStopTrack.setChecked(
				TrackStatus.get().trackIsRunning());
		}
	}
	
	@Override
	public void onClick() {
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			this.btMain_StartStopTrack.setChecked(
				TrackStatus.get().trackIsRunning());
			return;
		}
		
		if ((PrefsRegistry.get(TrackingModePrefs.class).
				getTrackingMode().equals(TrackingMode.Checkpoint) &&
			PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMax()) ||
			(PrefsRegistry.get(TrackingModePrefs.class).
				getTrackingMode().equals(TrackingMode.Standard) &&
			PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh())) {
			StartStopTrackDialog dlg = new StartStopTrackDialog(
				activity, this.btMain_StartStopTrack);
			dlg.show();
		} else {
			startStopTrack(activity, !TrackStatus.get().trackIsRunning(), true);
		}
	}
	
	private static class StartTrackProgressDialog extends AbstractProgressDialog<MainActivity> {
		private ToggleButton btMain_StartStopTrack;
		private Chronometer chronometer;
		private boolean oneTouchMode;
		
		public StartTrackProgressDialog(
			ToggleButton btMain_StartStopTrack,
			Chronometer chronometer,
			boolean oneTouchMode) {
			super();
			this.btMain_StartStopTrack = btMain_StartStopTrack;
			this.chronometer = chronometer;
			this.oneTouchMode = oneTouchMode;
		}
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
							LogUtils.info("antPlus listener started.");
						}
					case TrackingLocalization:
						OnClickButtonLocationListenerOnOffListener.
							startStopLocationListener(activity, false, true);
					case TrackingOnly:
					default:
						break;
				}
			}
			this.chronometer.setBase(
				SystemClock.elapsedRealtime() -
				TrackStatus.get().getRuntimeInMSecs(false));
			this.chronometer.start();
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.startService(UploadService.class);
			LogUtils.info("uploadService started.");
		}
		@Override
		public void cleanUp(MainActivity activity) {
			this.btMain_StartStopTrack.setChecked(true);
		}
	}
	
	private static class StopTrackProgressDialog extends AbstractProgressDialog<MainActivity> {
		private ToggleButton btMain_StartStopTrack;
		private Chronometer chronometer;
		private boolean oneTouchMode;
		
		public StopTrackProgressDialog(
			ToggleButton btMain_StartStopTrack,
			Chronometer chronometer,
			boolean oneTouchMode) {
			super();
			this.btMain_StartStopTrack = btMain_StartStopTrack;
			this.chronometer = chronometer;
			this.oneTouchMode = oneTouchMode;
		}
		@Override
		public void beforeTask(MainActivity activity) {
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.stopService(UploadService.class);
			LogUtils.info("uploadService stopped.");
		}
		@Override
		public void cleanUp(MainActivity activity) {
			this.chronometer.stop();
			if (this.oneTouchMode) {
				TrackingOneTouchMode mode = PrefsRegistry.get(OtherPrefs.class).
					getTrackingOneTouchMode();
				switch (mode) {
					case TrackingLocalizationHeartrate:
						OnClickButtonAntPlusListener.
							startStopAntPlus(activity, false);
						LogUtils.info("antPlus listener stopped.");
					case TrackingLocalization:
						OnClickButtonLocationListenerOnOffListener.
							startStopLocationListener(activity, false, false);
					case TrackingOnly:
					default:
						break;
				}
			}
			this.btMain_StartStopTrack.setChecked(false);
		}
	}
	
	public static void startStopTrack(final MainActivity activity, boolean start, boolean oneTouchMode) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null!");
		}
		TrackingModePrefs trackingModePrefs = PrefsRegistry.get(TrackingModePrefs.class);
		int txIdStarting = R.string.txMain_InfoStartingTracking;
		int txIdStartingDone = R.string.txMain_InfoStartTrackDone;
		int txIdStopping = R.string.txMain_InfoStoppingTracking;
		int txIdStoppingDone = R.string.txMain_InfoStopTrackDone;
		if (trackingModePrefs.getTrackingMode().equals(TrackingMode.Checkpoint)) {
			txIdStarting = R.string.txMain_InfoStartingCheckpoint;
			txIdStartingDone = R.string.txMain_InfoStartCheckpointDone;
			txIdStopping = R.string.txMain_InfoStoppingCheckpoint;
			txIdStoppingDone = R.string.txMain_InfoStopCheckpointDone;
		}
		ToggleButton btMain_StartStopTrack = (ToggleButton)
			activity.findViewById(R.id.btMain_StartStopTrack);
		Chronometer chronometer = (Chronometer)
        	activity.findViewById(R.id.tvMain_Runtime);
		if (start) {
			LogUtils.info(OnClickButtonStartStopListener.class, "start button pressed.");
			StartTrackProgressDialog startTrackDialog = 
				new StartTrackProgressDialog(
					btMain_StartStopTrack, chronometer, oneTouchMode);
			startTrackDialog.run(activity, 
				txIdStarting, txIdStartingDone);
		} else {
			LogUtils.info(OnClickButtonStartStopListener.class, "stop button pressed.");
			StopTrackProgressDialog stopTrackDialog = 
				new StopTrackProgressDialog(
					btMain_StartStopTrack, chronometer, oneTouchMode);
			stopTrackDialog.run(activity, 
				txIdStopping, txIdStoppingDone);
		}
	}
}
