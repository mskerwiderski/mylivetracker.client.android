package de.msk.mylivetracker.client.android.upload.protocol.mlt.urlparams;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.protocol.HttpProtocolUtils;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.client.android.util.VersionUtils;

/**
 * ProtocolEncoder.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-20 default value for getNetworkTypeAsStr is 'unknown'.
 * 000	2011-08-11 initial.
 * 
 */
public class ProtocolEncoder implements IProtocol {

	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		Preferences preferences = Preferences.get();
		TrackStatus trackStatus = TrackStatus.get();
				
		String paramsStr = null;
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "vco", String.valueOf(VersionUtils.get().getCode()));
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "vna", VersionUtils.get().getName());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "did", preferences.getDeviceId());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "tid", trackStatus.getTrackId());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "trn", preferences.getTrackName());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "phn", preferences.getPhoneNumber());
		
		if (batteryStateInfo != null) {
			if (batteryStateInfo.getPercent() != null) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "pow", 
					String.valueOf(batteryStateInfo.getPercent()));
			}
			if (batteryStateInfo.getVoltage() !=  null) {				
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "vol", 
					String.valueOf(batteryStateInfo.getVoltage()));
			}
		}
		
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "lat", String.valueOf(locationInfo.getLatitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "lon", String.valueOf(locationInfo.getLongitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "dst", String.valueOf(locationInfo.getTrackDistanceInMtr()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "mil", String.valueOf(locationInfo.getMileageInMtr()));
			if (locationInfo.getAltitude() != null) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "alt", String.valueOf(locationInfo.getAltitude()));
			}
			if (locationInfo.getSpeed() != null) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "spd", String.valueOf(locationInfo.getSpeed()));
			}
			if (locationInfo.getAccuracy() != null) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "acc", String.valueOf(locationInfo.getAccuracy()));
			}
			if (locationInfo.getBearing() != null) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "bea", String.valueOf(locationInfo.getBearing()));
			}
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "val", BooleanUtils.toString(locationInfo.isAccurate(), "1", "0"));
		} else {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "dst", String.valueOf(trackStatus.getTrackDistanceInMtr()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "mil", String.valueOf(trackStatus.getMileageInMtr()));
		}

		if (gpsStateInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "sat", String.valueOf(gpsStateInfo.getCountSatellites()));
		}
		
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "rtm", String.valueOf(trackStatus.getRuntimeInMSecs(false)));					
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "rtp", String.valueOf(trackStatus.getRuntimeInMSecs(true)));
		
		if (!StringUtils.isEmpty(preferences.getUsername())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "usr", preferences.getUsername());
		} 
		if (!StringUtils.isEmpty(preferences.getPassword())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "pwd", preferences.getSeed());
		}
		
		if (heartrateInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "hrt", 	
				String.valueOf(heartrateInfo.getHrInBpm()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "hrn",
				String.valueOf(heartrateInfo.getHrMinInBpm()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "hrm",
				String.valueOf(heartrateInfo.getHrMaxInBpm()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "hra",
				String.valueOf(heartrateInfo.getHrAvgInBpm()));
		}
		
		if (emergencySignalInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "sos", (emergencySignalInfo.isActivated() ? "on" : "off"));	
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "sosid", String.valueOf(emergencySignalInfo.getId()));
		}
		
		if (messageInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "msg", messageInfo.getMessage());
		}
		
		if (phoneStateInfo != null) {
			if (phoneStateInfo.hasPhoneType()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "pht", phoneStateInfo.getPhoneType());
			}
			if (phoneStateInfo.hasMobileCountryCode()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "mcc", phoneStateInfo.getMobileCountryCode());
			}
			if (phoneStateInfo.hasMobileNetworkCode()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "mnc", phoneStateInfo.getMobileNetworkCode());
			}
			if (phoneStateInfo.hasMobileNetworkName()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "mnn", phoneStateInfo.getMobileNetworkName());
			}
			if (phoneStateInfo.hasNetworkType()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "nwt", phoneStateInfo.getNetworkType());
			}
			if (phoneStateInfo.hasCellId()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "cid", phoneStateInfo.getCellId());
			}
			if (phoneStateInfo.hasLocalAreaCode()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "lac", phoneStateInfo.getLocalAreaCode());
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmmssSSS");
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "tst", sdf.format(lastInfoTimestamp));

		return paramsStr;
	}	
}
