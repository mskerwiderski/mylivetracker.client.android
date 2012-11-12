package de.msk.mylivetracker.client.android.status;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import android.location.Location;
import android.location.LocationManager;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.LatLonUtils;
import de.msk.mylivetracker.client.android.util.LatLonUtils.PosType;
import de.msk.mylivetracker.client.android.util.LatLonUtils.Wgs84Dsc;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * LocationInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 001 2012-02-04 additional localizationMode implemented (gpsAndNetwork).
 * 000 2011-08-11 initial.
 * 
 */
public class LocationInfo extends AbstractInfo {
	private static LocationInfo locationInfo = null;
	public static void update(Location location) {
		if (LocationInfo.skip(locationInfo, location)) {
			return;
		}
		locationInfo = 
			LocationInfo.createNewLocationInfo(locationInfo, location);
	}
	public static LocationInfo get() {
		return locationInfo;
	}	
	public static void reset() {
		locationInfo = null;
	}
	
	private Location location = null;	
	private Location lastLocationUsedForDistCalc = null;
	private float trackDistanceInMtr = 0.0f;
	private float mileageInMtr = 0.0f;
	
	private LocationInfo(Location location,
		Location lastLocationUsedForDistCalc,
		float trackDistanceInMtr, float mileageInMtr) {
		this.location = location;
		this.lastLocationUsedForDistCalc = lastLocationUsedForDistCalc;
		this.trackDistanceInMtr = trackDistanceInMtr;		
		this.mileageInMtr = mileageInMtr;
	}
	
	private static LocationInfo createNewLocationInfo(
		LocationInfo currLocationInfo, Location locationNew) {
		TrackStatus trackStatus = TrackStatus.get();
		float newTrackDistanceInMtr = trackStatus.getTrackDistanceInMtr();
		float newMileageInMtr = trackStatus.getMileageInMtr();
		Location newLastLocationUsedForDistCalc = 
			(currLocationInfo == null) ? null : 
			currLocationInfo.getLastLocationUsedForDistCalc();
		if (trackStatus.trackIsRunning() && 
			(locationNew != null) &&
			isAccurate(locationNew)) {			
			if ((currLocationInfo == null) || 
				(currLocationInfo.getLastLocationUsedForDistCalc() == null)) { 
				newLastLocationUsedForDistCalc = locationNew;
			} else if ((currLocationInfo != null) && 
				(currLocationInfo.getLastLocationUsedForDistCalc() != null)) {			
				float newDistanceInMtr = 
					locationNew.distanceTo(currLocationInfo.getLastLocationUsedForDistCalc());
				if (isDistanceUsedForDistCalc(newDistanceInMtr)) {
					trackStatus.setTrackDistanceInMtr(
						trackStatus.getTrackDistanceInMtr() +
						newDistanceInMtr);				
					newTrackDistanceInMtr = trackStatus.getTrackDistanceInMtr();
					trackStatus.setMileageInMtr(
						trackStatus.getMileageInMtr() + 
						newDistanceInMtr);
					newMileageInMtr = trackStatus.getMileageInMtr();
					newLastLocationUsedForDistCalc = locationNew;
				}
			}
		}
		return new LocationInfo(locationNew, 
			newLastLocationUsedForDistCalc,	
			newTrackDistanceInMtr, newMileageInMtr);			
	}
		
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(location).append(":")
				.append(trackDistanceInMtr).append(":").append(mileageInMtr)
				.append("]");
		return builder.toString();
	}

	public static boolean isDistanceUsedForDistCalc(float distanceInMtr) {
		int distReqInCMtr = Preferences.get().
			getLocDistBtwTwoLocsForDistCalcRequiredInCMtr();
		if (distReqInCMtr == 0) return true;
		else return ((distanceInMtr * 100f) >= (float)distReqInCMtr);
	}
	
	private static boolean skip(LocationInfo currLocationInfo, Location location) {
		boolean res = false;
		if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
			// positions from network provider are only used,
			// if there is no valid position from gps provider.
			if ((currLocationInfo != null) && (currLocationInfo.location != null) && 
				currLocationInfo.location.getProvider().equals(LocationManager.GPS_PROVIDER) && 
				currLocationInfo.isUpToDate()) {
				res = true;
			}
		}		
		return res;
	}
	
	public boolean isUpToDate() {
		if ((this.location == null) || (this.getTimestamp() == null)) return false;
		int periodOfRestInSecs = Preferences.get().getLocTimeTriggerInSeconds();
		if (periodOfRestInSecs == 0) {
			periodOfRestInSecs = 5;
		} else {
			int addon = Math.round(periodOfRestInSecs * 1.1f);
			if (addon < 5) {
				addon = 5;
			}
			periodOfRestInSecs += addon;
		} 
		int lastPosDetectedInSecs = (int)(((new Date()).getTime() - this.getTimestamp().getTime()) / 1000);
		return lastPosDetectedInSecs <= periodOfRestInSecs;
	}
	
	public static boolean isAccurate(Location location) {
		int accReq = Preferences.get().getLocAccuracyRequiredInMeter();
		if (accReq == 0) {
			return true;
		}
		if ((location == null) || !location.hasAccuracy()) {
			return false;
		}
		return accReq >= location.getAccuracy();
	}
	
	public boolean isAccurate() {
		
		return isAccurate(this.location);
	}
	
	public Location getLocation() {
		return location;
	}

	public static String getProviderAbbr(LocationInfo locationInfo) {
		String providerAbbr = "?";
		if (locationInfo != null) {
			String provider = locationInfo.location.getProvider();
			if (StringUtils.equals(provider, LocationManager.GPS_PROVIDER)) {
				providerAbbr = "gps";
			} else if (StringUtils.equals(provider, LocationManager.NETWORK_PROVIDER)) {
				providerAbbr = "nw";
			}
		}
		return providerAbbr;
	}
	
	public static String getLocationAsGprmcRecord(LocationInfo locationInfo) {
		String record = "GPRMC,";		
		DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
		String time = StringUtils.left(dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "HHmmss.S"), 8);
		String date = dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "ddMMyy");
		
		DecimalFormat decimalFmtSpeed = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
		String speedInKnoten = decimalFmtSpeed.format(locationInfo.getLocation().getSpeed() / 1.852);
		
		DecimalFormat decimalFmtBearing = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
		String bearingInDegrees = decimalFmtBearing.format(locationInfo.getLocation().getBearing());
		
		Wgs84Dsc wgs84Lat = LatLonUtils.decToWgs84(locationInfo.location.getLatitude(), PosType.Latitude);
		Wgs84Dsc wgs84Lon = LatLonUtils.decToWgs84(locationInfo.location.getLongitude(), PosType.Longitude);
		
		record += time + ",A," + 
			wgs84Lat.toNmea0183String() + "," +
			wgs84Lon.toNmea0183String() + "," +
			speedInKnoten + "," + bearingInDegrees + "," + date + ",0.0,E,A";
		
		int calcChecksum = 0;
		for (int idx=0; idx < record.length(); idx++) {
			calcChecksum = calcChecksum ^ record.charAt(idx);
		}		
		String calcChecksumStr = Integer.toHexString(calcChecksum);
		calcChecksumStr = StringUtils.leftPad(calcChecksumStr, 2, '0');
		calcChecksumStr = StringUtils.upperCase(calcChecksumStr);
		record += "*" + calcChecksumStr;
		return record;
	}
	
	/**
	 * @return the lastLocationUsedForDistCalc
	 */
	public Location getLastLocationUsedForDistCalc() {
		return lastLocationUsedForDistCalc;
	}
	public float getTrackDistanceInMtr() {
		return trackDistanceInMtr;
	}

	public float getMileageInMtr() {
		return mileageInMtr;
	}	
}
