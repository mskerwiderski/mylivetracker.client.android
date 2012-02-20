package de.msk.mylivetracker.client.android.listener;

import java.util.Iterator;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;

/**
 * GpsStateListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-11 initial.
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
	
	/* (non-Javadoc)
	 * @see android.location.GpsStatus.Listener#onGpsStatusChanged(int)
	 */
	@Override
	public void onGpsStatusChanged(int event) {
		
		MainActivity.logInfo(String.valueOf(event));
		int countSatellites = 0;
		GpsStatus gpsStatus = MainActivity.get().getLocationManager().getGpsStatus(null);
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
