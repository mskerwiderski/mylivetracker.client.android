package de.msk.mylivetracker.client.android.util.listener;

import android.app.Activity;

/**
 * classname: OnFinishActivityListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnFinishActivityListener extends ASafeOnClickListener {

	private Activity activity;
	
	public OnFinishActivityListener(Activity activity) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null!");
		}
		this.activity = activity;
	}
	
	@Override
	public void onClick() {
		this.activity.finish();		
	}
}
