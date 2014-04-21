package de.msk.mylivetracker.client.android.appcontrol;

import android.app.Activity;
import android.content.Intent;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.checkpoint.CheckpointService;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.phonestate.PhoneStateReceiver;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.upload.UploadService;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: AppControl
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-02	origin.
 * 
 */
public class AppControl {

	private static boolean appRunning = false;
	
	public static boolean appRunning() {
		return appRunning;
	}
	
	public static void exitApp(int timeoutMSecs) {
		if (!appRunning()) {
			throw new IllegalStateException("application not running");
		}
		appRunning = false;
		Intent intent = new Intent();
		intent.setAction(AppControlReceiver.ACTION_APP_EXIT);
		intent.putExtra(AppControlReceiver.ACTION_PARAM_TIMEOUT_MSECS, timeoutMSecs);
		App.getCtx().sendBroadcast(intent);
	}
	
	public static void startApp() {
		if (appRunning()) {
			throw new IllegalStateException("application running");
		}
		appRunning = true;
		Intent intent = new Intent();
		intent.setAction(AppControlReceiver.ACTION_APP_START);
		App.getCtx().sendBroadcast(intent);
	}
	
	public static boolean trackIsRunning() {
		return TrackStatus.get().trackIsRunning();
	}
	
	private static void resetTrackAux() {
		AbstractService.stopService(UploadService.class);
		stopLocalizationAux();
		if (antPlusDetectionIsAvailable() && 
			antPlusDetectionIsRunning()) {
			stopAntPlusDetectionAux();
		}
		TrackStatus.reset();
	}
	
	public static void resetTrack() {
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "resetTrack", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					resetTrackAux();
				}
			};
			progressDlg.runOnUiThread(activity, 
				R.string.txMain_InfoResettingTracking, 
				R.string.txMain_InfoResetTrackDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "resetTrack", 
				"main activity active", (activity != null));
			resetTrackAux();
		}
	}
	
	private static void startTrackAux() {
		if (trackIsRunning()) {
			throw new IllegalStateException("track is already running.");
		}
		if (!BatteryReceiver.isRegistered()) {
			BatteryReceiver.register();
		}
		if (!PhoneStateReceiver.isRegistered()) {
			PhoneStateReceiver.register();
		}
		TrackingOneTouchMode mode = 
			PrefsRegistry.get(OtherPrefs.class).
			getTrackingOneTouchMode();
		switch (mode) {
			case TrackingLocalizationHeartrate:
				if (antPlusDetectionIsAvailable()) { 
					startAntPlusDetectionAux();
				}
			case TrackingLocalization:
				startLocalizationAux();
			case TrackingOnly:
			default:
				break;
		}
		if (!AbstractService.isServiceRunning(UploadService.class)) {
			AbstractService.startService(UploadService.class);
		}
		if (TrackingModePrefs.isCheckpoint() &&
			!AbstractService.isServiceRunning(CheckpointService.class)) {
			AbstractService.startService(CheckpointService.class);
		}
	}
	
	public static void startTrack() {
		if (trackIsRunning()) {
			throw new IllegalStateException("track is already running.");
		}
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "startTrack", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					startTrackAux();
				}
			};
			int txIdStarting = R.string.txMain_InfoStartingTracking;
			int txIdStartingDone = R.string.txMain_InfoStartTrackDone;
			if (TrackingModePrefs.isCheckpoint()) {
				txIdStarting = R.string.txMain_InfoStartingCheckpoint;
				txIdStartingDone = R.string.txMain_InfoStartCheckpointDone;
			}
			progressDlg.runOnUiThread(activity, txIdStarting, txIdStartingDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "startTrack", 
				"main activity active", (activity != null));
			startTrackAux();
		}
	}
	
	private static void stopTrackAux() {
		if (!trackIsRunning()) {
			throw new IllegalStateException("track is not running.");
		}
		if (AbstractService.isServiceRunning(CheckpointService.class)) {
			AbstractService.stopService(CheckpointService.class);
		}
		if (AbstractService.isServiceRunning(UploadService.class)) {
			AbstractService.stopService(UploadService.class);
		}
		TrackingOneTouchMode mode = 
			PrefsRegistry.get(OtherPrefs.class).
			getTrackingOneTouchMode();
		switch (mode) {
			case TrackingLocalizationHeartrate:
				if (antPlusDetectionIsAvailable()) { 
					stopAntPlusDetectionAux();
				}
			case TrackingLocalization:
				stopLocalizationAux();
			case TrackingOnly:
			default:
				break;
		}
		if (!MainActivity.exists() && 
			PhoneStateReceiver.isRegistered()) {
			PhoneStateReceiver.unregister();
		}
		if (BatteryReceiver.isRegistered()) {
			BatteryReceiver.unregister();
		}
	}
	
	public static void stopTrack() {
		if (!trackIsRunning()) {
			throw new IllegalStateException("track is not running.");
		}
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "stopTrack", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					stopTrackAux();
				}
			};
			int txIdStopping = R.string.txMain_InfoStoppingTracking;
			int txIdStoppingDone = R.string.txMain_InfoStopTrackDone;
			if (TrackingModePrefs.isCheckpoint()) {
				txIdStopping = R.string.txMain_InfoStoppingCheckpoint;
				txIdStoppingDone = R.string.txMain_InfoStopCheckpointDone;
			}
			progressDlg.runOnUiThread(activity, txIdStopping, txIdStoppingDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "stopTrack", 
				"main activity active", (activity != null));
			stopTrackAux();
		}
	}

	public static void startOrStopTrack() {
		if (trackIsRunning()) {
			stopTrack();
		} else {
			startTrack();
		}
	}
	
	public static boolean localizationIsRunning() {
		return AbstractService.isServiceRunning(LocalizationService.class);
	}
	
	private static void startLocalizationAux() {
		if (!AbstractService.isServiceRunning(LocalizationService.class)) {
			AbstractService.startService(LocalizationService.class);						
		}		
	}
	
	public static void startLocalization() {
		if (localizationIsRunning()) {
			throw new IllegalStateException("localization is already running.");
		}
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "startLocalization", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					startLocalizationAux();
				}
			};
			int txIdStarting = R.string.txMain_InfoStartingLocalization;
			int txIdStartingDone = R.string.txMain_InfoStartLocalizationDone;
			progressDlg.runOnUiThread(activity, txIdStarting, txIdStartingDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "startLocalization", 
				"main activity active", (activity != null));
			startLocalizationAux();
		}
	}
	
	private static void stopLocalizationAux() {
		if (AbstractService.isServiceRunning(LocalizationService.class)) {
			AbstractService.stopService(LocalizationService.class);						
		}		
	}

	public static void stopLocalization() {
		if (!localizationIsRunning()) {
			throw new IllegalStateException("localization is not running.");
		}
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "stopLocalization", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					stopLocalizationAux();
				}
			};
			int txIdStopping = R.string.txMain_InfoStoppingLocalization;
			int txIdStoppingDone = R.string.txMain_InfoStopLocalizationDone;
			progressDlg.runOnUiThread(activity, txIdStopping, txIdStoppingDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "stopLocalization", 
				"main activity active", (activity != null));
			stopLocalizationAux();
		}
	}
	
	public static void startOrStopLocalization() {
		if (localizationIsRunning()) {
			stopLocalization();
		} else {
			startLocalization();
		}
	}
	
	public static boolean antPlusDetectionIsAvailable() {
		return AntPlusHardware.initialized() &&
			PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable();
	}
	
	public static boolean antPlusDetectionIsRunning() {
		if (!antPlusDetectionIsAvailable()) {
			throw new RuntimeException("antplus not available.");
		}
		return AntPlusManager.get().hasSensorListeners();
	}
	
	private static void startAntPlusDetectionAux() {
		if (!antPlusDetectionIsAvailable()) {
			throw new RuntimeException("antplus not available.");
		}
		if (antPlusDetectionIsRunning()) {
			throw new RuntimeException("antplus already running.");
		}
		AntPlusManager.start();
	}

	public static void startAntPlusDetection() {
		if (!antPlusDetectionIsAvailable()) {
			throw new RuntimeException("antplus not available.");
		}
		if (antPlusDetectionIsRunning()) {
			throw new IllegalStateException("localization is already running.");
		}
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "startAntPlusDetection", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					startAntPlusDetectionAux();
				}
			};
			int txIdStarting = R.string.txMain_InfoStartingAntPlus;
			int txIdStartingDone = R.string.txMain_InfoStartAntPlusDone;
			progressDlg.runOnUiThread(activity, txIdStarting, txIdStartingDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "startAntPlusDetection", 
				"main activity active", (activity != null));
			startAntPlusDetectionAux();
		}
	}
	
	protected static void stopAntPlusDetectionAux() {
		if (!antPlusDetectionIsAvailable()) {
			throw new RuntimeException("antplus not available.");
		}
		if (!antPlusDetectionIsRunning()) {
			throw new RuntimeException("antplus not running.");
		}
		AntPlusManager.stop();
	}
	
	public static void stopAntPlusDetection() {
		if (!antPlusDetectionIsAvailable()) {
			throw new RuntimeException("antplus not available.");
		}
		if (!antPlusDetectionIsRunning()) {
			throw new IllegalStateException("antplus is not running.");
		}
		final Activity activity = MainActivity.getActive();
		if (activity != null) {
			LogUtils.infoMethodState(AppControl.class, "stopAntPlusDetection", 
				"main activity active", (activity != null));
			final AbstractProgressDialog<Activity> progressDlg = new AbstractProgressDialog<Activity>() {
				@Override
				public void doTask(Activity activity) {
					stopAntPlusDetectionAux();
				}
			};
			int txIdStopping = R.string.txMain_InfoStoppingAntPlus;
			int txIdStoppingDone = R.string.txMain_InfoStopAntPlusDone;
			progressDlg.runOnUiThread(activity, txIdStopping, txIdStoppingDone);
		} else {
			LogUtils.infoMethodState(AppControl.class, "stopAntPlusDetection", 
				"main activity active", (activity != null));
			stopAntPlusDetectionAux();
		}
	}
	
	public static void startOrStopAntPlusDetection() {
		if (!antPlusDetectionIsAvailable()) {
			throw new RuntimeException("antplus not available.");
		}
		if (antPlusDetectionIsRunning()) {
			stopAntPlusDetection();
		} else {
			startAntPlusDetection();
		}
	}
}
