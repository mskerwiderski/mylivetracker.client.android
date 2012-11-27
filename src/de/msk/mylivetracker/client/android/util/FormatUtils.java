package de.msk.mylivetracker.client.android.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * FormatUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-11-27 initial.
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
		if (decimalPositions <= 0) {
			throw new IllegalArgumentException("decimalPositions must not be null.");
		}
		String decimalFmtStr = StringUtils.rightPad("0.", decimalPositions + 2, '0');
		DecimalFormat decimalFmt = new DecimalFormat(decimalFmtStr, 
			new DecimalFormatSymbols(Locale.ENGLISH));
		return decimalFmt.format(value);
	}
}
