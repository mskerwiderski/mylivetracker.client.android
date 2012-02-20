package de.msk.mylivetracker.client.android.mainview;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * OnClickButtonWirelessLanIndicatorListener.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-02-20 initial.
 * 
 */
public class OnClickButtonWirelessLanIndicatorListener implements OnClickListener {
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {			
		final MainActivity mainActivity = MainActivity.get();
		Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		mainActivity.startActivity(intent);		
	}
}
