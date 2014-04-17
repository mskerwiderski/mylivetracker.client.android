package de.msk.mylivetracker.client.android.util;

import android.app.Activity;
import android.view.View;

public class SafeActivityUtils {

	public static View findViewById(Activity activity, int viewId) {
		View view = null;
		if (activity != null) {
			view = activity.findViewById(viewId);
		}
		return view;
	}
}
