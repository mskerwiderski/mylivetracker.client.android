package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.auto.AutoPrefsActivity;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonAutoIndicatorListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonAutoIndicatorListener extends ASafeOnClickListener {
	@Override
	public void onClick() {			
		MainActivity.get().
			startActivityWithWarningDlgIfTrackRunning(AutoPrefsActivity.class);
	}
}
