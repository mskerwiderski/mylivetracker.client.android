package de.msk.mylivetracker.client.android.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * classname: TimeUtils
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-08-15	origin.
 * 
 */
public class TimeUtils {
	public static long getElapsedTimeInMSecs() {
		return (new Date()).getTime();
	}
	
	public static final String TRACK_TIME_FORMATTED_ZERO = "000:00:00";
	
	public static String getTrackTimeFormatted(long msecs) {
		LogUtils.infoMethodIn(TimeUtils.class, "getTrackTimeFormatted", msecs);
		String time = "";
		if (msecs <= 0) {
			time = TRACK_TIME_FORMATTED_ZERO;
		} else {
			long hours = ((long)((float)msecs / (1000 * 60 * 60)) % 999);
			LogUtils.infoMethodState(TimeUtils.class, "getTrackTimeFormatted", "hours", hours);
			time += StringUtils.leftPad(String.valueOf(hours), 3, '0');
			msecs = msecs - (hours * 60 * 60 * 1000);
			long mins = (long)((float)msecs / (1000 * 60));
			LogUtils.infoMethodState(TimeUtils.class, "getTrackTimeFormatted", "mins", mins);
			time += ":" + StringUtils.leftPad(String.valueOf(mins), 2, '0');
			msecs = msecs - (mins * 60 * 1000);
			long secs = msecs / 1000;
			time += ":" + StringUtils.leftPad(String.valueOf(secs), 2, '0');
		}
		LogUtils.infoMethodOut(TimeUtils.class, "getTrackTimeFormatted", time);
		return time;
	}
}
