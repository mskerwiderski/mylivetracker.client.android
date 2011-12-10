package de.msk.mylivetracker.client.android.status;

/**
 * GpsStateInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class GpsStateInfo extends AbstractInfo {
	private static GpsStateInfo gpsStateInfo = null;
	public static void update(int countSatellites) {
		gpsStateInfo = 
			GpsStateInfo.createNewGpsStateInfo(gpsStateInfo, countSatellites);
	}
	public static GpsStateInfo get() {
		return gpsStateInfo;
	}	
	public static void reset() {
		gpsStateInfo = null;
	}
	
	private Integer countSatellites = 0;
	
	private GpsStateInfo(int countSatellites) {
		this.countSatellites = countSatellites;		
	}
	
	public static GpsStateInfo createNewGpsStateInfo(
		GpsStateInfo currGpsStateInfo, Integer countSatellites) {		
		return new GpsStateInfo(countSatellites);			
	}
	
	public boolean isAccurate() {
		if (this.countSatellites == null) {
			return false;
		}
		return this.countSatellites >= 4;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(countSatellites).append("]");
		return builder.toString();
	}

	/**
	 * @return the countSatellites
	 */
	public Integer getCountSatellites() {
		return countSatellites;
	}
}
