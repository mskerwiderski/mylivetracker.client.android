package de.msk.mylivetracker.client.android.upload.protocol.http.userdefined;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.HttpProtocolParams;
import de.msk.mylivetracker.client.android.preferences.HttpProtocolParams.ParamId;
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
import de.msk.mylivetracker.commons.util.datetime.DateTime;

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

	private static final String TIMESTAMP_FMT = "ddMMyyHHmmssSSS";
	
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		Preferences prefs = Preferences.get();
		HttpProtocolParams params = prefs.getHttpProtocolParams();
		TrackStatus trackStatus = TrackStatus.get();
				
		String paramsStr = null;
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.AppVersionCode), 
			VersionUtils.get().getCode());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.AppVersionName), 
			VersionUtils.get().getName());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.DeviceId), 
			prefs.getDeviceId());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.TrackId), 
			trackStatus.getTrackId());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.TrackName), 
			prefs.getTrackName());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.PhoneType), 
			prefs.getPhoneNumber());
		
		if (batteryStateInfo != null) {
			if (batteryStateInfo.getPercent() != null) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.BatteryPowerPercent),
					batteryStateInfo.getPercent());
			}
			if (batteryStateInfo.getVoltage() !=  null) {				
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.BatteryPowerVoltage),
					batteryStateInfo.getVoltage());
			}
		}
		
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Latitude), 
				locationInfo.getLatitude());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Longitude), 
				locationInfo.getLongitude());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Distance), 
				locationInfo.getTrackDistanceInMtr());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Mileage), 
				locationInfo.getMileageInMtr());
			if (locationInfo.hasValidAltitude()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.Altitude), 
					locationInfo.getAltitudeInMtr());
			}
			if (locationInfo.hasValidSpeed()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.Speed), 
					locationInfo.getSpeedInMtrPerSecs());
			}
			if (locationInfo.hasValidAccuracy()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.Accuracy), 
					locationInfo.getAccuracyInMtr());
			}
			if (locationInfo.hasValidBearing()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.Bearing), 
					locationInfo.getBearingInDegree());
			}
			paramsStr = HttpProtocolUtils.addParamTrueFalse(paramsStr, 
				params.getParamDsc(ParamId.ValidPosition), 
				locationInfo.isAccurate());
		} else {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Distance), 
				trackStatus.getTrackDistanceInMtr());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Mileage), 
				trackStatus.getMileageInMtr());
		}

		if (gpsStateInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.SatelliteCount), 
				gpsStateInfo.getCountSatellites());
		}
		
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.RuntimeNet), 
			trackStatus.getRuntimeInMSecs(false));					
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.RuntimeGross),
			trackStatus.getRuntimeInMSecs(true));
		
		if (!StringUtils.isEmpty(prefs.getUsername())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Username), 
				prefs.getUsername());
		} 
		if (!StringUtils.isEmpty(prefs.getPassword())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Password), 
				prefs.getSeed());
		}
		
		if (heartrateInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.HeartrateCurrent), 	
				heartrateInfo.getHrInBpm());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.HeartrateMin),
				heartrateInfo.getHrMinInBpm());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.HeartrateMax),
				heartrateInfo.getHrMaxInBpm());
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.HeartrateAvg),
				heartrateInfo.getHrAvgInBpm());
		}
		
		if (emergencySignalInfo != null) {
			paramsStr = HttpProtocolUtils.addParamOnOff(paramsStr, 
				params.getParamDsc(ParamId.SosActivated), 
				emergencySignalInfo.isActivated());	
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.SosId), 
				emergencySignalInfo.getId());
		}
		
		if (messageInfo != null) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Message), 
				messageInfo.getMessage());
		}
		
		if (phoneStateInfo != null) {
			if (phoneStateInfo.hasPhoneType()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.PhoneType), 
					phoneStateInfo.getPhoneType());
			}
			if (phoneStateInfo.hasMobileCountryCode()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.MobileCountryCode), 
					phoneStateInfo.getMobileCountryCode());
			}
			if (phoneStateInfo.hasMobileNetworkCode()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.MobileNetworkCode), 
					phoneStateInfo.getMobileNetworkCode());
			}
			if (phoneStateInfo.hasMobileNetworkName()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.MobileNetworkName), 
					phoneStateInfo.getMobileNetworkName());
			}
			if (phoneStateInfo.hasNetworkType()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.NetworkType), 
					phoneStateInfo.getNetworkType());
			}
			if (phoneStateInfo.hasCellId()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.CellId), 
					phoneStateInfo.getCellId());
			}
			if (phoneStateInfo.hasLocalAreaCode()) {
				paramsStr = HttpProtocolUtils.addParam(paramsStr, 
					params.getParamDsc(ParamId.LocalAreaCode), 
					phoneStateInfo.getLocalAreaCode());
			}
		}
		
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.Timestamp), 
			DateTime.getCurrentAsUtcStr(TIMESTAMP_FMT));

		return paramsStr;
	}	
}
