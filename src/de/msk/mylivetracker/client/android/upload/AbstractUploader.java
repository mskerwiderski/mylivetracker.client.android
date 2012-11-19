package de.msk.mylivetracker.client.android.upload;

import java.io.IOException;
import java.util.Date;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * AbstractUploader.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public abstract class AbstractUploader {

	private IProtocol protocol;
	
	public AbstractUploader(IProtocol protocol) {
		this.protocol = protocol;
	}

	public static final class UploadResult {
		// sucessfully uploaded and successfully processed on server side.
		private boolean processed = false; 
		// successfully uploaded (but maybe not successfully processed on server side).
		private boolean uploaded = false;
		// upload time in milliseconds (even if upload failed).
		private long uploadTimeInMSecs = 0;
		// upload failed, but data strings has been buffered for later upload.
		private boolean buffered = false;
		private int countPositions = 0;
		private String resultCode = "";
		public UploadResult(boolean processed,
			long uploadTimeInMSecs,
			int countPositions, String resultCode) {
			this(processed, uploadTimeInMSecs, false, countPositions, resultCode);
		}
		public UploadResult(boolean processed, 
			long uploadTimeInMSecs, boolean buffered, 
			int countPositions, String resultCode) {
			this.processed = processed;
			this.buffered = buffered;
			this.countPositions = countPositions; 
			this.uploaded = (this.countPositions > 0);
			this.uploadTimeInMSecs = uploadTimeInMSecs;
			this.resultCode = resultCode;
		}		
		public boolean isBuffered() {
			return buffered;
		}
		public boolean isProcessed() {
			return processed;
		}
		public boolean isUploaded() {
			return uploaded;
		}
		protected long getUploadTimeInMSecs() {
			return uploadTimeInMSecs;
		}
		public int getCountPositions() {
			return countPositions;
		}
		public String getResultCode() {
			return resultCode;
		}
		@Override
		public String toString() {
			return "[uploaded=" + uploaded + ", resultCode="
				+ resultCode + "]";
		}		
	}
	
	public abstract void finish();
	public abstract UploadResult upload(String dataStr);
	
	public void checkConnection() throws Exception {
		if (!MainActivity.get().isDataConnectionActive()) {
			throw new IOException("no data connection.");			
		}		
	}
	
	public UploadResult upload(		
		Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo,
		BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo,
		GpsStateInfo gpsStateInfo,
		HeartrateInfo heartrateInfo, 
		EmergencySignalInfo emergencySignalInfo,
		MessageInfo messageInfo) throws InterruptedException {
		Preferences preferences = Preferences.get();
		String dataStr = protocol.createDataStrForDataTransfer(
			lastInfoTimestamp, 
			phoneStateInfo, batteryStateInfo, 
			locationInfo, gpsStateInfo, 
			heartrateInfo, emergencySignalInfo, 
			messageInfo, 
			preferences.getUsername(), 
			preferences.getPassword());
		return this.upload(dataStr);
	}	
}
