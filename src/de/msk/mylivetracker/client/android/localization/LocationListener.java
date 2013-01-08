package de.msk.mylivetracker.client.android.localization;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import de.msk.mylivetracker.client.android.listener.GpsStateListener;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;

/**
 * classname: LocationListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LocationListener implements android.location.LocationListener {
	
	private static LocationListener locationListener = null;
	private boolean active = false;
	
	private static LocationListener get() {
		if (locationListener == null) {
			locationListener = new LocationListener();			
		} 
		return locationListener;
	}

	public static boolean isActive() {
		return get().active;
	}

	public static void setActive(boolean active) {
		get().active = active;
	}

	public static void start() {
		LocalizationPrefs prefs = 
			PrefsRegistry.get(LocalizationPrefs.class);
		LocationManager locationManager = 
			LocationManagerUtils.getLocationManager();
		locationManager.
			addGpsStatusListener(GpsStateListener.get());
		if (prefs.getLocalizationMode().gpsProviderEnabled()) {
			locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				prefs.getTimeTriggerInSeconds() * 1000, 
				prefs.getDistanceTriggerInMeter(), 
				LocationListener.get());
		}
		if (prefs.getLocalizationMode().networkProviderEnabled()) {
			locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				prefs.getTimeTriggerInSeconds() * 1000, 
				prefs.getDistanceTriggerInMeter(), 
				LocationListener.get());
		}
		LocationListener.setActive(true);		
	}
	
	public static void stop() {
		LocationManager locationManager = 
			LocationManagerUtils.getLocationManager();
		locationManager.removeUpdates(LocationListener.get());
		locationManager.removeGpsStatusListener(GpsStateListener.get());
		LocationListener.setActive(false);
	}	
	
	@Override
	public void onLocationChanged(Location location) {
		LocationInfo.update(location);
		MainActivity.get().updateView();		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {		
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}	
}
