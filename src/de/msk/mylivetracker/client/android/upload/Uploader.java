package de.msk.mylivetracker.client.android.upload;

import java.util.Date;

import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.TransferProtocol;
import de.msk.mylivetracker.client.android.status.AbstractInfo;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.upload.AbstractUploader.UploadResult;
import de.msk.mylivetracker.client.android.upload.protocol.Protocols;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: Uploader
 * 
 * @author michael skerwiderski, (c)2012
 * @version 001
 * @since 1.5.0
 * 
 * history:
 * 001	2014-01-03  createUploader extended for gatorPt350.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class Uploader {
	public static void uploadOneTime() {
		AbstractService.runServiceOnlyOnce(UploadService.class);
	}
	
	public static AbstractUploader createUploader() {
		AbstractUploader uploader = null;
		TransferProtocol transferProtocol = 
			PrefsRegistry.get(ProtocolPrefs.class).getTransferProtocol();
		if (transferProtocol.equals(TransferProtocol.uploadDisabled)) {
			uploader = new DummyUploader(Protocols.createProtocolDummy());
		} else if (transferProtocol.equals(TransferProtocol.httpUserDefined)) {
			uploader = new HttpUploader(Protocols.createProtocolMltUrlparams());
		} else if (transferProtocol.equals(TransferProtocol.sms)) {
			uploader = new SmsUploader(Protocols.createProtocolMltSms());
		} else if (transferProtocol.equals(TransferProtocol.mltTcpEncrypted)) {
			uploader = new TcpUploader(Protocols.createProtocolMltDatastrEncrypted(), true);
		} else if (transferProtocol.equals(TransferProtocol.tk102Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolXexunTk102(), false);
		} else if (transferProtocol.equals(TransferProtocol.tk5000Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolIncutexTk5000(), false);
		} else if (transferProtocol.equals(TransferProtocol.pt350Emulator)) {
			uploader = new TcpUploader(Protocols.createProtocolGatorPt350(), false);
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
	
	public static void upload(AbstractUploader uploader, LastInfoDsc lastInfoDsc)
		throws InterruptedException {
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
		
		Date lastInfoTimestamp = null;
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		BatteryStateInfo batteryStateInfo = BatteryStateInfo.get();
		LocationInfo locationInfo = LocationInfo.get();
		GpsStateInfo gpsStateInfo = GpsStateInfo.get();
		HeartrateInfo heartrateInfo = HeartrateInfo.get();
		EmergencySignalInfo emergencySignalInfo = EmergencySignalInfo.get();
		MessageInfo messageInfo = MessageInfo.get();
		
		LogUtils.infoMethodState(Uploader.class, "upload", "emergencySignalInfo", emergencySignalInfo);
		
		lastInfoTimestamp = 
			updateLastInfoTimestamp(phoneStateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(batteryStateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(locationInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(gpsStateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(heartrateInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(emergencySignalInfo, lastInfoTimestamp);
		lastInfoTimestamp = 
			updateLastInfoTimestamp(messageInfo, lastInfoTimestamp);
		
		if (protocolPrefs.isUploadPositionsOnlyOnce()) {
			if ((locationInfo != null) && 
				!locationInfo.isUpToDate(lastInfoDsc.lastLocationInfo)) {
				locationInfo = null;
			} 
		}
		
		LogUtils.infoMethodState(Uploader.class, "upload", "last emergencySignalInfo was", lastInfoDsc.lastEmergencySignalInfo);
		if ((emergencySignalInfo != null) && 
			!emergencySignalInfo.isUpToDate(lastInfoDsc.lastEmergencySignalInfo)) {
			LogUtils.infoMethodState(Uploader.class, "upload", "emergencySignalInfo is NOT up to date", emergencySignalInfo);
			emergencySignalInfo = null;
		} else {
			LogUtils.infoMethodState(Uploader.class, "upload", "emergencySignalInfo is up to date", emergencySignalInfo);
		}
		
		if ((messageInfo != null) && 
			!messageInfo.isUpToDate(lastInfoDsc.lastMessageInfo)) {
			messageInfo = null;
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
				gpsStateInfo,
				heartrateInfo, 
				emergencySignalInfo,
				messageInfo);
		
		if (uploadResult.isUploaded() || uploadResult.isBuffered()) {
			LogUtils.infoMethodState(Uploader.class, "upload", "upload or buffering was successful");
			if (locationInfo != null) {
				lastInfoDsc.lastLocationInfo = locationInfo;
			}
			if (emergencySignalInfo != null) {
				lastInfoDsc.lastEmergencySignalInfo = emergencySignalInfo;
				LogUtils.infoMethodState(Uploader.class, "upload", "lastEmergencySignalInfo updated", lastInfoDsc.lastEmergencySignalInfo);
			}
			if (messageInfo != null) {
				lastInfoDsc.lastMessageInfo = messageInfo;
			}						
		}		
		
		UploadInfo.update(uploadResult.isProcessed(), 
			uploadResult.getResultCode(), 
			uploadResult.getCountPositions(), 
			uploadResult.getUploadTimeInMSecs(),
			LocationInfo.getProviderAbbr(locationInfo));
	}
}
