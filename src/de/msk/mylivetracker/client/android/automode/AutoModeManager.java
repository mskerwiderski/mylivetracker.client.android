package de.msk.mylivetracker.client.android.automode;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonLocationListenerOnOffListener;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonResetListener;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonStartStopListener;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.receiver.BatteryReceiver;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * AutoModeManager.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-02-18 initial.
 * 
 */
public class AutoModeManager extends Thread {

	private static AutoModeManager autoModeManager = null;
	
	public static AutoModeManager get() {
		if (autoModeManager == null) {
			autoModeManager = new AutoModeManager();	
			autoModeManager.start();
			MainActivity.logInfo("AutoModeManager started.");
		} 
		return autoModeManager;
	}

	public static void shutdown() {
		if (autoModeManager == null) return;
		autoModeManager.interrupt();
		autoModeManager = null;
		MainActivity.logInfo("AutoModeManager stopped.");
	}
	
	private static class ResetTrackTask implements Runnable {
		@Override
		public void run() {
			OnClickButtonResetListener.resetTrack(MainActivity.get());
		}
	}
	
	private static class LocalizationTask implements Runnable {
		private boolean start = true;
		
		public LocalizationTask(boolean start) {
			this.start = start;
		}

		@Override
		public void run() {
			OnClickButtonLocationListenerOnOffListener.
				startStopLocationListener(MainActivity.get(), 
				this.start);
		}
	}
	
	private static class TrackingTask implements Runnable {
		private boolean start = true;
		
		public TrackingTask(boolean start) {
			this.start = start;
		}

		@Override
		public void run() {
			OnClickButtonStartStopListener.
				startStopTrack(MainActivity.get(), 
				this.start, false);
		}
	}
	
	private boolean trackIsExpired() {
		boolean res = false;
		Long lastStopSignal = TrackStatus.get().getLastAutoModeStopSignalReceived();
		if (lastStopSignal == null) return res;
		int resetTrackMode = Preferences.get().getAutoModeResetTrackMode().getVal();
		if (resetTrackMode != 0) {
			if (resetTrackMode == -1) {
				res = false;
				String now = (new DateTime()).getAsStr(
					TimeZone.getDefault(), 
					DateTime.STD_DATE_FORMAT);
				String updated = (new DateTime(lastStopSignal)).getAsStr(
					TimeZone.getDefault(), 
					DateTime.STD_DATE_FORMAT);	
				res = !StringUtils.equals(now, updated);
			} else {
				DateTime now = new DateTime();
				if (now.getAsMSecs() - lastStopSignal >= 
					resetTrackMode * 60 * 60 * 1000) {
					res = true;
				}
			}
		}
		return res;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		boolean run = true;
		while (run) {
			try {
				Preferences prefs = Preferences.get();
				TrackStatus status = TrackStatus.get();
				if (prefs.isAutoModeEnabled()) {
					MainActivity.logInfo("AutoModeManager: enabled.");
					if (BatteryReceiver.get().isBatteryCharging() && 
						!status.trackIsRunning()) {
						MainActivity.logInfo("AutoModeManager: battery is charging, but track is not running.");
						if (trackIsExpired()) {
							MainActivity.logInfo("AutoModeManager: reset the track.");
							MainActivity.get().runOnUiThread(new ResetTrackTask());
							MainActivity.logInfo("AutoModeManager: track resetted.");
						}
						MainActivity.logInfo("AutoModeManager: start localization.");
						MainActivity.get().runOnUiThread(new LocalizationTask(true));
						MainActivity.logInfo("AutoModeManager: localization started.");
						MainActivity.logInfo("AutoModeManager: start tracking.");
						MainActivity.get().runOnUiThread(new TrackingTask(true));
						MainActivity.logInfo("AutoModeManager: tracking started.");
					} else if (!BatteryReceiver.get().isBatteryCharging() && 
						status.trackIsRunning()) {
						MainActivity.logInfo("AutoModeManager: battery is not charging, but track is running.");
						MainActivity.logInfo("AutoModeManager: stop tracking.");
						MainActivity.get().runOnUiThread(new TrackingTask(false));
						MainActivity.logInfo("AutoModeManager: tracking stopped.");
						MainActivity.logInfo("AutoModeManager: stop localization.");
						MainActivity.get().runOnUiThread(new LocalizationTask(false));
						MainActivity.logInfo("AutoModeManager: localization stopped.");
						status.updateLastAutoModeStopSignalReceived();
					}
				} else {
					MainActivity.logInfo("AutoModeManager: not enabled.");
				}
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				run = false;
				MainActivity.logInfo("AutoModeManager interrupted.");
			} catch (Exception e) {
				// ...
			}
		}
	}
}
