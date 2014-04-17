package de.msk.mylivetracker.client.android.util;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.location.LocationManager;
import de.msk.mylivetracker.client.android.App;

/**
 * classname: LocationManagerUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-07	revised for v1.5.x.
 * 
 */
public class LocationManagerUtils {

	public static LocationManager getLocationManager() {
		return (LocationManager)App.getCtx().
			getSystemService(Context.LOCATION_SERVICE);
	}
	
	private static boolean providerAvailable(String provider, boolean checkEnabled) {
		if (StringUtils.isEmpty(provider)) {
			throw new IllegalArgumentException("provider must not be empty.");
		}
		boolean result = false;
		LocationManager locationManager = (LocationManager)
			App.get().getSystemService(Context.LOCATION_SERVICE);
		if ((locationManager != null) && 
			(locationManager.getProvider(provider) != null)) {
			result = checkEnabled ? locationManager.isProviderEnabled(provider) : true;
		}
		return result;
	}
	
	public static boolean networkProviderSupported() {
		return providerAvailable(LocationManager.NETWORK_PROVIDER, false); 
	}
	
	public static boolean gpsProviderSupported() {
		return providerAvailable(LocationManager.GPS_PROVIDER, false); 
	}
	
	public static boolean networkProviderEnabled() {
		return providerAvailable(LocationManager.NETWORK_PROVIDER, true); 
	}
	
	public static boolean gpsProviderEnabled() {
		return providerAvailable(LocationManager.GPS_PROVIDER, true); 
	}
}
