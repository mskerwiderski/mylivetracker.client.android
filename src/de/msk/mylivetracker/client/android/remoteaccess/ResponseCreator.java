package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.dropbox.DropboxUtils.UploadFileResult;
import de.msk.mylivetracker.client.android.status.LocationInfo;
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
	public static final String GOOGLE_LATLON_URL = "googleLatLonUrl";
	public static final String DATE_TIME_FORMAT = "'UTC' yyyy-MM-dd HH:mm:ss.SSS";
	
	public static String getResultOfError(String errorMsg) {
		if (StringUtils.isEmpty(errorMsg)) {
			errorMsg = "unknown";
		}
		return "failed:" + errorMsg;
	}
	
	public static String getResultNotSupported() {
		return getResultOfError("currently not supported");
	}

	public static String getResultOfNotConnectedToDropbox() {
		return getResultOfError("device is not connected to dropbox");
	}
	
	public static String getResultOfUploadFile(UploadFileResult uploadFileResult) {
		String result = "";
		if (!uploadFileResult.success) {
			result = addParamValue(result, "error", uploadFileResult.error);
		} else {
			result = addParamValue(result, "revid", uploadFileResult.revisionId);
			result = addParamValue(result, "size", uploadFileResult.sizeStr);
		}
		return result;
	}
	
	public static String getResultOfGetLocation(LocationInfo locationInfo) {
		return addLocationInfoValues(null, locationInfo);
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
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 6) + ", " +
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
		return addParamValue(str, TIMESTAMP, fmtDateTime(javaDateTime));
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
