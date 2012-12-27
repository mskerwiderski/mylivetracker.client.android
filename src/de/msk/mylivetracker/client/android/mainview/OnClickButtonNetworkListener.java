package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.server.ServerPrefsActivity;

/**
 * OnClickButtonLocalizationListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001  2012-02-20 call server prefences page.
 * 000 	2011-09-02 initial.
 * 
 */
public class OnClickButtonNetworkListener implements OnClickListener {
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {			
		MainActivity.get().
			startActivityWithWarningDlgIfTrackRunning(ServerPrefsActivity.class);	
	}
}
