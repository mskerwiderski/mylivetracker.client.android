package de.msk.mylivetracker.client.android.util;

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
	
	public static boolean networkProviderSupported() {
		return (getLocationManager().getProvider(
			LocationManager.NETWORK_PROVIDER) != null); 
	}
	
	public static boolean gpsProviderSupported() {
		return (getLocationManager().getProvider(
			LocationManager.GPS_PROVIDER) != null); 
	}
	
	public static boolean networkProviderEnabled() {
		return getLocationManager().isProviderEnabled(
			LocationManager.NETWORK_PROVIDER); 
	}
	
	public static boolean gpsProviderEnabled() {
		return getLocationManager().isProviderEnabled(
			LocationManager.GPS_PROVIDER); 
	}
}
