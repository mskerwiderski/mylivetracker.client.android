package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

/**
 * classname: GpsStateInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class GpsStateInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = -590577992347885756L;

	private static GpsStateInfo gpsStateInfo = null;
	
	public static void update(int countSatellites) {
		if (TrackStatus.isInResettingState()) return;
		gpsStateInfo = 
			GpsStateInfo.createNewGpsStateInfo(gpsStateInfo, countSatellites);
	}
	public static GpsStateInfo get() {
		return gpsStateInfo;
	}	
	public static void reset() {
		gpsStateInfo = null;
	}
	public static void set(GpsStateInfo gpsStateInfo) {
		GpsStateInfo.gpsStateInfo = gpsStateInfo;
	}
	
	private Integer countSatellites = 0;
	
	private GpsStateInfo() {
	}
	
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
	
	public Integer getCountSatellites() {
		return countSatellites;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GpsStateInfo [countSatellites=")
			.append(countSatellites).append("]");
		return builder.toString();
	}
}
