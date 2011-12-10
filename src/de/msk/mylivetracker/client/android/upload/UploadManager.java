package de.msk.mylivetracker.client.android.upload;

import java.util.Date;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.Preferences.TransferProtocol;
import de.msk.mylivetracker.client.android.status.AbstractInfo;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.NmeaInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.upload.AbstractUploader.UploadResult;
import de.msk.mylivetracker.client.android.upload.protocol.Protocols;

/**
 * UploadManager.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class UploadManager extends Thread {

	private static UploadManager uploadManagerForTracking = null;
	@SuppressWarnings("unused")
	private static UploadManager uploadManagerOnlyOneTime = null;
	
	private static AbstractUploader createUploader() {
		AbstractUploader uploader = null;
		Preferences prefs = Preferences.get();		
		if (prefs.getTransferProtocol().equals(TransferProtocol.uploadDisabled)) {
			uploader = null;
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.mltHttpPlain)) {
			uploader = new HttpUploader(Protocols.createProtocolMltUrlparams());
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.mltTcpEncrypted)) {
			uploader = new TcpUploader(Protocols.createProtocolMltDatastrEncrypted(), true);
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.fransonGpsGateHttp)) {
			uploader = new TcpUploader(Protocols.createProtocolFransonGpsGateHttp(), false);
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.tk102Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolXexunTk102(), false);
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.tk5000Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolIncutexTk5000(), false);
		} else {
			throw new RuntimeException();
		}
		return uploader;
	}
	
	public static void startUploadManager() {
		TrackStatus.get().markAsStarted();
		runUploadThread(false);
	}
	
	public static void stopUploadManager() {
		while (uploadManagerForTracking != null) {
			MainActivity.logInfo("stopUploadManager: uploadManagerForTracking is running.");
			uploadManagerForTracking.interrupt();
			MainActivity.logInfo("stopUploadManager: uploadManagerForTracking:interrupt called.");
			try {
				MainActivity.logInfo("stopUploadManager: uploadManagerForTracking wait...");
				Thread.sleep(300);
			} catch (InterruptedException e) {
				MainActivity.logInfo("stopUploadManager: uploadManagerForTracking wait... interrupted.");
			}			
			if (!uploadManagerForTracking.isAlive()) {
				uploadManagerForTracking = null;
				MainActivity.logInfo("stopUploadManager: uploadManagerForTracking NOT alived - done.");
			}
		}
		TrackStatus.get().markAsStopped();
	}
	
	public static void uploadOnlyOneTime() {
		if ((uploadManagerForTracking != null) && 
			uploadManagerForTracking.isAlive()) {			
			uploadManagerForTracking.setSendImmediately();
			MainActivity.logInfo("uploadOnlyOneTime: used running UploaderThread.");
		} else {
			runUploadThread(true);
			MainActivity.logInfo("uploadOnlyOneTime: started UploaderThread for only a single upload.");
		}
	}
	
	private static void runUploadThread(boolean onlyOneUpload) {
		AbstractUploader uploader = createUploader();
		MainActivity.logInfo("runUploadThread: " + onlyOneUpload);
		if (uploader != null) {
			UploadManager uploadManager = new UploadManager(uploader, onlyOneUpload);
			uploadManager.start();
			if (onlyOneUpload) {
				uploadManagerOnlyOneTime = uploadManager;
			} else {
				uploadManagerForTracking = uploadManager;
			}				
			MainActivity.logInfo("runUploadThread: thread started");
		} else {
			// data transfer disabled.
		}
	}
	
	private AbstractUploader uploader = null;
	private boolean onlyOneUpload = false;
	
	private static LocationInfo lastLocationInfo = null;
	private static EmergencySignalInfo lastEmergencySignalInfo = null;
	private static MessageInfo lastMessageInfo = null;
	
	private UploadManager(AbstractUploader uploader, boolean onlyOneUpload) {
		this.uploader = uploader;
		this.onlyOneUpload = onlyOneUpload;
	}	
	
	private static Date updateLastInfoTimestamp(
		AbstractInfo info, Date lastInfoTimestamp) {		
		if ((info == null) || (info.getTimestamp() == null)) 
			return lastInfoTimestamp;
		Date timestamp = info.getTimestamp();
		if (lastInfoTimestamp == null) {
			lastInfoTimestamp = timestamp;
		} else if (lastInfoTimestamp.getTime() < 
			timestamp.getTime()) {
			lastInfoTimestamp = timestamp;
		}
		return lastInfoTimestamp;
	}	
	
	private void upload(AbstractUploader uploader) {
		long start = SystemClock.elapsedRealtime();		
		Date lastInfoTimestamp = null;
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		BatteryStateInfo batteryStateInfo = BatteryStateInfo.get();
		LocationInfo locationInfo = LocationInfo.get();
		NmeaInfo nmeaInfo = NmeaInfo.get();
		GpsStateInfo gpsStateInfo = GpsStateInfo.get();
		HeartrateInfo heartrateInfo = HeartrateInfo.get();
		EmergencySignalInfo emergencySignalInfo = EmergencySignalInfo.get();
		MessageInfo messageInfo = MessageInfo.get();
		
		lastInfoTimestamp = 
			updateLastInfoTimestamp(phoneStateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(batteryStateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(locationInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(nmeaInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(gpsStateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(heartrateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(emergencySignalInfo, lastInfoTimestamp);
		
		if ((emergencySignalInfo != null) && 
			!emergencySignalInfo.isUpToDate(lastEmergencySignalInfo)) {
			emergencySignalInfo = null;
		} else {
			lastInfoTimestamp = 
				updateLastInfoTimestamp(emergencySignalInfo, lastInfoTimestamp);
		}
		
		if ((messageInfo != null) && 
			!messageInfo.isUpToDate(lastMessageInfo)) {
			messageInfo = null;
		} else {
			lastInfoTimestamp = 
				updateLastInfoTimestamp(messageInfo, lastInfoTimestamp);
		}		
		
		if (lastInfoTimestamp == null) {
			lastInfoTimestamp = new Date();
		}
		
		UploadResult uploadResult = 
			uploader.upload(
				lastInfoTimestamp,
				phoneStateInfo,
				batteryStateInfo,
				locationInfo,
				nmeaInfo,
				gpsStateInfo,
				heartrateInfo, 
				emergencySignalInfo,
				messageInfo);
		
		if (uploadResult.isUploaded() || uploadResult.isBuffered()) {
			if (locationInfo != null) {
				lastLocationInfo = locationInfo;
			}
			if (emergencySignalInfo != null) {
				lastEmergencySignalInfo = emergencySignalInfo;
			}
			if (messageInfo != null) {
				lastMessageInfo = messageInfo;
			}						
		}		
		
		long stop = SystemClock.elapsedRealtime();	
		
		UploadInfo.update(uploadResult.isProcessed(), 
			uploadResult.getResultCode(), 
			uploadResult.getCountPositions(), 
			stop - start);
		
		MainActivity.get().updateView();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (this.onlyOneUpload) {
			this.runOnlyOneUpload();
		} else {
			this.runInfinite();
		}
		
		this.uploader.finish();
	}
	
	private void runOnlyOneUpload() {
		try {
			this.upload(this.uploader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean sendImmediately = false;
	public void setSendImmediately() {
		this.sendImmediately = true;
	}
	
	private void runInfinite() {
		Preferences prefs = Preferences.get();
		boolean run = true;
		// do upload at start up in every case, even there is no info.
		boolean doUpload = true;
		long lastUploaded = SystemClock.elapsedRealtime();
		
		while (run) {
			try {
				LocationInfo locationInfo = LocationInfo.get();
				if (!doUpload && (lastLocationInfo == null) && (locationInfo != null)) {
					// do upload in every case, if first position was received.
					doUpload = true;
				}
				long timeTrigger = prefs.getUplTimeTriggerInSeconds();
				long distanceTrigger = prefs.getUplDistanceTriggerInMeter();
				if (!doUpload && (timeTrigger == 0) && (distanceTrigger == 0)) {
					doUpload = true;
				}
				if (!doUpload && (timeTrigger > 0)) {
					if ((lastUploaded + (timeTrigger * 1000)) <=
						SystemClock.elapsedRealtime()) {
						doUpload = true;
					}
				}
				if (!doUpload && (distanceTrigger > 0)) {					
					if ((locationInfo != null) && (lastLocationInfo != null)) {
						if (distanceTrigger <=
							locationInfo.getLocation().distanceTo(lastLocationInfo.getLocation())) {
							doUpload = true;
						}
					}
				}
				if (doUpload || this.sendImmediately) {
					sendImmediately = false;
					this.upload(this.uploader);
				}
			} catch (Exception e) {
				e.printStackTrace();
				run = true;
			} finally {
				if (doUpload) {
					lastUploaded = SystemClock.elapsedRealtime();
					doUpload = false;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {					
					this.upload(this.uploader);
					run = false;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.uploader.toString() + "," +
		(this.onlyOneUpload ? "only once" : "infinite") + "]";		
	}
}
