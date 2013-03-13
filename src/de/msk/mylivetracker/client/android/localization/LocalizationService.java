package de.msk.mylivetracker.client.android.localization;

import android.location.LocationManager;
import android.os.Message;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: LocalizationService
 * 
 * @author michael skerwiderski, (c)2013
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-08	revised for v1.5.x.
 * 
 */
public class LocalizationService extends AbstractService {

	private LocationListener locationListener = null;
	private GpsStateListener gpsStateListener = null;
	
	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return LocalizationServiceThread.class;
	}

	@Override
	public NotificationDsc getNotificationDsc() {
		return null;
	}

	@Override
	public void onReceiveMessage(Message msg) {
		LogUtils.infoMethodIn(LocalizationService.class, "onReceiveMessage", msg);
		if ((this.locationListener != null) && (this.gpsStateListener != null)) {
			boolean expired = 
				((new DateTime()).getAsMSecs() - 
				this.locationListener.getLastLocationReceived().getAsMSecs()) > 
				PrefsRegistry.get(LocalizationPrefs.class).getMaxWaitingPeriodForGpsFixInMSecs();
			LogUtils.infoMethodState(LocalizationService.class, "onReceiveMessage", 
				"locationListener expired", expired);
			if (expired) {
				this.stopLocationListener();
				this.startLocationListener();
			}
		}
		LogUtils.infoMethodOut(LocalizationService.class, "onReceiveMessage");
	}

	private void startLocationListener() {
		LogUtils.infoMethodIn(LocalizationService.class, "startLocationListener");
		LogUtils.infoMethodState(LocalizationService.class, "startLocationListener", 
			"locationListener", this.locationListener != null);
		if (this.locationListener == null) {
			this.locationListener = new LocationListener();
			LocalizationPrefs prefs = 
				PrefsRegistry.get(LocalizationPrefs.class);
			LocationManager locationManager = 
				LocationManagerUtils.getLocationManager();
			this.gpsStateListener = new GpsStateListener();
			locationManager.
				addGpsStatusListener(this.gpsStateListener);
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
		LogUtils.infoMethodOut(LocalizationService.class, "startLocationListener");
	}
	
	private void stopLocationListener() {
		LogUtils.infoMethodIn(LocalizationService.class, "stopLocationListener");
		LogUtils.infoMethodState(LocalizationService.class, "stopLocationListener", 
			"locationListener", this.locationListener != null);
		if (this.locationListener != null) {
			LocationManager locationManager = 
			LocationManagerUtils.getLocationManager();
			locationManager.removeUpdates(this.locationListener);
			locationManager.removeGpsStatusListener(this.gpsStateListener);
			this.locationListener = null;
		}
		LogUtils.infoMethodOut(LocalizationService.class, "stopLocationListener");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.startLocationListener();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.stopLocationListener();
	}
}
