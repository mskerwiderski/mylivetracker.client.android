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
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * AutoManager.
 * 
 * @author michael skerwiderski, (c)2012
 * @since 1.3.0 (18.02.2012)
 * 
 * This class is responsible for the automatic actions:
 * o handles auto mode if enabled.
 * o handles auto start if enabled.
 * 
 */
public class AutoManager extends Thread {

	private static AutoManager autoManager = null;
	
	private boolean running = false;
	
	private boolean isRunning() {
		return this.running;
	}
	private synchronized void setRunning(boolean running) {
		this.running = running;
	}
	
	protected static void startAutoManager() {
		if (autoManager == null) {
			LogUtils.info("startAutoManager...");
			autoManager = new AutoManager();
			autoManager.start();
			LogUtils.info("startAutoManager...started.");
		}
	}
	
	protected static void stopAutoManager() {
		while (autoManager != null) {
			LogUtils.info("stopAutoManager...");
			autoManager.setRunning(false);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// noop.
			}			
			if ((autoManager != null) && 
				!autoManager.isAlive()) {
				autoManager = null;
			}
		}
		LogUtils.info("stopAutoManager...stopped.");
	}
	
	protected static boolean isAutoManagerRunning() {
		boolean res = false;
		if (autoManager != null) {
			res = autoManager.isRunning();
		}
		return res;
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
		this.setRunning(true);
		while (this.isRunning()) {
			try {
				Preferences prefs = Preferences.get();
				TrackStatus status = TrackStatus.get();
				if (prefs.isAutoModeEnabled()) {
					if (BatteryReceiver.get().isBatteryCharging() && 
						!status.trackIsRunning()) {
						if (trackIsExpired()) {
							MainActivity.get().runOnUiThread(new ResetTrackTask());
						}
						MainActivity.get().runOnUiThread(new LocalizationTask(true));
						MainActivity.get().runOnUiThread(new TrackingTask(true));
					} else if (!BatteryReceiver.get().isBatteryCharging() && 
						status.trackIsRunning()) {
						MainActivity.get().runOnUiThread(new TrackingTask(false));
						MainActivity.get().runOnUiThread(new LocalizationTask(false));
						status.updateLastAutoModeStopSignalReceived();
					}
				} else if (prefs.isAutoStartEnabled() && !status.trackIsRunning()) {
					MainActivity.get().runOnUiThread(new LocalizationTask(true));
					MainActivity.get().runOnUiThread(new TrackingTask(true));
				}
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				this.setRunning(false);
			} catch (Exception e) {
				// noop.
			}
		}
	}
}
