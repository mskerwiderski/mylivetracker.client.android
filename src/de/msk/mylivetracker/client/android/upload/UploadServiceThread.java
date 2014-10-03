package de.msk.mylivetracker.client.android.upload;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.message.MessageActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.upload.Uploader.LastInfoDsc;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.TimeUtils;
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
		this.lastUploaded = TimeUtils.getElapsedTimeInMSecs();
		this.lastInfoDsc = new LastInfoDsc();
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		if (TrackingModePrefs.isStandard() || TrackingModePrefs.isAuto()) {
			runSinglePassTrackingModeStandardOrAuto();
		} else if (TrackingModePrefs.isCheckpoint()) {
			runSinglePassTrackingModeCheckpoint();
		} else {
			throw new IllegalStateException("illegal tracking mode: " + 
				PrefsRegistry.get(TrackingModePrefs.class).getTrackingMode().name());
		}
	}
	
	public void runSinglePassTrackingModeStandardOrAuto() throws InterruptedException {
		if (!TrackingModePrefs.isStandard() && !TrackingModePrefs.isAuto()) {
			throw new IllegalStateException("illegal tracking mode: " + 
				PrefsRegistry.get(TrackingModePrefs.class).getTrackingMode().name());
		}
		if (!TrackStatus.get().countdownIsActive()) {
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
						TimeUtils.getElapsedTimeInMSecs()) {
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
			if (this.doUpload) {
				Uploader.upload(this.uploader, this.lastInfoDsc);
				lastUploaded = TimeUtils.getElapsedTimeInMSecs();
				doUpload = false;
			}
		}
	}

	private void uploadCheckpoint() throws InterruptedException {
		if (TrackingModePrefs.hasCheckpointMessage()) {
			String message = PrefsRegistry.get(TrackingModePrefs.class).getCheckpointMessage();
			MessageInfo.update(message);
			MessageActivity.sendMessageAsSmsIfConfigured(message);
		}
		Uploader.upload(this.uploader, this.lastInfoDsc);
	}
	
	public void runSinglePassTrackingModeCheckpoint() throws InterruptedException {
		if (!TrackingModePrefs.isCheckpoint()) {
			throw new IllegalStateException("illegal tracking mode: " + 
				PrefsRegistry.get(TrackingModePrefs.class).getTrackingMode().name());
		}
		Long lastStartedInMSecs = TrackStatus.get().getLastStartedInMSecs();
		if (AppControl.trackIsRunning() && (this.lastInfoDsc.lastLocationInfo == null)) {
			TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
			LocationInfo locationInfo = LocationInfo.get();
			if ((locationInfo != null) && 
				locationInfo.hasValidLatLon() && 
				locationInfo.isAccurate()) {
				uploadCheckpoint();
				LogUtils.infoMethodState(UploadServiceThread.class, 
					"runSinglePassTrackingModeCheckpoint", "status", "accurate location sent");
				AppControl.stopTrack();
			} else if ((TimeUtils.getElapsedTimeInMSecs() - 
				lastStartedInMSecs) > 
				prefs.getMaxCheckpointPeriodInSecs() * 1000L) {
				if ((locationInfo != null) && 
					locationInfo.hasValidLatLon() &&
					prefs.isSendAnyValidLocationBeforeTimeout()) {
					uploadCheckpoint();
					LogUtils.infoMethodState(UploadServiceThread.class, 
						"runSinglePassTrackingModeCheckpoint", "status", "valid location sent before timeout");
				}
				AppControl.stopTrack();
			}
		}
	}
	
	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 50;
	}

	@Override
	public void cleanUp() {
	}
}
