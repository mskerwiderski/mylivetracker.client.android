package de.msk.mylivetracker.client.android.mainview;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * OnClickButtonLocalizationListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-09-02
 * 
 */
public class OnClickButtonLocalizationListener implements OnClickListener {
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {			
		final MainActivity mainActivity = MainActivity.get();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		mainActivity.startActivity(intent);		
	}
}
