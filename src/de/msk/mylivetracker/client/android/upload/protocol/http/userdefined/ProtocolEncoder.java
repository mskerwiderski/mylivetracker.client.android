package de.msk.mylivetracker.client.android.upload.protocol.http.userdefined;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.App.VersionDsc;
import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.emergency.EmergencyPrefs;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParams;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParams.ParamId;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParamsPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
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
import de.msk.mylivetracker.commons.util.datetime.DateTime;
import de.msk.mylivetracker.commons.util.password.PasswordEncoderForGpsGatePortal;

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
public class ProtocolEncoder implements IProtocol {

	private static final String TIMESTAMP_FMT = "ddMMyyHHmmssSSS";
	
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		HttpProtocolParamsPrefs prefs = PrefsRegistry.get(HttpProtocolParamsPrefs.class);
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		HttpProtocolParams params = prefs.getHttpProtocolParams();
		TrackStatus trackStatus = TrackStatus.get();
				
		String paramsStr = null;
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.AppVersionCode), 
			VersionDsc.getCode());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.AppVersionName), 
			VersionDsc.getName());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.DeviceId), 
			accountPrefs.getDeviceId());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.TrackId), 
			trackStatus.getTrackId());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.TrackName), 
			accountPrefs.getTrackName());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, 
			params.getParamDsc(ParamId.PhoneType), 
			accountPrefs.getPhoneNumber());
		
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
		
		if (!StringUtils.isEmpty(accountPrefs.getUsername())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.Username), 
				accountPrefs.getUsername());
		} 
		if (!StringUtils.isEmpty(accountPrefs.getPassword())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.PasswordPlainText), 
				accountPrefs.getPassword());
		}
		if (!StringUtils.isEmpty(accountPrefs.getPassword())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.PasswordMd5), 
				accountPrefs.getSeed());
		}
		if (!StringUtils.isEmpty(accountPrefs.getPassword())) {
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.PasswordGpsGate), 
				PasswordEncoderForGpsGatePortal.encode(
					accountPrefs.getPassword()));
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
			paramsStr = HttpProtocolUtils.addParam(paramsStr, 
				params.getParamDsc(ParamId.SosMessage), 
				EmergencyPrefs.getEmergencyMessageText());
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
