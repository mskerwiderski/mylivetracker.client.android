package de.msk.mylivetracker.client.android.httpprotocolparams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: HttpProtocolParams
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
		PasswordPlainText,
		PasswordMd5,
		PasswordGpsGate,
		HeartrateCurrent,
		HeartrateMin,
		HeartrateMax,
		HeartrateAvg,
		SosId,
		SosActivated,
		SosMessage,
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
	
	protected List<HttpProtocolParamDsc> getParams() {
		return params;
	}

	public static HttpProtocolParams create() {
		HttpProtocolParams httpProtocolParams = new HttpProtocolParams();
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameTimestamp,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleTimestamp, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameDeviceId,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleDeviceId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameAppVersionCode,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleAppVersionCode, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameAppVersionName,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleAppVersionName, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameTrackId,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleTrackId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameTrackName,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleTrackName, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNamePhoneNumber,
			R.string.txHttpProtocolParamsPrefs_ParamValueExamplePhoneNumber, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameBatteryPowerPercent,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleBatteryPowerPercent, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameBatteryPowerVoltage,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleBatteryPowerVoltage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameValidPosition,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleValidPosition, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameLatitude,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleLatitude, 
			true, false));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameLongitude,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleLongitude, 
			true, false));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameAltitude,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleAltitude, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameSpeed,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleSpeed, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameAccuracy,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleAccuracy, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameBearing,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleBearing, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameSatelliteCount,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleSatelliteCount, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameDistance,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleDistance, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameMileage,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleMileage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameRuntimeNet,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleRuntimeNet, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameRuntimeGross,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleRuntimeGross, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameUsername,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleUsername, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNamePasswordPlainText,
			R.string.txHttpProtocolParamsPrefs_ParamValueExamplePasswordPlainText, 
			false));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNamePasswordMd5,
			R.string.txHttpProtocolParamsPrefs_ParamValueExamplePasswordMd5, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNamePasswordGpsGate,
			R.string.txHttpProtocolParamsPrefs_ParamValueExamplePasswordGpsGate, 
			false));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameHeartrateCurrent,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleHeartrateCurrent, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameHeartrateMin,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleHeartrateMin, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameHeartrateMax,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleHeartrateMax, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameHeartrateAvg,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleHeartrateAvg, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameSosId,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleSosId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameSosActivated,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleSosActivated, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameSosMessage,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleSosMessage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameMessage,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleMessage, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNamePhoneType,
			R.string.txHttpProtocolParamsPrefs_ParamValueExamplePhoneType, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameNetworkType,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleNetworkType, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameMobileCountryCode,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleMobileCountryCode, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameMobileNetworkCode,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleMobileNetworkCode, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameMobileNetworkName,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleMobileNetworkName, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameCellId,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleCellId, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txHttpProtocolParamsPrefs_ParamNameLocalAreaCode,
			R.string.txHttpProtocolParamsPrefs_ParamValueExampleLocalAreaCode, 
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
	public boolean paramNameExistsOutsidePosition(String paramName, int position) {
		if (StringUtils.isEmpty(paramName)) {
			throw new IllegalArgumentException("paramName must not be empty.");
		}
		if ((position < 0) || (position >= this.params.size())) {
			throw new IllegalArgumentException("position out of valid range.");
		}
		boolean exists = false;
		for (int i=0; !exists && (i < this.params.size()); i++) {
			exists = 
				StringUtils.equals(paramName, this.params.get(i).getName()) &&
				(i != position);
		}
		return exists;
	}
}
