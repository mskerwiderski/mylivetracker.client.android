package de.msk.mylivetracker.client.android.util.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class OnFinishActivityListener implements OnClickListener {

	private Activity activity;
	
	public OnFinishActivityListener(Activity activity) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null!");
		}
		this.activity = activity;
	}
	
	@Override
	public void onClick(View v) {
		this.activity.finish();		
	}
}
