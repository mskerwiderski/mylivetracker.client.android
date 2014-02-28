package de.msk.mylivetracker.client.android.util;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonLocationListenerOnOffListener;

/**
 * classname: LocalizationUtils
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-28	origin.
 * 
 */
public class LocalizationUtils {

	public static boolean startLocalization() {
		MainActivity.get().runOnUiThread(new LocalizationTask(true));
		return true;
	}
	
	public static boolean stopLocalization() {
		MainActivity.get().runOnUiThread(new LocalizationTask(false));
		return true;
	}

	private static class LocalizationTask implements Runnable {
		private boolean start = true;
		
		public LocalizationTask(boolean start) {
			this.start = start;
		}

		@Override
		public void run() {
			OnClickButtonLocationListenerOnOffListener.
				startStopLocationListener(
					MainActivity.get(), false, this.start);
		}
	}
	
}
