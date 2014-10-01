package de.msk.mylivetracker.client.android.appcontrol;

import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Intent;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.antplus.AntPlusHeartrateListener;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.auto.AutoService;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.updater.ViewUpdateService;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
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
 * Following services are used and need to be controlled programatically:
 * o ViewUpdateService --> controls frontend data updates
 *   --> running only if frontend is active
 * o UploadService --> controls data upload
 *   --> running only if tracking is active
 * o AutoService --> controls tracking mode 'Auto'
 *   --> running only if tracking mode is set to 'Auto'
 * o CheckpointService --> controls tracking mode 'Checkpoint'
 *   --> running if tracking mode is set to 'Checkpoint' if tracking is active
 * o LocalizationService --> controls localization
 *   --> running only if user has started this service
 * 
 * Following receivers are used and need to be controlled programatically:
 * o PhoneStateReceiver --> receives updates of phone state
 *   --> running if frontend is active or tracking is active
 * o BatteryReceiver --> receives updates of battery state
 *   --> running if frontend is active or tracking is active or tracking mode is set to 'Auto' 
 * 
 * Following receivers are auto control and need not started or stopped programatically.
 * o LaunchAppBroadcast --> called after reboot of device (currently only for tracking mode 'Auto' relevant)
 * o AppControlReceiver --> called when app is exiting
 * o RemoteSmsCmdReceiver --> called when a sms command was received
 * o SmsSentStatusReceiver --> called when a sms was sent or delivered by the app
 *  
 */
public class AppControl {

	public static boolean appRunning() {
		return
			appRunningFrontend() ||
			appRunningHidden();
	}
	
	public static boolean appRunningFrontend() {
		return MainActivity.exists();
	}
	
	public static boolean appRunningHidden() {
		return
			AbstractService.isServiceRunning(ViewUpdateService.class) ||
			AbstractService.isServiceRunning(UploadService.class) ||
			AbstractService.isServiceRunning(AutoService.class) ||
			AbstractService.isServiceRunning(LocalizationService.class);
	}
	
	protected static void stopAllAppsActivities() {
		AbstractService.stopService(AutoService.class);
		AppControl.stopTrack();
		AppControl.stopLocalization();
		AppControl.stopAntPlusDetection();
		BatteryReceiver.unregister();
		PhoneStateReceiver.unregister();
		AbstractService.stopService(ViewUpdateService.class);
	}
	
	public static void exitApp(int timeoutMSecs) {
		if (!appRunning()) {
			throw new IllegalStateException("application not running");
		}
		if (!MainActivity.exists()) {
			stopAllAppsActivities();
		} else {
			Intent intent = new Intent();
			intent.setAction(AppControlReceiver.ACTION_APP_EXIT);
			intent.putExtra(AppControlReceiver.ACTION_PARAM_TIMEOUT_MSECS, timeoutMSecs);
			App.getCtx().sendBroadcast(intent);
		}
	}
	
	public static void startApp() {
		if (MainActivity.exists()) {
			throw new IllegalStateException("application already running");
		}
		Intent intent = new Intent(App.getCtx(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		App.getCtx().startActivity(intent);
	}
	
	private final static ReentrantLock lockTrackStartStop = new ReentrantLock();
	
	public static boolean trackIsRunning() {
		boolean res = false;
		lockTrackStartStop.lock();
		try {
			res = TrackStatus.get().trackIsRunning();
		} finally {
			lockTrackStartStop.unlock();
		}
		return res;
	}
	
	private static void resetTrackAux() {
		lockTrackStartStop.lock();
		try {
			AbstractService.stopService(UploadService.class);
			AbstractService.stopService(LocalizationService.class);
			stopAntPlusDetection();
			TrackStatus.reset();
		} finally {
			lockTrackStartStop.unlock();
		}
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
		lockTrackStartStop.lock();
		try {
			if (!TrackStatus.get().trackIsRunning()) {
				PhoneStateReceiver.register();
		        BatteryReceiver.register();
				TrackingOneTouchMode mode = 
					PrefsRegistry.get(OtherPrefs.class).
					getTrackingOneTouchMode();
				switch (mode) {
					case TrackingLocalizationHeartrate:
						startAntPlusDetection();
					case TrackingLocalization:
						startLocalization();
					case TrackingOnly:
					default:
						break;
				}
				AbstractService.startService(UploadService.class);
				TrackStatus.get().markAsStarted();
			}
		} finally {
			lockTrackStartStop.unlock();
		}
	}
	
	public static void startTrack() {
		if (!TrackStatus.get().trackIsRunning()) {
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
	}
	
	private static void stopTrackAux() {
		lockTrackStartStop.lock();
		try {
			if (TrackStatus.get().trackIsRunning()) {
				if (AbstractService.isServiceRunning(UploadService.class)) {
					AbstractService.stopService(UploadService.class);
				}
				TrackStatus.get().markAsStopped();
				TrackingOneTouchMode mode = 
					PrefsRegistry.get(OtherPrefs.class).
					getTrackingOneTouchMode();
				switch (mode) {
					case TrackingLocalizationHeartrate:
						stopAntPlusDetection();
					case TrackingLocalization:
						stopLocalization();
					case TrackingOnly:
					default:
						break;
				}
				if (!MainActivity.exists()) {
					TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
					if (!TrackingModePrefs.isAuto() || !prefs.isRunOnlyIfBattFullOrCharging()) {
						BatteryReceiver.unregister();
					}
					PhoneStateReceiver.unregister();;
				}
			}
		} finally {
			lockTrackStartStop.unlock();
		} 
	}
	
	public static void stopTrack() {
		if (TrackStatus.get().trackIsRunning()) {
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
	}

	private final static ReentrantLock lockLocalizationStartStop = new ReentrantLock();
	
	public static boolean localizationIsRunning() {
		boolean res = false;
		lockLocalizationStartStop.lock();
		try {
			res = AbstractService.isServiceRunning(LocalizationService.class);
		} finally {
			lockLocalizationStartStop.unlock();
		}
		return res;
	}
	
	private static void startLocalizationAux() {
		lockLocalizationStartStop.lock();
		try {
			AbstractService.startService(LocalizationService.class);
		} finally {
			lockLocalizationStartStop.unlock();
		}
	}
	
	public static void startLocalization() {
		if (!AbstractService.isServiceRunning(LocalizationService.class)) {
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
	}
	
	private static void stopLocalizationAux() {
		lockLocalizationStartStop.lock();
		try {
			AbstractService.stopService(LocalizationService.class);
		} finally {
			lockLocalizationStartStop.unlock();
		}
	}
	
	public static void stopLocalization() {
		if (AbstractService.isServiceRunning(LocalizationService.class)) {
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
	}
	
	private final static ReentrantLock lockAntPlusDetectionStartStop = new ReentrantLock();
	
	public static boolean antPlusDetectionIsAvailable() {
		return AntPlusHardware.initialized() &&
			PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable();
	}
	
	public static boolean antPlusDetectionIsRunning() {
		lockAntPlusDetectionStartStop.lock();
		boolean res = false;
		try {
			res = antPlusDetectionIsAvailable() &&
				AntPlusManager.get().hasSensorListeners();
		} finally {
			lockAntPlusDetectionStartStop.unlock();
		}
		return res;
	}
	
	public static void startAntPlusDetectionAux() {
		lockAntPlusDetectionStartStop.lock();
		try {
			if (!(antPlusDetectionIsAvailable() &&
				AntPlusManager.get().hasSensorListeners())) {
				AntPlusManager.get().requestSensorUpdates(
					AntPlusHeartrateListener.get());
			}
		} finally {
			lockAntPlusDetectionStartStop.unlock();
		}
	}
	
	public static void startAntPlusDetection() {
		if (!(antPlusDetectionIsAvailable() &&
			AntPlusManager.get().hasSensorListeners())) {
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
	}
	
	public static void stopAntPlusDetectionAux() {
		lockAntPlusDetectionStartStop.lock();
		try {
			if (antPlusDetectionIsAvailable() &&
				AntPlusManager.get().hasSensorListeners()) {
				AntPlusManager.get().removeUpdates(
					AntPlusHeartrateListener.get());
			}
		} finally {
			lockAntPlusDetectionStartStop.unlock();
		}
	}
	
	public static void stopAntPlusDetection() {
		if (antPlusDetectionIsAvailable() &&
			AntPlusManager.get().hasSensorListeners()) {
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
	}
}
