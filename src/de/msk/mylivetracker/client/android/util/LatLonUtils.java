package de.msk.mylivetracker.client.android.util;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * LatLonUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-11-09 initial.
 * 
 */
public class LatLonUtils {

	private LatLonUtils() {
	}
	
	public static class DmsDsc {
		public double degrees;
		public double minutes;
		public double seconds;
		public DmsDsc(double degrees, double minutes, double seconds) {
			this.degrees = degrees;
			this.minutes = minutes;
			this.seconds = seconds;
		}
		@Override
		public String toString() {
			return "DmsDsc [degrees=" + degrees + ", minutes=" + minutes
				+ ", seconds=" + seconds + "]";
		}
	}

	public static double dmsToDec(double degrees, double minutes, double seconds) {
		double decimal;
		double frac = minutes / 60 + seconds / 3600;
		if (degrees < 0)
			decimal = degrees - frac;
		else
			decimal = degrees + frac;
		return decimal;
	}

	
	public static DmsDsc decToDms(double decimal) {
		double frac, dfSec;
		double degrees, minutes, seconds;
		degrees = Math.floor(decimal);
		if (degrees < 0)
			degrees = degrees + 1;
		frac = Math.abs(decimal - degrees);
		dfSec = frac * 3600;
		minutes = Math.floor(dfSec / 60);
		seconds = dfSec - minutes * 60;
		if (Math.rint(seconds) == 60) {
			minutes = minutes + 1;
			seconds = 0;
		}
		if (Math.rint(minutes) == 60) {
			if (degrees < 0)
				degrees = degrees - 1;
			else
				degrees = degrees + 1;
			minutes = 0;
		}
		return new DmsDsc(degrees, minutes, seconds);
	}

	public enum PosType {
		Latitude(4), Longitude(5);
		private int digits;
		private PosType(int digits) {
			this.digits = digits;
		}
		public int getDigits() {
			return digits;
		}
	}
	
	public enum AxisDir {
		North("N", 1d), West("W", -1d), South("S", -1d), East("E", 1d);
		private String abb;
		private double mul;
		private static Map<String, AxisDir> dirMap;
		private static Map<String, AxisDir> getDirMap() {
			if (dirMap == null) {
				dirMap = new HashMap<String, AxisDir>();
			}
			return dirMap;
		}
		private AxisDir(String abb, double mul) {
			this.abb = abb;
			this.mul = mul;
			getDirMap().put(abb, this);
		}
		public String getAbb() {
			return abb;
		}
		public double getMul() {
			return mul;
		}
		public static AxisDir get(String abb) {
			AxisDir dir = getDirMap().get(abb);
			if (dir == null) {
				throw new IllegalArgumentException(
					"direction not found, unknown abbreviation '" + 
					abb	+ "'.");
			}
			return dir;
		}
		public static AxisDir get(double degrees, PosType posType) {
			return posType.equals(PosType.Latitude) ? 
				((degrees > 0) ? AxisDir.North : AxisDir.South) :
				((degrees > 0) ? AxisDir.East : AxisDir.West);
		}
	}

	public static class Wgs84Dsc {
		public String value;
		public AxisDir axisDir;
		public Wgs84Dsc(String value, AxisDir axisDir) {
			if (StringUtils.isEmpty(value)) {
				throw new IllegalArgumentException("value must not be empty.");
			}
			if (axisDir == null) {
				throw new IllegalArgumentException("axisDir must not be null.");
			}
			this.value = value;
			this.axisDir = axisDir;
		}
		public double getDblValue() {
			return Double.parseDouble(this.value);
		}
		public String toNmea0183String() {
			return this.value + "," + this.axisDir.getAbb();
		}
		@Override
		public String toString() {
			return "Wgs84Dsc [value=" + value + ", axisDir=" + axisDir + "]";
		}
	}
	
	public static Wgs84Dsc decToWgs84(double decimal, PosType posType) {
		if (posType == null) {
			throw new IllegalArgumentException("posType must not be null.");
		}
		DmsDsc dmsDsc = decToDms(decimal);
		double wgs84DblVal = 
			Math.abs(dmsDsc.degrees) * 100 + 
			dmsDsc.minutes + 
			dmsDsc.seconds / 60;
		DecimalFormat fmt = new DecimalFormat(
			"0.0000", new DecimalFormatSymbols(Locale.ENGLISH));
		String wgs84Val = fmt.format(wgs84DblVal);
		wgs84Val = StringUtils.leftPad(wgs84Val, posType.getDigits() + 5, '0');
		return new Wgs84Dsc(wgs84Val, AxisDir.get(dmsDsc.degrees, posType));
	}
	
	public static double wgs84ToDec(Wgs84Dsc wgs84Dsc) {
		if (wgs84Dsc == null) {
			throw new IllegalArgumentException("wgs84Dsc must not be null.");
		}
		Double gm = wgs84Dsc.getDblValue() / 100d;
		Double g = Math.floor(gm);
		Double gk = (gm - g) / 0.6;
		double decimal = g + gk;
		decimal = decimal * wgs84Dsc.axisDir.getMul();
		return decimal;
	}
}
