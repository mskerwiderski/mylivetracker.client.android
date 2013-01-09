package de.msk.mylivetracker.client.android.localization;

import android.location.Location;
import android.os.Bundle;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

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
	private DateTime lastLocationReceived = new DateTime();
	
	@Override
	public void onLocationChanged(Location location) {
		LocationInfo.update(location);
		this.lastLocationReceived = new DateTime();
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

	public DateTime getLastLocationReceived() {
		return lastLocationReceived;
	}	
}
