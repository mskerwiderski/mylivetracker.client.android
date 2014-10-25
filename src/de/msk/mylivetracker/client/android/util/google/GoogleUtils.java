package de.msk.mylivetracker.client.android.util.google;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.util.FormatUtils;

/**
 * classname: GoogleUtils
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.8.0
 * 
 * history:
 * 000	2014-10-20	origin.
 * 
 */
public class GoogleUtils {

	private static final String PARAM_NAME_GOOGLE_API_KEY = "GOOGLE_API_KEY";
	private static final String PARAM_NAME_GOOGLE_SHORTENER_URL = "GOOGLE_SHORTENER_URL";
	
	private static final String GOOGLE_MAPS_URL_TEMPLATE = 
		"https://maps.google.de/maps?q=$LAT,$LON";
	
	private GoogleUtils() {
	}
	
	public static String getGoogleApiKey() {
		return App.getManifestMetaData(PARAM_NAME_GOOGLE_API_KEY);
	}
	
	public static String getGoogleShortenerUrl() {
		return App.getManifestMetaData(PARAM_NAME_GOOGLE_SHORTENER_URL);
	}
	
	public static String getGoogleLatLonUrl(LocationInfo locationInfo, boolean shortenUrl) {
		String value = null;
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			value = GOOGLE_MAPS_URL_TEMPLATE;
			value = StringUtils.replace(value, "$LAT", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 6));
			value = StringUtils.replace(value, "$LON", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLongitude(), 6));
		}
		if (!StringUtils.isEmpty(value) && shortenUrl) {
			value = GoogleUrlShortener.getShortUrl(value);
		}
		return value;
	}
}
