package de.msk.mylivetracker.client.android.upload.protocol.mlt.datastr.encrypted;

import java.util.Date;

import android.location.Location;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.NmeaInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.client.android.util.VersionUtils;
import de.msk.mylivetracker.commons.protocol.EncDecoder;
import de.msk.mylivetracker.commons.protocol.KeyStore;
import de.msk.mylivetracker.commons.protocol.UploadDataPacket;

/**
 * ProtocolEncoder.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-20 default value for getNetworkTypeAsStr is 'unknown'.
 * 000 	2011-08-11 initial.
 * 
 */
public class ProtocolEncoder extends EncDecoder implements IProtocol {
		
	public ProtocolEncoder() {
		super(new KeyStore(Keys.keyDscArrClient), true);
	}	
		
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, NmeaInfo nmeaInfo,
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
		Preferences prefs = Preferences.get();
		TrackStatus status = TrackStatus.get();
			
		UploadDataPacket data = new UploadDataPacket();
		data.setTimestamp(lastInfoTimestamp.getTime());
		data.setVersionCode(VersionUtils.get(MainActivity.get()).getCode());
		data.setVersionName(VersionUtils.get(MainActivity.get()).getName());
		data.setUsername(prefs.getUsername());
		data.setSeed(prefs.getSeed());
		data.setDeviceId(prefs.getDeviceId());
		data.setTrackId(status.getTrackId());
		data.setTrackName(prefs.getTrackName());
		data.setPhoneNumber(prefs.getPhoneNumber());
		
		if (batteryStateInfo != null) {
			if (batteryStateInfo.getPercent() != null) {
				data.setBatteryPowerInPercent(batteryStateInfo.getPercent());
			}
			if (batteryStateInfo.getVoltage() !=  null) {
				data.setBatteryPowerInVoltage(batteryStateInfo.getVoltage());
			}
		}
		
		if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
			Location location = locationInfo.getLocation();
			data.setLatitudeInDecimal(location.getLatitude());
			data.setLongitudeInDecimal(location.getLongitude());						
			if (location.hasAltitude()) {
				data.setAltitudeInMtr(location.getAltitude());
			}
			if (location.hasSpeed()) {
				data.setSpeedInMtrPerSecs(Double.valueOf(location.getSpeed()));
			}
			if (location.hasAccuracy()) {
				data.setLocationAccuracyInMtr(Double.valueOf(location.getAccuracy()));				
			}
			data.setLocationValid(locationInfo.isAccurate());
			data.setMileageInMtr(new Double(locationInfo.getMileageInMtr()));
			data.setTrackDistanceInMtr(new Float(locationInfo.getTrackDistanceInMtr()));
		} else {		
			data.setMileageInMtr(new Double(status.getMileageInMtr()));
			data.setTrackDistanceInMtr(new Float(status.getTrackDistanceInMtr()));
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
		}
		if (messageInfo != null) {
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
