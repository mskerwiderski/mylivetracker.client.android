package de.msk.mylivetracker.client.android.upload;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.Uploader.LastInfoDsc;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: UploadServiceThread
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class UploadServiceThread extends AbstractServiceThread {

	private AbstractUploader uploader;
	private boolean doUpload;
	private long lastUploaded;
	private LastInfoDsc lastInfoDsc;
	
	@Override
	public void init() throws InterruptedException {
		this.uploader = Uploader.createUploader();
		this.doUpload = true;
		this.lastUploaded = SystemClock.elapsedRealtime();
		this.lastInfoDsc = new LastInfoDsc();
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		ProtocolPrefs prefs = PrefsRegistry.get(ProtocolPrefs.class);
		LocationInfo locationInfo = LocationInfo.get();
		if (!this.doUpload && this.isRunOnlyOnce()) {
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
						LocationInfo.distance(locationInfo,
							this.lastInfoDsc.lastLocationInfo)) {
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
		if (this.doUpload && !TrackStatus.get().countdownIsActive()) {
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
	}

	@Override
	public boolean doStopService() {
		// TODO Auto-generated method stub
		return super.doStopService();
	}
}
