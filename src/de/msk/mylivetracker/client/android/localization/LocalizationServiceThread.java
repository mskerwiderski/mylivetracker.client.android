package de.msk.mylivetracker.client.android.localization;

import android.location.LocationManager;
import de.msk.mylivetracker.client.android.listener.GpsStateListener;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

public class LocalizationServiceThread extends AbstractServiceThread {

	private LocationListener locationListener = null;
	
	@Override
	public void init() throws InterruptedException {
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
				this.locationListener);
		}
		if (prefs.getLocalizationMode().networkProviderEnabled()) {
			locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				prefs.getTimeTriggerInSeconds() * 1000, 
				prefs.getDistanceTriggerInMeter(), 
				this.locationListener);
		}
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

}
