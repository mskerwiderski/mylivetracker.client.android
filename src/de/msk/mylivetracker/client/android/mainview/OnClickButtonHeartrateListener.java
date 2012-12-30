package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.other.OtherPrefsActivity;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonHeartrateListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-30	revised for v1.5.x.
 * 
 */
public class OnClickButtonHeartrateListener extends ASafeOnClickListener {
	@Override
	public void onClick() {			
		MainActivity.get().
			startActivityWithWarningDlgIfTrackRunning(OtherPrefsActivity.class);	
	}
}
