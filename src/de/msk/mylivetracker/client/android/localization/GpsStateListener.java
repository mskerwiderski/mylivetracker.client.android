package de.msk.mylivetracker.client.android.localization;

import java.util.Iterator;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;

/**
 * classname: GpsStateListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class GpsStateListener implements Listener {

	private int currentNumberOfSatellites = 0;
	
	@Override
	public void onGpsStatusChanged(int event) {
		int numberOfSatellites = 0;
		GpsStatus gpsStatus = LocationManagerUtils.
			getLocationManager().getGpsStatus(null);
		Iterator<GpsSatellite> iterator = gpsStatus.getSatellites().iterator();
		while (iterator.hasNext()) {
			GpsSatellite gpsSatellite = iterator.next();
			if (gpsSatellite.usedInFix()) {
				numberOfSatellites++;
			}
		}
		GpsStateInfo.update(numberOfSatellites);
		this.currentNumberOfSatellites = numberOfSatellites;
		MainActivity.get().updateView();
	}

	public int getCurrentNumberOfSatellites() {
		return currentNumberOfSatellites;
	}
}
