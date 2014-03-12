package de.msk.mylivetracker.client.android.util;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonAntPlusListener;

/**
 * classname: AntPlusUtils
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-12	origin.
 * 
 */
public class AntPlusUtils {

	public static boolean startAntPlus() {
		MainActivity.get().runOnUiThread(new AntPlusTask(true));
		return true;
	}
	
	public static boolean stopLocalization() {
		MainActivity.get().runOnUiThread(new AntPlusTask(false));
		return true;
	}

	private static class AntPlusTask implements Runnable {
		private boolean start = true;
		
		public AntPlusTask(boolean start) {
			this.start = start;
		}

		@Override
		public void run() {
			OnClickButtonAntPlusListener.
				startStopAntPlus(
					MainActivity.get(), this.start);
		}
	}
	
}
