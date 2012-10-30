package de.msk.mylivetracker.client.android.status;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.mainview.MainActivity;

/**
 * NmeaInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class NmeaInfo extends AbstractInfo {
	public static final String GPRMC_IDENTIFIER = "$GPRMC";
	public static final char GPRMC_MARKER_CHECKSUM = '*';
	public static final String GPRMC_EMPTY_SENTENCE = "$GPRMC,,V,,,,,,,,,,N*53";
	
	private static NmeaInfo nmeaInfo = null;
	
	private static boolean isValidGprmcSentence(String gprmc) {
		MainActivity.logInfo("isValidGprmcSentence(" + gprmc + ")");
		boolean res = false;
		if (!StringUtils.isEmpty(gprmc) && 
			StringUtils.startsWith(gprmc, GPRMC_IDENTIFIER) &&	
			!StringUtils.startsWith(gprmc, GPRMC_EMPTY_SENTENCE) &&
			(gprmc.length() >= GPRMC_EMPTY_SENTENCE.length()) && 
			StringUtils.contains(gprmc, GPRMC_MARKER_CHECKSUM)) {
			res = true;
		}
		MainActivity.logInfo("isValidGprmcSentence()=" + res);
		return res;
	}
	
	public static void update(String gprmc) {
		if (isValidGprmcSentence(gprmc)) {
			int idx =StringUtils.indexOf(gprmc, GPRMC_MARKER_CHECKSUM);
			gprmc = StringUtils.substring(gprmc, 0, idx);
			MainActivity.logInfo("GPRMC (len=" + gprmc.length() + "):'" + gprmc + "'");
			nmeaInfo = 
				NmeaInfo.createNewImeaInfo(
					nmeaInfo, gprmc);
		}
	}
	
	public static NmeaInfo get() {
		return nmeaInfo;
	}
	public static void reset() {
		nmeaInfo = null;
	}
	
	private String gprmc;
	
	private NmeaInfo(String gprmc) {
		this.gprmc = gprmc;
	}	

	public static NmeaInfo createNewImeaInfo(
		NmeaInfo currImeaInfo, String gprmc) {
		gprmc = StringUtils.chomp(gprmc);
		return new NmeaInfo(gprmc); 
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NmeaInfo [gprmc=").append(gprmc).append("]");
		return builder.toString();
	}
	
	public String getGprmc() {
		return gprmc;
	}
}
