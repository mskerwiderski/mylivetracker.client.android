package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.util.FormatUtils;
import de.msk.mylivetracker.client.android.util.FormatUtils.Unit;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * ResponseCreator.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-11-27 	initial.
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
	
	
	/*
	 * location infos.
	 */
	
	public static String getLocationInfoValues(LocationInfo locationInfo) {
		return addLocationInfoValues(null, locationInfo);
	}
	
	public static String addLocationInfoValues(String str, LocationInfo locationInfo) {
		str = ResponseCreator.getTimestampValue(locationInfo.getTimestamp());
		str = ResponseCreator.addLatLonValue(str, locationInfo);
		str = ResponseCreator.addFloatValue(str, ACCURACY, locationInfo.getAccuracyInMtr(), 0, Unit.Meter);
		str = ResponseCreator.addFloatValue(str, BEARING, locationInfo.getBearingInDegree(), 0, Unit.Degree);
		// TODO check if there is no speed!
		str = ResponseCreator.addFloatValue(str, SPEED, locationInfo.getSpeedInMtrPerSecs() * 3.6f, 1, Unit.KilometerPerHour);
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
