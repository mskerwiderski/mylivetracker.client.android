package de.msk.mylivetracker.client.android.upload;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.Uploader.LastInfoDsc;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * UploadServiceThread.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 001 2012-02-04 lastUsedLocationProvider added.
 * 000 2011-08-26 initial.
 * 
 */
public class UploadServiceThread extends AbstractServiceThread {

	private AbstractUploader uploader;
	private boolean doUpload;
	private long lastUploaded;
	private LastInfoDsc lastInfoDsc;
	private boolean runOnlyOneSinglePass;
	
	public UploadServiceThread() {
		this.runOnlyOneSinglePass = false;
	}
	public UploadServiceThread(boolean runOnlyOneSinglePass) {
		this.runOnlyOneSinglePass = runOnlyOneSinglePass;
	}
	
	@Override
	public void init() throws InterruptedException {
		TrackStatus.get().markAsStarted();
		this.uploader = Uploader.createUploader();
		this.doUpload = true;
		this.lastUploaded = SystemClock.elapsedRealtime();
		this.lastInfoDsc = new LastInfoDsc();
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		Preferences prefs = Preferences.get();
		LocationInfo locationInfo = LocationInfo.get();
		if (!this.doUpload && this.runOnlyOneSinglePass) {
			this.doUpload = true;
		}
		if (!this.doUpload && 
			(this.lastInfoDsc.lastLocationInfo == null) && 
			(locationInfo != null)) {
			this.doUpload = true;
		}
		int timeTrigger = prefs.getUplTimeTrigger().getSecs();
		int distanceTrigger = prefs.getUplDistanceTrigger().getMtrs();
		if (!this.doUpload && (timeTrigger == 0) && (distanceTrigger == 0)) {
			this.doUpload = true;
		}
		if (!this.doUpload) {
			boolean timeConditionFulfilled = false;
			if (timeTrigger > 0) {
				if ((this.lastUploaded + (timeTrigger * 1000)) <=
					SystemClock.elapsedRealtime()) {
					timeConditionFulfilled = true;
				}
			}
			boolean distanceConditionFulfilled = false;
			if (distanceTrigger > 0) {					
				if ((locationInfo != null) && 
					(this.lastInfoDsc.lastLocationInfo != null)) {
					if (distanceTrigger <=
						locationInfo.getLocation().distanceTo(
							this.lastInfoDsc.lastLocationInfo.getLocation())) {
						distanceConditionFulfilled = true;
					}
				}
			}
			boolean triggerLogicIsAND = prefs.getUplTriggerLogic().AND();
			if ((timeTrigger == 0) || (distanceTrigger == 0)) {
				triggerLogicIsAND = false;
			}
			if (triggerLogicIsAND) {
				this.doUpload = timeConditionFulfilled && distanceConditionFulfilled;
			} else {
				this.doUpload = timeConditionFulfilled || distanceConditionFulfilled;
			}
		}
		if (this.doUpload) {
			Uploader.upload(this.uploader, this.lastInfoDsc);
			lastUploaded = SystemClock.elapsedRealtime();
			doUpload = false;
		}
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 50;
	}

	@Override
	public void cleanUp() {
		TrackStatus.get().markAsStopped();
	}

	@Override
	public boolean runOnlyOneSinglePass() {
		return runOnlyOneSinglePass;
	}
}
