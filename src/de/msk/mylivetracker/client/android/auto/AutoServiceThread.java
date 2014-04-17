package de.msk.mylivetracker.client.android.auto;

import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
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
		// noop.
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		TrackStatus status = TrackStatus.get();
		BatteryStateInfo batteryStateInfo = BatteryStateInfo.get();
		boolean battFullOrCharging = (batteryStateInfo == null) ? false :
			batteryStateInfo.fullOrCharging();
		if (TrackingModePrefs.isAuto()) {
			if (battFullOrCharging &&
				!AppControl.trackIsRunning()) {
				if (trackIsExpired()) {
					AppControl.resetTrack();
				}
				AppControl.startTrack();
			} else if (!battFullOrCharging && 
				AppControl.trackIsRunning()) {
				AppControl.stopTrack();
				status.updateLastAutoModeStopSignalReceived();
			}
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
		TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
		Long lastStopSignal = TrackStatus.get().getLastAutoModeStopSignalReceived();
		if (lastStopSignal == null) return res;
		int resetTrackMode = prefs.getAutoModeResetTrackMode().getVal();
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
