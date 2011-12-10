package de.msk.mylivetracker.client.android.listener;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.status.NmeaInfo;

/**
 * NmeaListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class NmeaListener implements android.location.GpsStatus.NmeaListener {

	private static NmeaListener nmeaListener = null;
	
	public static NmeaListener get() {
		if (nmeaListener == null) {
			nmeaListener = new NmeaListener();			
		} 
		return nmeaListener;
	}
	
	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
		if (StringUtils.startsWith(nmea, NmeaInfo.GPRMC_IDENTIFIER)) {
			NmeaInfo.update(nmea);
		}
	}
}
