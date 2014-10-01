package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils.UploadFileResult;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor.Result;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.util.FormatUtils;
import de.msk.mylivetracker.client.android.util.FormatUtils.Unit;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: ResponseCreator
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ResponseCreator {
	public static final String UNKNOWN = "<./.>";
	public static final String EMPTY = "<empty>";
	public static final String TIMESTAMP = "timestamp";
	public static final String LAT_LON = "latLon";
	public static final String ACCURACY = "accuracy";
	public static final String BEARING = "bearing";
	public static final String SPEED = "speed";
	public static final String ALTITUDE = "altitude";
	public static final String GOOGLE_LATLON_URL = "googlemaps";
	public static final String DATE_TIME_FORMAT = "'UTC' yyyy-MM-dd HH:mm:ss.SSS";
	
	public static String getResultNotSupported() {
		return "currently not supported";
	}

	public static String getResultOfNotConnectedToDropbox() {
		return "device is not connected to dropbox";
	}
	
	public static Result getResultOfTrackInfo() {
		boolean success = true;
		String str = "";
		TrackStatus status = TrackStatus.get();
		if (status.getRuntimeInMSecs(true).longValue() == 0L) {
			success = false;
			str = "no track information recorded";
		} else {
			LocationInfo locationInfo = LocationInfo.get();
			str = addParamValue(str, "name", PrefsRegistry.get(AccountPrefs.class).getTrackName());
			str = addParamValue(str, "status", status.trackIsRunning() ? "running" : "not running");
			str = addParamValue(str, "runtime (overall)", status.getRuntimeAsPrettyStr(true));
			str = addParamValue(str, "runtime (netto)", status.getRuntimeAsPrettyStr(false));
			str = addFloatValue(str, "distance", 
				(status.getTrackDistanceInMtr() != null) ? status.getTrackDistanceInMtr()/1000f : 0f, 
				2, Unit.Kilometer);
			str = addIntValue(str, "uploaded", 
				(UploadInfo.get() != null) ? UploadInfo.get().getCountUploaded() : 0, null);
			str = addTimestampValue(str, "last location update", 
				(locationInfo != null) ? locationInfo.getTimestamp() : null);
		}
		return new Result(success, str);
	}
	
	public static Result getResultOfUploadFile(UploadFileResult uploadFileResult) {
		boolean success = true;
		String response = "";
		if (!uploadFileResult.success) {
			success = false;
			response = uploadFileResult.error;
		} else {
			response = addParamValue(response, "revid", uploadFileResult.revisionId);
			response = addParamValue(response, "size", uploadFileResult.sizeStr);
		}
		return new Result(success, response);
	}
	
	public static String getResultOfGetHeartrate(HeartrateInfo heartrateInfo) {
		String str = "";
		if (heartrateInfo != null) {
			str = addLongValue(str, "current hr", heartrateInfo.getHrInBpm(), Unit.BeatsPerMinute);
			str = addLongValue(str, "average hr", heartrateInfo.getHrAvgInBpm(), Unit.BeatsPerMinute);
			str = addLongValue(str, "min hr", heartrateInfo.getHrMinInBpm(), Unit.BeatsPerMinute);
			str = addLongValue(str, "max hr", heartrateInfo.getHrMaxInBpm(), Unit.BeatsPerMinute);
		} else {
			str = "no heartrate info available";
		}
		return str;
	}
	
	public static Result getResultOfGetLocation(LocationInfo locationInfo, boolean accurate) {
		boolean success = false;
		String response = "no valid location found";
		if ((locationInfo != null) && locationInfo.hasValidLatLon() && 
			(!accurate || locationInfo.isAccurate())) {
			success = true;
			response = addLocationInfoValues(null, locationInfo);
		}
		return new Result(success, response);
	}
	
	public static String addLocationInfoValues(String str, LocationInfo locationInfo) {
		str = ResponseCreator.addTimestampValue(str, locationInfo.getTimestamp());
		str = ResponseCreator.addLatLonValue(str, locationInfo);
		str = ResponseCreator.addFloatValue(str, ACCURACY, locationInfo.getAccuracyInMtr(), 0, Unit.Meter);
		str = ResponseCreator.addFloatValue(str, BEARING, locationInfo.getBearingInDegree(), 0, Unit.DegreeAsTxt);
		str = ResponseCreator.addFloatValue(str, SPEED, locationInfo.getSpeedInMtrPerSecs(), 0, Unit.MeterPerSec);
		str = ResponseCreator.addDoubleValue(str, ALTITUDE, locationInfo.getAltitudeInMtr(), 0, Unit.Meter);
		str = ResponseCreator.addGoogleLatLonUrl(str, locationInfo);
		return str;
	}
	
	public static String addLatLonValue(String str, LocationInfo locationInfo) {
		String value = UNKNOWN;
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			value = 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 6) + "/" +
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLongitude(), 6);
		}
		return addParamValue(str, LAT_LON, value);
	}

	private static final String GOOGLE_MAPS_URL_TEMPLATE = 
			"https://maps.google.de/maps?q=$LAT,$LON";
	public static String addGoogleLatLonUrl(String str, LocationInfo locationInfo) {
		String value = UNKNOWN;
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			value = GOOGLE_MAPS_URL_TEMPLATE;
			value = StringUtils.replace(value, "$LAT", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 6));
			value = StringUtils.replace(value, "$LON", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLongitude(), 6));
		}
		return addParamValue(str, GOOGLE_LATLON_URL, value);
	}
	
	/*
	 * common methods. 
	 */
	
	public static String fmtDateTime(Date javaDateTime) {
		if (javaDateTime == null) return UNKNOWN;
		DateTime dateTime = new DateTime(javaDateTime.getTime());
		return dateTime.getAsStr(
			TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
			DATE_TIME_FORMAT);
	}
	
	public static String getTimestampValue(Date javaDateTime) {
		return addTimestampValue(null, javaDateTime);	
	}
	
	public static String addTimestampValue(String str, Date javaDateTime) {
		return addTimestampValue(str, TIMESTAMP, javaDateTime);
	}
	
	public static String addTimestampValue(String str, String param, Date javaDateTime) {
		if (javaDateTime != null) {
			str = addParamValue(str, param, fmtDateTime(javaDateTime));
		} else {
			str = addParamValue(str, param, UNKNOWN);
		}
		return str;
	}
	
	public static String addFloatValue(String str, String param, Float value, int decimalPositions, Unit unit) {
		String floatAsStr = UNKNOWN;
		if (value != null) {
			floatAsStr = FormatUtils.getDoubleAsSimpleStr(value.doubleValue(), decimalPositions);
			floatAsStr += ((unit == null) ? "" : unit.getTxt());
		}
		return addParamValue(str, param, floatAsStr);
	}
	
	public static String addDoubleValue(String str, String param, Double value, int decimalPositions, Unit unit) {
		String doubleAsStr = UNKNOWN;
		if (value != null) {
			doubleAsStr = FormatUtils.getDoubleAsSimpleStr(value, decimalPositions);
			doubleAsStr += ((unit == null) ? "" : unit.getTxt());
		}
		return addParamValue(str, param, doubleAsStr);
	}
	
	public static String getParamValue(String param, String value) {
		return addParamValue(null, param, value);
	}
	
	public static String addIntValue(String str, String param, Integer value, Unit unit) {
		String longAsInt = UNKNOWN;
		if (value != null) {
			longAsInt = String.valueOf(value);
			longAsInt += ((unit == null) ? "" : unit.getTxt());
		}
		return addParamValue(str, param, longAsInt);
	}
	
	public static String addLongValue(String str, String param, Long value, Unit unit) {
		String longAsStr = UNKNOWN;
		if (value != null) {
			longAsStr = String.valueOf(value);
			longAsStr += ((unit == null) ? "" : unit.getTxt());
		}
		return addParamValue(str, param, longAsStr);
	}
	
	public static String addParamValue(String str, String param, String value) {
		if (StringUtils.isEmpty(param)) {
			throw new IllegalArgumentException("param must not be empty.");
		}
		String res = "";
		if (!StringUtils.isEmpty(str)) {
			res += str + ", ";
		}
		if (value == null) {
			value = UNKNOWN;
		} else if (StringUtils.isEmpty(value)) {
			value = EMPTY;
		}
		res += param + "=" + value;
		return res;
	}
}
