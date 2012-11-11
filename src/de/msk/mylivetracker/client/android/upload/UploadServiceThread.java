package de.msk.mylivetracker.client.android.upload;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import android.location.LocationManager;
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
public class UploadServiceThread extends Thread {

	private static UploadServiceThread uploadManagerForTracking = null;
	@SuppressWarnings("unused")
	private static UploadServiceThread uploadManagerOnlyOneTime = null;
	
	private static AbstractUploader createUploader() {
		AbstractUploader uploader = null;
		Preferences prefs = Preferences.get();		
		if (prefs.getTransferProtocol().equals(TransferProtocol.uploadDisabled)) {
			uploader = new DummyUploader(Protocols.createProtocolDummy());
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.mltHttpPlain)) {
			uploader = new HttpUploader(Protocols.createProtocolMltUrlparams());
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.mltTcpEncrypted)) {
			uploader = new TcpUploader(Protocols.createProtocolMltDatastrEncrypted(), true);
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.mltRpcEncrypted)) {
			uploader = new RpcUploader(Protocols.createProtocolMltDatastrEncrypted());
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.tk102Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolXexunTk102(), false);
		} else if (prefs.getTransferProtocol().equals(TransferProtocol.tk5000Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolIncutexTk5000(), false);
		} else {
			throw new RuntimeException();
		}
		return uploader;
	}
	
	protected static void startUploadManager() {
		TrackStatus.get().markAsStarted();
		runUploadThread(false);
	}
	
	protected static void stopUploadManager() {
		if (uploadManagerForTracking != null) {
			uploadManagerForTracking.interrupt();
			uploadManagerForTracking = null;
		}
		TrackStatus.get().markAsStopped();
	}
	
	public static void uploadOnlyOneTime() {
		if ((uploadManagerForTracking != null) && 
			uploadManagerForTracking.isAlive()) {			
			uploadManagerForTracking.setSendImmediately();
		} else {
			runUploadThread(true);
		}
	}
	
	private static void runUploadThread(boolean onlyOneUpload) {
		AbstractUploader uploader = createUploader();
		if (uploader != null) {
			UploadServiceThread uploadManager = new UploadServiceThread(uploader, onlyOneUpload);
			uploadManager.start();
			if (onlyOneUpload) {
				uploadManagerOnlyOneTime = uploadManager;
			} else {
				uploadManagerForTracking = uploadManager;
			}				
		} else {
			// data transfer disabled.
		}
	}
	
	private AbstractUploader uploader = null;
	private boolean onlyOneUpload = false;
	
	private static LocationInfo lastLocationInfo = null;
	private static EmergencySignalInfo lastEmergencySignalInfo = null;
	private static MessageInfo lastMessageInfo = null;
	
	private UploadServiceThread(AbstractUploader uploader, boolean onlyOneUpload) {
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
		
		String lastUsedLocationProvider = "?";
		if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
			if (StringUtils.equals(locationInfo.getLocation().getProvider(), LocationManager.GPS_PROVIDER)) {
				lastUsedLocationProvider = "gps";
			} else if (StringUtils.equals(locationInfo.getLocation().getProvider(), LocationManager.NETWORK_PROVIDER)) {
				lastUsedLocationProvider = "nw";
			} 
		}
		
		UploadInfo.update(uploadResult.isProcessed(), 
			uploadResult.getResultCode(), 
			uploadResult.getCountPositions(), 
			stop - start,
			lastUsedLocationProvider);
		
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
		// do upload at start up in every case, even there is no info.
		boolean doUpload = true;
		long lastUploaded = SystemClock.elapsedRealtime();
		boolean run = true;
		while (run) {
			try {
				LocationInfo locationInfo = LocationInfo.get();
				if (!doUpload && (lastLocationInfo == null) && (locationInfo != null)) {
					// do upload in every case, if first position was received.
					doUpload = true;
				}
				int timeTrigger = prefs.getUplTimeTrigger().getSecs();
				int distanceTrigger = prefs.getUplDistanceTrigger().getMtrs();
				if (!doUpload && (timeTrigger == 0) && (distanceTrigger == 0)) {
					doUpload = true;
				}
				if (!doUpload) {
					boolean timeConditionFulfilled = false;
					if (timeTrigger > 0) {
						if ((lastUploaded + (timeTrigger * 1000)) <=
							SystemClock.elapsedRealtime()) {
							timeConditionFulfilled = true;
						}
					}
					boolean distanceConditionFulfilled = false;
					if (distanceTrigger > 0) {					
						if ((locationInfo != null) && (lastLocationInfo != null)) {
							if (distanceTrigger <=
								locationInfo.getLocation().distanceTo(lastLocationInfo.getLocation())) {
								distanceConditionFulfilled = true;
							}
						}
					}
					boolean triggerLogicIsAND = prefs.getUplTriggerLogic().AND();
					if ((timeTrigger == 0) || (distanceTrigger == 0)) {
						triggerLogicIsAND = false;
					}
					if (triggerLogicIsAND) {
						doUpload = timeConditionFulfilled && distanceConditionFulfilled;
					} else {
						doUpload = timeConditionFulfilled || distanceConditionFulfilled;
					}
				}
				if (doUpload || this.sendImmediately) {
					sendImmediately = false;
					this.upload(this.uploader);
				}
			} catch (Exception e) {
				// noop.
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
		if (!run) {
			this.upload(this.uploader);
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
