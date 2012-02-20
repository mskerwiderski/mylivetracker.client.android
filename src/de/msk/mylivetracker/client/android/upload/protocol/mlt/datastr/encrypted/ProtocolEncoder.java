package de.msk.mylivetracker.client.android.upload.protocol.mlt.datastr.encrypted;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import android.location.Location;
import android.telephony.ServiceState;
import android.telephony.gsm.GsmCellLocation;
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
		data.setVersionCode(MainActivity.getVersion().getCode());
		data.setVersionName(MainActivity.getVersion().getName());
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
			data.setPhoneType(PhoneStateInfo.getPhoneTypeAsStr());
			ServiceState serviceState = phoneStateInfo.getServiceState();
			if (serviceState != null) {
				String nwOpCode = serviceState.getOperatorNumeric();				
				if (!StringUtils.isEmpty(nwOpCode) && 
					((StringUtils.length(nwOpCode) == 5) || (StringUtils.length(nwOpCode) == 6))) {
					data.setMobileCountryCode(StringUtils.left(nwOpCode, 3));
					data.setMobileNetworkCode(StringUtils.substring(nwOpCode, 3));
				}
				String mnn = serviceState.getOperatorAlphaLong();
				if (!StringUtils.isEmpty(mnn)) {
					data.setMobileNetworkName(mnn);
				}				
			}
			data.setMobileNetworkType(phoneStateInfo.getNetworkTypeAsStr("unknown"));
				
			GsmCellLocation gsmCellLocation = phoneStateInfo.getGsmCellLocation();
			if (gsmCellLocation != null) {				
				if (gsmCellLocation.getCid() != -1) {
					data.setCellId(gsmCellLocation.getCid());
				}
				if (gsmCellLocation.getLac() != -1) {
					data.setLocaleAreaCode(gsmCellLocation.getLac());
				}
			}
		}
		
		return data;
	}
}
