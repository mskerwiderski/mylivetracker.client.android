package de.msk.mylivetracker.client.android.status;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import android.location.Location;
import android.location.LocationManager;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
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
		MainActivity.logInfo("update locationInfo");
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
				MainActivity.logInfo("first accurate position received for distance calculation");
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
				MainActivity.logInfo("position used for distance calculation: " + String.valueOf(newDistanceInMtr));
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
		MainActivity.logInfo("check if position can be skipped.");
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
		MainActivity.logInfo("skip position = " + res);
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
		MainActivity.logInfo(location.getProvider());
		MainActivity.logInfo(String.valueOf(location.getAccuracy()));
		
		return accReq >= location.getAccuracy();
	}
	
	public boolean isAccurate() {
		
		return isAccurate(this.location);
	}
	
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	public static String getLocationAsGprmcRecord(LocationInfo locationInfo) {
		String record = "GPRMC,";		
		
		DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
		String time = StringUtils.left(dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "HHmmss.S"), 8);
		String date = dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "ddMMyy");
		
		DecimalFormat decimalFmt = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
		String speedInKnoten = decimalFmt.format(locationInfo.getLocation().getSpeed() / 1.852);
		
		record += time + ",A," + 
			decimal2degrees(locationInfo.getLocation().getLatitude(), 2, "N", "S") + "," +
			decimal2degrees(locationInfo.getLocation().getLongitude(), 3, "E", "W") + "," +
			speedInKnoten + ",0.0," + date + ",0.0,E,A";
		
		int calcChecksum = 0;
		for (int idx=0; idx < record.length(); idx++) {
			calcChecksum = calcChecksum ^ record.charAt(idx);
		}		
		String calcChecksumStr = Integer.toHexString(calcChecksum);
		calcChecksumStr = StringUtils.leftPad(calcChecksumStr, 2, '0');
		calcChecksumStr = StringUtils.upperCase(calcChecksumStr);
		
		return record + "*" + calcChecksumStr;
	}
	
	private static String decimal2degrees(double decimal, int digits, String pos, String neg) {
		// [+|-]48:6:20,79932
		String degrees = Location.convert(decimal, Location.FORMAT_SECONDS);
		String direction = (StringUtils.startsWith(degrees, "-") ? neg : pos);
		degrees = StringUtils.remove(degrees, "+");
		degrees = StringUtils.remove(degrees, "-");
		String[] parts = StringUtils.split(degrees, ":");
		degrees = 
			StringUtils.leftPad(parts[0], digits, '0') + 
			StringUtils.leftPad(parts[1], 2, '0') + "." + 
			StringUtils.left(StringUtils.remove(parts[2], ","), 4);
		degrees += "," + direction;
		return degrees;
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
