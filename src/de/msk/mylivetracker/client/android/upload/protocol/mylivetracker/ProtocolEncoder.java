package de.msk.mylivetracker.client.android.upload.protocol.mylivetracker;

import java.util.Date;

import de.msk.mylivetracker.client.android.App.VersionDsc;
import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.emergency.EmergencyPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.commons.protocol.EncDecoder;
import de.msk.mylivetracker.commons.protocol.KeyStore;
import de.msk.mylivetracker.commons.protocol.UploadDataPacket;

/**
 * classname: ProtocolEncoder
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ProtocolEncoder extends EncDecoder implements IProtocol {
		
	public ProtocolEncoder() {
		super(new KeyStore(Keys.keyDscArrClient), true);
	}	
		
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {

		UploadDataPacket uploadDataPacket =
			createUploadDataPacket(
				lastInfoTimestamp, 
				phoneStateInfo, 
				batteryStateInfo, 
				locationInfo, 
				gpsStateInfo, 
				heartrateInfo, 
				emergencySignalInfo, 
				messageInfo);
		
		return this.encode("Key1024", uploadDataPacket);
	}


	private static UploadDataPacket createUploadDataPacket(
		Date lastInfoTimestamp,	
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo, 
		LocationInfo locationInfo, GpsStateInfo gpsStateInfo,
		HeartrateInfo heartrateInfo, EmergencySignalInfo emergencySignalInfo,
		MessageInfo messageInfo) {
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		TrackStatus status = TrackStatus.get();
			
		UploadDataPacket data = new UploadDataPacket();
		data.setTimestamp(lastInfoTimestamp.getTime());
		data.setVersionCode(VersionDsc.getCode());
		data.setVersionName(VersionDsc.getName());
		data.setUsername(accountPrefs.getUsername());
		data.setSeed(accountPrefs.getSeed());
		data.setDeviceId(accountPrefs.getDeviceId());
		data.setTrackId(status.getTrackId());
		data.setTrackName(accountPrefs.getTrackName());
		data.setPhoneNumber(accountPrefs.getPhoneNumber());
		
		if (batteryStateInfo != null) {
			if (batteryStateInfo.getPercent() != null) {
				data.setBatteryPowerInPercent(batteryStateInfo.getPercent());
			}
			if (batteryStateInfo.getVoltage() !=  null) {
				data.setBatteryPowerInVoltage(batteryStateInfo.getVoltage());
			}
		}
		
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			data.setLatitudeInDecimal(locationInfo.getLatitude());
			data.setLongitudeInDecimal(locationInfo.getLongitude());						
			if (locationInfo.hasValidAltitude()) {
				data.setAltitudeInMtr(locationInfo.getAltitudeInMtr());
			}
			if (locationInfo.hasValidSpeed()) {
				data.setSpeedInMtrPerSecs(Double.valueOf(locationInfo.getSpeedInMtrPerSecs()));
			}
			if (locationInfo.hasValidAccuracy()) {
				data.setLocationAccuracyInMtr(Double.valueOf(locationInfo.getAccuracyInMtr()));				
			}
			// TODO bearing
			data.setLocationValid(locationInfo.isAccurate());
			data.setMileageInMtr(Double.valueOf(locationInfo.getMileageInMtr()));
			data.setTrackDistanceInMtr(Float.valueOf(locationInfo.getTrackDistanceInMtr()));
		} else {		
			data.setMileageInMtr(Double.valueOf(status.getMileageInMtr()));
			data.setTrackDistanceInMtr(Float.valueOf(status.getTrackDistanceInMtr()));
		}
		
		if (gpsStateInfo != null) {
			data.setCountSatellites(gpsStateInfo.getCountSatellites());
		}
		
		data.setRuntimeMSecsPausesIncl(status.getRuntimeInMSecs(true));
		data.setRuntimeMSecsPausesNotIncl(status.getRuntimeInMSecs(false));							
		if (heartrateInfo != null) {
			data.setHeartrateInBpm(heartrateInfo.getHrInBpm());
			data.setHeartrateMinInBpm(heartrateInfo.getHrMinInBpm());
			data.setHeartrateMaxInBpm(heartrateInfo.getHrMaxInBpm());
			data.setHeartrateAvgInBpm(heartrateInfo.getHrAvgInBpm());
		}
		if (emergencySignalInfo != null) {
			data.setSosActivated(emergencySignalInfo.activated);
			data.setSosId(emergencySignalInfo.getId());
			data.setMessage(EmergencyPrefs.getEmergencyMessageText());
		}
		if ((messageInfo != null) && (emergencySignalInfo != null)) {
			data.setMessage(messageInfo.getMessage());
		}
		if (phoneStateInfo != null) {
			if (phoneStateInfo.hasPhoneType()) {
				data.setPhoneType(phoneStateInfo.getPhoneType());
			}
			if (phoneStateInfo.hasMobileCountryCode()) {
				data.setMobileCountryCode(phoneStateInfo.getMobileCountryCode());
			}
			if (phoneStateInfo.hasMobileNetworkCode()) {
				data.setMobileNetworkCode(phoneStateInfo.getMobileNetworkCode());
			}
			if (phoneStateInfo.hasMobileNetworkName()) {
				data.setMobileNetworkName(phoneStateInfo.getMobileNetworkName());
			}
			if (phoneStateInfo.hasNetworkType()) {
				data.setMobileNetworkType(phoneStateInfo.getNetworkType());
			}
			if (phoneStateInfo.hasCellId()) {
				data.setCellId(Integer.valueOf(phoneStateInfo.getCellId()));
			}
			if (phoneStateInfo.hasLocalAreaCode()) {
				data.setLocaleAreaCode(Integer.valueOf(phoneStateInfo.getLocalAreaCode()));
			}
		}
		
		return data;
	}
}
