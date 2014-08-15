package de.msk.mylivetracker.client.android.util;

import java.util.Date;

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
}
