package de.msk.mylivetracker.client.android.upload.protocol.mlt.urlparams;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import android.location.Location;
import android.telephony.ServiceState;
import android.telephony.gsm.GsmCellLocation;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
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

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.upload.protocol.IProtocol#createDataStrForDataTransfer(java.util.Date, de.msk.mylivetracker.client.android.status.PhoneStateInfo, de.msk.mylivetracker.client.android.status.BatteryStateInfo, de.msk.mylivetracker.client.android.status.LocationInfo, de.msk.mylivetracker.client.android.status.NmeaInfo, de.msk.mylivetracker.client.android.status.GpsStateInfo, de.msk.mylivetracker.client.android.status.HeartrateInfo, de.msk.mylivetracker.client.android.status.EmergencySignalInfo, de.msk.mylivetracker.client.android.status.MessageInfo, java.lang.String, java.lang.String)
	 */
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, NmeaInfo nmeaInfo,
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		Preferences preferences = Preferences.get();
		TrackStatus trackStatus = TrackStatus.get();
				
		String paramsStr = null;
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "vco", String.valueOf(VersionUtils.get(MainActivity.get()).getCode()));
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "vna", VersionUtils.get(MainActivity.get()).getName());
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
		
		if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
			Location location = locationInfo.getLocation();
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "lat", String.valueOf(location.getLatitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "lon", String.valueOf(location.getLongitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "dst", String.valueOf(locationInfo.getTrackDistanceInMtr()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "mil", String.valueOf(locationInfo.getMileageInMtr()));
			if (location.hasAltitude()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "alt", String.valueOf(location.getAltitude()));
			}
			if (location.hasSpeed()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "spd", String.valueOf(location.getSpeed()));
			}
			if (location.hasAccuracy()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, "acc", String.valueOf(location.getAccuracy()));
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
		
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "pht", PhoneStateInfo.getPhoneTypeAsStr());
		if (phoneStateInfo != null) {
			ServiceState serviceState = phoneStateInfo.getServiceState();
			if (serviceState != null) {
				String nwOpCode = serviceState.getOperatorNumeric();				
				if (!StringUtils.isEmpty(nwOpCode) && 
					((StringUtils.length(nwOpCode) == 5) || (StringUtils.length(nwOpCode) == 6))) {
					paramsStr = HttpProtocolUtils.addParam(paramsStr, "mcc", StringUtils.left(nwOpCode, 3));
					paramsStr = HttpProtocolUtils.addParam(paramsStr, "mnc", StringUtils.substring(nwOpCode, 3));
				}
				String mnn = serviceState.getOperatorAlphaLong();
				if (!StringUtils.isEmpty(mnn)) {
					paramsStr = HttpProtocolUtils.addParam(paramsStr, "mnn", mnn);
				}
			}
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "nwt", phoneStateInfo.getNetworkTypeAsStr(UpdaterUtils.getNoValue()));
			
			GsmCellLocation gsmCellLocation = phoneStateInfo.getGsmCellLocation();
			if (gsmCellLocation != null) {				
				if (gsmCellLocation.getCid() != -1) {
					paramsStr = HttpProtocolUtils.addParam(paramsStr, "cid", String.valueOf(gsmCellLocation.getCid()));
				}
				if (gsmCellLocation.getLac() != -1) {
					paramsStr = HttpProtocolUtils.addParam(paramsStr, "lac", String.valueOf(gsmCellLocation.getLac()));
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmmssSSS");
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "tst", sdf.format(lastInfoTimestamp));

		return paramsStr;
	}	
}
