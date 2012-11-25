package de.msk.mylivetracker.client.android.automode;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryReceiver;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.TrackUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * AutoServiceThread.
 * 
 * @author michael skerwiderski, (c)2012
 * @since 1.3.0 (18.02.2012)
 * 
 * This class is responsible for the automatic actions:
 * o handles auto mode if enabled.
 * o handles auto start if enabled.
 * 
 */
public class AutoServiceThread extends AbstractServiceThread {

	@Override
	public void init() throws InterruptedException {
		// noop.
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		Preferences prefs = Preferences.get();
		TrackStatus status = TrackStatus.get();
		if (prefs.isAutoModeEnabled()) {
			if (BatteryReceiver.get().isBatteryCharging() &&
				!status.trackIsRunning()) {
				if (trackIsExpired()) {
					TrackUtils.resetTrack();
				}
				TrackUtils.startTrack();
			} else if (!BatteryReceiver.get().isBatteryCharging() && 
				status.trackIsRunning()) {
				TrackUtils.stopTrack();
				status.updateLastAutoModeStopSignalReceived();
			}
		} else if (prefs.isAutoStartEnabled() && !status.trackIsRunning()) {
			TrackUtils.startTrack();
		}
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 5000;
	}

	@Override
	public void cleanUp() {
		// noop.
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
}
