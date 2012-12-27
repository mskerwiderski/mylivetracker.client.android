package de.msk.mylivetracker.client.android.listener;

import android.location.Location;
import android.os.Bundle;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.LocationInfo;

/**
 * LocationListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2011-08-11 	initial.
 * 
 */
public class LocationListener implements android.location.LocationListener {
	
	private static LocationListener locationListener = null;
	private boolean active = false;
	
	public static LocationListener get() {
		if (locationListener == null) {
			locationListener = new LocationListener();			
		} 
		return locationListener;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		LocationInfo.update(location);
		MainActivity.get().updateView();		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
	}	
}
