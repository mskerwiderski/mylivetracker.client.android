package de.msk.mylivetracker.client.android.checkpoint;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.util.TrackUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: CheckpointServiceThread
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-01-25	origin.
 * 
 */
public class CheckpointServiceThread extends AbstractServiceThread {

	private long lastLocationInfoId = -1;
	private String lastTrackId = null;
	private Integer lastCountUploaded = null;
	
	@Override
	public void init() throws InterruptedException {
		if (LocationInfo.get() != null) {
			this.lastLocationInfoId = LocationInfo.get().getId();
		}
		if (TrackStatus.get() != null) {
			this.lastTrackId = TrackStatus.get().getTrackId();
		}
		if (UploadInfo.get() != null) {
			this.lastCountUploaded = UploadInfo.get().getCountUploaded();
		}
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		TrackStatus status = TrackStatus.get();
		TrackingModePrefs trackingModePrefs = PrefsRegistry.get(TrackingModePrefs.class);
		
		boolean periodExpired = false; 
		boolean validPositionFound = false;
		boolean uploadDone = false;
		
		if (status.trackIsRunning() && 
			trackingModePrefs.getTrackingMode().equals(TrackingMode.Checkpoint)) {
			// check if maximum period time for getting a valid position has been expired.
			if ((new Date()).getTime() >
				(status.getLastStartedInMSecs() + trackingModePrefs.getMaxCheckpointPeriodInSecs() * 1000L)) {
				periodExpired = true;
			} else {
				// check if minimum one valid position was successfully sent.
				LocationInfo currLocationInfo = LocationInfo.get();
				if ((currLocationInfo != null) && 
					(currLocationInfo.getId() > this.lastLocationInfoId) &&
					currLocationInfo.hasValidLatLon() && currLocationInfo.isAccurate()) {
					validPositionFound = true;
				}
				String currTrackId = TrackStatus.get().getTrackId();
				UploadInfo uploadInfo = UploadInfo.get();
				Integer currCountUploaded = (uploadInfo == null) ? 
					null : uploadInfo.getCountUploaded();
				if (!StringUtils.equals(currTrackId, lastTrackId) && 
					(currCountUploaded != null) && 
					(currCountUploaded > 0)) {
					uploadDone = true;
				} else if (StringUtils.equals(currTrackId, lastTrackId) && 
					(currCountUploaded != null)) {
					if ((this.lastCountUploaded == null) && (currCountUploaded > 0)) {
						uploadDone = true;
					} else if ((this.lastCountUploaded != null) && 
						(currCountUploaded > this.lastCountUploaded)) {
						uploadDone = true;
					}
				}
			}
			
			if (periodExpired || (validPositionFound && uploadDone)) {
				TrackUtils.stopTrack();
			}
			
			this.init();
		}
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 1000;
	}

	@Override
	public void cleanUp() {
		// noop.
	}
}
