package de.msk.mylivetracker.client.android.listener;

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

	private static GpsStateListener gpsStateListener = null;
	
	public static GpsStateListener get() {
		if (gpsStateListener == null) {
			gpsStateListener = new GpsStateListener();			
		} 
		return gpsStateListener;
	}
	
	@Override
	public void onGpsStatusChanged(int event) {
		int countSatellites = 0;
		GpsStatus gpsStatus = LocationManagerUtils.
			getLocationManager().getGpsStatus(null);
		Iterator<GpsSatellite> iterator = gpsStatus.getSatellites().iterator();
		while (iterator.hasNext()) {
			GpsSatellite gpsSatellite = iterator.next();
			if (gpsSatellite.usedInFix()) {
				countSatellites++;
			}
		}
		
		GpsStateInfo.update(countSatellites);
		MainActivity.get().updateView();
	}
}
