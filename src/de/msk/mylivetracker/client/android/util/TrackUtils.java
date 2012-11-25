package de.msk.mylivetracker.client.android.util;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonResetListener;
import de.msk.mylivetracker.client.android.mainview.OnClickButtonStartStopListener;
import de.msk.mylivetracker.client.android.status.TrackStatus;

/**
 * TrackUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-11-24 initial.
 * 
 */
public class TrackUtils {

	public static void resetTrack() {
		MainActivity.get().runOnUiThread(new ResetTrackTask());
	}
	
	public static boolean startTrack() {
		if (TrackStatus.get().trackIsRunning()) return false;
		MainActivity.get().runOnUiThread(new TrackingTask(true));
		return true;
	}
	
	public static boolean stopTrack() {
		if (!TrackStatus.get().trackIsRunning()) return false;
		MainActivity.get().runOnUiThread(new TrackingTask(false));
		return true;
	}

	private static class ResetTrackTask implements Runnable {
		@Override
		public void run() {
			OnClickButtonResetListener.resetTrack(MainActivity.get());
		}
	}
	
	private static class TrackingTask implements Runnable {
		private boolean start = true;
		
		public TrackingTask(boolean start) {
			this.start = start;
		}

		@Override
		public void run() {
			OnClickButtonStartStopListener.
				startStopTrack(MainActivity.get(), 
				this.start, true);
		}
	}
	
}
