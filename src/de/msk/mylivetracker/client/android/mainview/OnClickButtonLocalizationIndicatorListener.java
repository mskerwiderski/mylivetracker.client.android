package de.msk.mylivetracker.client.android.mainview;

import android.content.Intent;
import android.provider.Settings;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonLocalizationIndicatorListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonLocalizationIndicatorListener extends ASafeOnClickListener {
	@Override
	public void onClick() {			
		final MainActivity mainActivity = MainActivity.get();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		mainActivity.startActivity(intent);		
	}
}
