package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;
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
public class LocationInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = -9163148611652412593L;
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
	public static void set(LocationInfo locationInfo) {
		LocationInfo.locationInfo = locationInfo;
	}
	public static void reset() {
		locationInfo = null;
	}

	private Location lastLocationUsedForDistCalc = null;
	
	private String provider = null;
	private Double latitude = null;
	private Double longitude = null;
	private Float accuracy = null;
	private Float bearing = null;
	private Double altitude = null;
	private Float speed = null;
	
	private float trackDistanceInMtr = 0.0f;
	private float mileageInMtr = 0.0f;
	
	private LocationInfo() {
	}
	
	private LocationInfo(
		Location lastLocationUsedForDistCalc, 
		String provider,
		Double latitude, Double longitude, 
		Float accuracy, Float bearing,
		Double altitude, Float speed, 
		float trackDistanceInMtr, float mileageInMtr) {
		this.lastLocationUsedForDistCalc = lastLocationUsedForDistCalc;
		this.provider = provider;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
		this.bearing = bearing;
		this.altitude = altitude;
		this.speed = speed;
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
			currLocationInfo.lastLocationUsedForDistCalc;
		if (trackStatus.trackIsRunning() && 
			(locationNew != null) &&
			isAccurate(locationNew.getAccuracy())) {			
			if ((currLocationInfo == null) || 
				(currLocationInfo.lastLocationUsedForDistCalc == null)) { 
				newLastLocationUsedForDistCalc = locationNew;
			} else if ((currLocationInfo != null) && 
				(currLocationInfo.lastLocationUsedForDistCalc != null)) {			
				float newDistanceInMtr = 
					locationNew.distanceTo(currLocationInfo.lastLocationUsedForDistCalc);
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
		return new LocationInfo( 
			newLastLocationUsedForDistCalc,
			locationNew.getProvider(),
			locationNew.getLatitude(), locationNew.getLongitude(),
			(locationNew.hasAccuracy() ? locationNew.getAccuracy() : null), 
			(locationNew.hasBearing() ? locationNew.getBearing() : null),
			(locationNew.hasAltitude() ? locationNew.getAltitude() : null),
			(locationNew.hasSpeed() ? locationNew.getSpeed() : null),
			newTrackDistanceInMtr, newMileageInMtr);			
	}
	
	public boolean hasLatLon() {
		return (this.latitude != null) && (this.longitude != null);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocationInfo [lastLocationUsedForDistCalc=")
			.append(lastLocationUsedForDistCalc).append(", provider=")
			.append(provider).append(", latitude=").append(latitude)
			.append(", longitude=").append(longitude).append(", accuracy=")
			.append(accuracy).append(", bearing=").append(bearing)
			.append(", altitude=").append(altitude).append(", speed=")
			.append(speed).append(", trackDistanceInMtr=")
			.append(trackDistanceInMtr).append(", mileageInMtr=")
			.append(mileageInMtr).append("]");
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
		if (StringUtils.equals(location.getProvider(), LocationManager.NETWORK_PROVIDER)) {
			// positions from network provider are only used,
			// if there is no valid position from gps provider.
			if ((currLocationInfo != null) && 
				currLocationInfo.hasLatLon() && 
				StringUtils.equals(currLocationInfo.provider, LocationManager.GPS_PROVIDER) && 
				currLocationInfo.isUpToDate()) {
				res = true;
			}
		}		
		return res;
	}
	
	public boolean isUpToDate() {
		if (!this.hasLatLon() || (this.getTimestamp() == null)) return false;
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
	
	public static boolean isAccurate(Float accuracy) {
		int accReq = Preferences.get().getLocAccuracyRequiredInMeter();
		if (accReq == 0) {
			return true;
		}
		if (accuracy == null) {
			return false;
		}
		return accReq >= accuracy;
	}
	
	public boolean isAccurate() {
		return isAccurate(this.accuracy);
	}
	
	public static String getProviderAbbr(LocationInfo locationInfo) {
		String providerAbbr = "?";
		if (locationInfo != null) {
			String provider = locationInfo.provider;
			if (StringUtils.equals(provider, LocationManager.GPS_PROVIDER)) {
				providerAbbr = "gps";
			} else if (StringUtils.equals(provider, LocationManager.NETWORK_PROVIDER)) {
				providerAbbr = "nw";
			}
		}
		return providerAbbr;
	}
	
	public float distanceTo(LocationInfo locationInfo) {
		if (locationInfo == null) {
			throw new IllegalArgumentException("locationInfo must not be null.");
		}
		if (!this.hasLatLon() || !locationInfo.hasLatLon()) {
			throw new IllegalArgumentException("no valid position infos found.");
		}
		float[] results = new float[1];
		Location.distanceBetween(this.latitude, this.longitude, 
			locationInfo.latitude, locationInfo.longitude, results);
		return results[0];
	}
	
	private static final String GPRMC_SEP = ",";
	private static final String GPRMC_IDENTIFIER = "GPRMC";
	private static final char GPRMC_MARKER_CHECKSUM = '*';
	private static final String GPRMC_EMPTY_SENTENCE = "GPRMC,,V,,,,,,,,,,N*53";
	public static String getLocationAsGprmcRecord(LocationInfo locationInfo) {
		if ((locationInfo == null) || !locationInfo.hasLatLon()) {
			return GPRMC_EMPTY_SENTENCE;
		}
		String record = GPRMC_IDENTIFIER + GPRMC_SEP;		
		DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
		String time = StringUtils.left(dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "HHmmss.S"), 8);
		String date = dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "ddMMyy");
		
		String speedInKnoten = "0.0";
		if (locationInfo.speed != null) {
			DecimalFormat decimalFmtSpeed = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
			speedInKnoten = decimalFmtSpeed.format(locationInfo.speed * 3.6f / 1.852f);
		}
		
		String bearingInDegrees = "0.00";
		if (locationInfo.bearing != null) {
			DecimalFormat decimalFmtBearing = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
			bearingInDegrees = decimalFmtBearing.format(locationInfo.bearing);
		}
		
		Wgs84Dsc wgs84Lat = LatLonUtils.decToWgs84(locationInfo.latitude, PosType.Latitude);
		Wgs84Dsc wgs84Lon = LatLonUtils.decToWgs84(locationInfo.longitude, PosType.Longitude);
		
		record += time + GPRMC_SEP + "A" + GPRMC_SEP + 
			wgs84Lat.toNmea0183String() + GPRMC_SEP +
			wgs84Lon.toNmea0183String() + GPRMC_SEP +
			speedInKnoten + GPRMC_SEP + 
			bearingInDegrees + GPRMC_SEP + 
			date + ",0.0,E,A";
		
		int calcChecksum = 0;
		for (int idx=0; idx < record.length(); idx++) {
			calcChecksum = calcChecksum ^ record.charAt(idx);
		}		
		String calcChecksumStr = Integer.toHexString(calcChecksum);
		calcChecksumStr = StringUtils.leftPad(calcChecksumStr, 2, '0');
		calcChecksumStr = StringUtils.upperCase(calcChecksumStr);
		record += GPRMC_MARKER_CHECKSUM + calcChecksumStr;
		return record;
	}
	public String getProvider() {
		return provider;
	}
	public Double getLatitude() {
		return latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public Float getAccuracy() {
		return accuracy;
	}
	public Float getBearing() {
		return bearing;
	}
	public Double getAltitude() {
		return altitude;
	}
	public Float getSpeed() {
		return speed;
	}
	public float getTrackDistanceInMtr() {
		return trackDistanceInMtr;
	}
	public float getMileageInMtr() {
		return mileageInMtr;
	}	
}
