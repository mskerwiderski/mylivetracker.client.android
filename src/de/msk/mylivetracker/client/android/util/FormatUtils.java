package de.msk.mylivetracker.client.android.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * classname: FormatUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class FormatUtils {

	public enum Unit {
		Percent("%"), 
		Volt("V"), 
		Degree("¡"),
		DegreeCelsius("¡C"),
		Seconds("s"),
		Milliseconds("ms"),
		Kilometer("km"),
		KilometerPerHour("km/h"),
		Meter("m"),
		BeatsPerMinute("bpm");
		
		private String txt;
		private Unit(String txt) {
			this.txt = txt;
		}
		public String getTxt() {
			return txt;
		}
	}
	
	private FormatUtils() {
	}

	public static final String getDoubleAsSimpleStr(double value, int decimalPositions) {
		if (decimalPositions < 0) {
			throw new IllegalArgumentException("decimalPositions must not be 0!");
		}
		String result = null;
		if (decimalPositions == 0) {
			result = String.valueOf(Math.round(value));
		} else {
			long rounder = 
				Math.round(Math.pow(10d, decimalPositions));
			value = (double)Math.round(value * rounder) / rounder;
			String decimalFmtStr = StringUtils.rightPad("0.", decimalPositions + 2, '0');
			DecimalFormat decimalFmt = new DecimalFormat(decimalFmtStr, 
				new DecimalFormatSymbols(Locale.ENGLISH));
			result = decimalFmt.format(value);
		}
		return result;
	}
}
