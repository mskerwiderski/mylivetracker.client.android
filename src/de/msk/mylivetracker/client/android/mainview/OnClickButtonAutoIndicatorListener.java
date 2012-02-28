package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * OnClickButtonAutoIndicatorListener.
 * 
 * @author michael skerwiderski, (c)2012
 * @since 1.3.0 (22.02.2012)
 * 
 * Listener class which opens the auto preferences on click.
 * 
 */
public class OnClickButtonAutoIndicatorListener implements OnClickListener {
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {			
		MainActivity.get().startActivityPrefsAuto();
	}
}
