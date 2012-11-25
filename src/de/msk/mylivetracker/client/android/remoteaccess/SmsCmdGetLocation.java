package de.msk.mylivetracker.client.android.remoteaccess;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.status.LocationInfo;

/**
 * SmsCmdGetLocation.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public class SmsCmdGetLocation extends AbstractSmsCmdExecutor {

	public SmsCmdGetLocation(String cmdName, String sender, String... params) {
		super(cmdName, sender, params);
	}

	@Override
	public CmdDsc getCmdDsc() {
		return new CmdDsc("[google]", 0,1);
	}

	private boolean googleLocation(String... params) {
		if (params.length < 1) return false;
		return StringUtils.equalsIgnoreCase(params[0], "google");
	}
	
	private static final String GOOGLE_MAPS_URL_TEMPLATE = 
		"https://maps.google.de/maps?q=$LAT,$LON";
	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String response = "no valid location found.";
		LocationInfo locationInfo = LocationInfo.get();
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			DecimalFormat decimalFmt = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.ENGLISH));
			String latitudeStr = decimalFmt.format(locationInfo.getLatitude());
			String longitudeStr = decimalFmt.format(locationInfo.getLongitude());
			if (googleLocation(params)) {
				response = GOOGLE_MAPS_URL_TEMPLATE;
				response = StringUtils.replace(response, "$LAT", latitudeStr);
				response = StringUtils.replace(response, "$LON", longitudeStr);
			} else {
				response = latitudeStr+ "," + longitudeStr; 
			}
		}
		return response;
	}
}
