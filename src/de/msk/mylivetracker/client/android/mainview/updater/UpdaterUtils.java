package de.msk.mylivetracker.client.android.mainview.updater;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.util.FormatUtils.Unit;

/**
 * classname: UpdaterUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class UpdaterUtils {

	private UpdaterUtils() {
	}	
	
	public static TextView tv(Activity act, int id) {
		return (TextView)act.findViewById(id);
	}
	
	private static final String NO_VALUE = "--";
	private static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss";	
	
	public static String getNoValue() {
		return NO_VALUE;
	}
	
	public static String getStr(String val) {
		return getStr(val, NO_VALUE);
	}
	
	public static String getStr(String val, String def) {
		String viewStr = val;
		if (StringUtils.isEmpty(val)) {
			viewStr = def; 
		}
		return viewStr;
	}	
	
	public static String getIntStr(Integer value) {
		return getIntStr(value, null);
	}
	
	public static String getIntStr(Integer value, Unit unit) {
		if (value == null) return getNoValue();
		return getLongStr(Long.valueOf(value), unit);
	}
	
	public static String getLongStr(Long value) {
		return getLongStr(value, null);
	}
	
	public static String getLongStr(Long value, Unit unit) {
		if (value == null) return getNoValue();
		return value.toString() + (unit == null ? "" : unit.getTxt());
	}
	
	public static String getFltStr(Float value, int decimalPlaces) {
		return getFltStr(value, decimalPlaces, null);
	}
	
	public static String getFltStr(Float value, int decimalPlaces, Unit unit) {
		if (value == null) return getNoValue();
		return getDblStr(Double.valueOf(value), decimalPlaces, unit);
	}

	public static String getDblStr(Double value, int decimalPlaces) {
		return getDblStr(value, decimalPlaces, null);
	}
	
	public static String getDblStr(Double value, int decimalPlaces, Unit unit) {
		if (value == null) return getNoValue();
		String res = null;
		if (decimalPlaces > 0) {
			String format = "#,##0." + 
				StringUtils.repeat("0", decimalPlaces);				
			DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(App.getLocale());
			df.applyPattern(format);
			res = df.format(value);
		} else {
			res = String.valueOf(Math.round(value));
		}		
		res += (unit == null ? "" : unit.getTxt());
		return res;
	}	
	
	public static String getTimestampStr(Date timestamp) {
		if (timestamp == null) return getNoValue();
		SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
		return "[" + sdf.format(timestamp) + "]";
	}
}
