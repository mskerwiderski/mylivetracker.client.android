package de.msk.mylivetracker.client.android.auto;

import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: AutoServiceThread
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class AutoServiceThread extends AbstractServiceThread {

	@Override
	public void init() throws InterruptedException {
		TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
		if (prefs.isRunOnlyIfBattFullOrCharging()) {
			BatteryReceiver.register();
			LogUtils.infoMethodState(AutoServiceThread.class, "init",
				"battery receiver", "registered");
		}
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		LogUtils.infoMethodIn(AutoServiceThread.class, "runSinglePass");
		TrackStatus status = TrackStatus.get();
		BatteryStateInfo batteryStateInfo = BatteryStateInfo.get();
		LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
			"batteryStateInfo available", batteryStateInfo != null ? batteryStateInfo.toString() : "no");
		boolean battFullOrCharging = (batteryStateInfo == null) ? false :
			batteryStateInfo.fullOrCharging();
		LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
			"battFullOrCharging", battFullOrCharging);
		boolean runTracking = !status.isTrackInterruptedByUserInTrackingModeAuto() && 
			(!PrefsRegistry.get(TrackingModePrefs.class).isRunOnlyIfBattFullOrCharging() || 
			battFullOrCharging);
		LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
			"runTracking", runTracking);
		LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
			"isTrackingInterrupted", status.isTrackInterruptedByUserInTrackingModeAuto());
		
		if (trackIsExpired()) {
			AppControl.resetTrack();
			LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
				"track", "expired and therefore resetted");
		}
		if (runTracking &&
			!AppControl.trackIsRunning()) {
			AppControl.startTrack();
			LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
				"track", "started");
		} else if (!runTracking && 
			AppControl.trackIsRunning()) {
			AppControl.stopTrack();
			LogUtils.infoMethodState(AutoServiceThread.class, "runSinglePass",
				"track", "stopped");
		}
		LogUtils.infoMethodOut(AutoServiceThread.class, "runSinglePass");
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 500;
	}

	@Override
	public void cleanUp() {
		AppControl.stopTrack();
		if (!MainActivity.exists()) {
			BatteryReceiver.unregister();
		}
	}

	private boolean trackIsExpired() {
		boolean res = false;
		TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
		int resetTrackMode = prefs.getAutoModeResetTrackMode().getVal();
		if (resetTrackMode != 0) {
			if (resetTrackMode == -1) {
				Long trackStarted = TrackStatus.get().getStartedInMSecs();
				if (trackStarted != null) {
					String now = (new DateTime()).getAsStr(
						TimeZone.getDefault(), 
						DateTime.STD_DATE_FORMAT);
					String updated = (new DateTime(
						trackStarted)).getAsStr(
						TimeZone.getDefault(), 
						DateTime.STD_DATE_FORMAT);	
					res = !StringUtils.equals(now, updated);
				}
			} else if (!AppControl.trackIsRunning()) {
				Long lastStopped = TrackStatus.get().getLastStoppedInMSecs();
				if (lastStopped != null) {
					DateTime now = new DateTime();
					if (now.getAsMSecs() - lastStopped > 
						resetTrackMode * 60 * 60 * 1000) {
						res = true;
					}
				}
			}
		}
		return res;
	}
}
