package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * OnClickButtonAutoModeIndicatorListener.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-02-19 initial.
 * 
 */
public class OnClickButtonAutoModeIndicatorListener implements OnClickListener {
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {			
		MainActivity.get().startActivityPrefsAuto();
	}
}
