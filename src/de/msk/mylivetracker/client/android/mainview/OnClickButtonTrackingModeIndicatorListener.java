package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefsActivity;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonTrackingModeIndicatorListener
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-01-25	origin.
 * 
 */
public class OnClickButtonTrackingModeIndicatorListener extends ASafeOnClickListener {
	@Override
	public void onClick() {			
		MainActivity.get().
			startActivityWithWarningDlgIfTrackRunning(TrackingModePrefsActivity.class);
	}
}
