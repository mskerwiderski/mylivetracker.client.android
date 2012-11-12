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
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.upload.AbstractUploader.UploadResult;
import de.msk.mylivetracker.client.android.upload.protocol.Protocols;

/**
 * Uploader.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-11-12
 * 
 */
public class Uploader {
	public static void uploadOneTime() {
		UploadServiceThread thread = new UploadServiceThread(true);
		thread.start();
	}
	
	public static AbstractUploader createUploader() {
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
	
	public static class LastInfoDsc {
		public LocationInfo lastLocationInfo = null;
		public EmergencySignalInfo lastEmergencySignalInfo = null;
		public MessageInfo lastMessageInfo = null;
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
	
	public static void upload(AbstractUploader uploader, LastInfoDsc lastInfoDsc) {
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
			!emergencySignalInfo.isUpToDate(lastInfoDsc.lastEmergencySignalInfo)) {
			emergencySignalInfo = null;
		} else {
			lastInfoTimestamp = 
				updateLastInfoTimestamp(emergencySignalInfo, lastInfoTimestamp);
		}
		
		if ((messageInfo != null) && 
			!messageInfo.isUpToDate(lastInfoDsc.lastMessageInfo)) {
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
				lastInfoDsc.lastLocationInfo = locationInfo;
			}
			if (emergencySignalInfo != null) {
				lastInfoDsc.lastEmergencySignalInfo = emergencySignalInfo;
			}
			if (messageInfo != null) {
				lastInfoDsc.lastMessageInfo = messageInfo;
			}						
		}		
		
		long stop = SystemClock.elapsedRealtime();	
		
		UploadInfo.update(uploadResult.isProcessed(), 
			uploadResult.getResultCode(), 
			uploadResult.getCountPositions(), 
			stop - start,
			LocationInfo.getProviderAbbr(locationInfo));
		
		MainActivity.get().updateView();
	}
}