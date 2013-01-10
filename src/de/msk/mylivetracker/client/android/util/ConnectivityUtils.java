package de.msk.mylivetracker.client.android.util;

import de.msk.mylivetracker.client.android.App;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * classname: ConnectivityUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-10	revised for v1.5.x.
 * 
 */
public class ConnectivityUtils {
	
	public static boolean isDataConnectionActive() {
		boolean res = false;
		ConnectivityManager connectivityManager = getConnectivityManager();
		if (connectivityManager != null) {
			NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
			if ((nwInfo != null) && nwInfo.isConnected()) {
				res = true;
			}
		}
		return res;
	}
	
	public static boolean isDataConnectionAvailable() {
		boolean res = false;
		ConnectivityManager connectivityManager = getConnectivityManager();
		if (connectivityManager != null) {
			NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
			if ((nwInfo != null) && nwInfo.isAvailable()) {
				res = true;
			}
		}
		return res;
	}
	
	public static ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager)
			App.getCtx().getSystemService(
				Context.CONNECTIVITY_SERVICE);
	}
}
