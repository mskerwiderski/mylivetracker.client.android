package de.msk.mylivetracker.client.android.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * HttpProtocolParams.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-12-01 initial. 
 * 
 */
public class HttpProtocolParams implements Serializable {

	private static final long serialVersionUID = -5362511109345782859L;

	public enum ParamId {
		Timestamp,
		DeviceId,
		AppVersionCode,
		AppVersionName,
		TrackId,
		TrackName,
		PhoneNumber,
		BatteryPowerPercent,
		BatteryPowerVoltage,
		ValidPosition,
		Latitude,
		Longitude,
		Altitude,
		Speed,
		Accuracy,
		Bearing,
		SatelliteCount,
		Distance,
		Mileage,
		RuntimeNet,
		RuntimeGross,
		Username,
		Password,
		HeartrateCurrent,
		HeartrateMin,
		HeartrateMax,
		HeartrateAvg,
		SosId,
		SosActivated,
		Message,
		PhoneType,
		NetworkType,
		MobileCountryCode,
		MobileNetworkCode,
		MobileNetworkName,
		CellId,
		LocalAreaCode,
	}
	
	public List<HttpProtocolParamDsc> params = new ArrayList<HttpProtocolParamDsc>();
	
	public static HttpProtocolParams create() {
		HttpProtocolParams httpProtocolParams = new HttpProtocolParams();
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameTimestamp,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleTimestamp, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameDeviceId,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleDeviceId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameAppVersionCode,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleAppVersionCode, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameAppVersionName,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleAppVersionName, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameTrackId,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleTrackId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameTrackName,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleTrackName, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNamePhoneNumber,
			R.string.txPrefsHttpProtocolParams_ParamValueExamplePhoneNumber, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameBatteryPowerPercent,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleBatteryPowerPercent, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameBatteryPowerVoltage,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleBatteryPowerVoltage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameValidPosition,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleValidPosition, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameLatitude,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleLatitude, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameLongitude,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleLongitude, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameAltitude,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleAltitude, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameSpeed,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleSpeed, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameAccuracy,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleAccuracy, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameBearing,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleBearing, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameSatelliteCount,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleSatelliteCount, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameDistance,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleDistance, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameMileage,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleMileage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameRuntimeNet,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleRuntimeNet, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameRuntimeGross,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleRuntimeGross, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameUsername,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleUsername, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNamePassword,
			R.string.txPrefsHttpProtocolParams_ParamValueExamplePassword, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameHeartrateCurrent,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleHeartrateCurrent, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameHeartrateMin,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleHeartrateMin, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameHeartrateMax,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleHeartrateMax, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameHeartrateAvg,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleHeartrateAvg, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameSosId,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleSosId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameSosActivated,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleSosActivated, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameMessage,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleMessage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNamePhoneType,
			R.string.txPrefsHttpProtocolParams_ParamValueExamplePhoneType, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameNetworkType,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleNetworkType, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameMobileCountryCode,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleMobileCountryCode, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameMobileNetworkCode,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleMobileNetworkCode, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameMobileNetworkName,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleMobileNetworkName, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameCellId,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleCellId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameLocalAreaCode,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleLocalAreaCode, 
			true));
		return httpProtocolParams;
	}
	
	public HttpProtocolParams copy() {
		LogUtils.infoMethodIn(this.getClass(), "copy");
		HttpProtocolParams httpProtocolParams = new HttpProtocolParams();
		for (HttpProtocolParamDsc dsc : this.params) {
			httpProtocolParams.params.add(dsc.copy());
			LogUtils.infoMethodState(this.getClass(), "copy", "param", dsc);
		}
		LogUtils.infoMethodOut(this.getClass(), "copy", httpProtocolParams.params.size());
		return httpProtocolParams;
	}
	
	private HttpProtocolParams() {
	}

	public HttpProtocolParamDsc getParamDsc(ParamId id) {
		return params.get(id.ordinal());
	}
	public String getParamName(ParamId id) {
		return params.get(id.ordinal()).getName();
	}
	public HttpProtocolParamDsc getParamDsc(int id) {
		return params.get(id);
	}
	public boolean paramNameExists(String paramName) {
		boolean exists = false;
		for (int i=0; !exists && (i < this.params.size()); i++) {
			exists = StringUtils.equals(paramName, this.params.get(i).getName());
		}
		return exists;
	}
}
