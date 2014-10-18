package de.msk.mylivetracker.client.android.mainview;

import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;

import android.app.Activity;
import android.os.Bundle;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: AbstractActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractActivity extends Activity {

	private static Integer visibleActivitiesCounter = 0;
	private static Stack<AbstractActivity> activityStack = 
		new Stack<AbstractActivity>();

	public static AbstractActivity[] getActivityArray() {
		if (activityStack.empty()) return null;
		return activityStack.toArray(new AbstractActivity[0]);
	}
	
	public static AbstractActivity getActive() {
		if (activityStack.empty()) return null;
		return activityStack.lastElement();
	}
	
	public static boolean activityExists(AbstractActivity activity) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null.");
		}
		return activityStack.contains(activity);
	}
	
	protected abstract boolean isPrefsActivity();
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(
			this.getString(R.string.app_name) + 
			" - " + title);
	}

	@Override
	public void setTitle(int titleId) {
		this.setTitle(this.getString(titleId));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		activityStack.push(this);
		LogUtils.infoMethodState(AbstractActivity.class, "onCreate", 
			"activityStack", ArrayUtils.toString(activityStack.toArray()));
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		LogUtils.infoMethodIn(AbstractActivity.class, "onStart", this.getClass());
		if (PinCodeQueryPrefs.pinCodeQueryEnabledForWholeApp() && 
			(visibleActivitiesCounter == 0)) {
			LogUtils.info(AbstractActivity.class, "start pinCodeQuery");
			PinCodeQueryActivity.runPinCodeQuery();
		} 
		visibleActivitiesCounter++;		
		super.onStart();
		LogUtils.infoMethodState(AbstractActivity.class, "onStart",
			"visibleActivitiesCounter", visibleActivitiesCounter);
		LogUtils.infoMethodOut(AbstractActivity.class, "onStart", this.getClass());
	}

	@Override
	protected void onStop() {
		LogUtils.infoMethodIn(AbstractActivity.class, "onStop", this.getClass());
		visibleActivitiesCounter--;
		if (PinCodeQueryPrefs.pinCodeQueryEnabledForWholeApp() && 
			(visibleActivitiesCounter == 0)) {
			LogUtils.info(AbstractActivity.class, "pinCode invalidated");
		}
		super.onStop();
		LogUtils.infoMethodState(AbstractActivity.class, "onStop",
			"visibleActivitiesCounter", visibleActivitiesCounter);
		LogUtils.infoMethodOut(AbstractActivity.class, "onStop", this.getClass());
	}

	@Override
	protected void onDestroy() {
		activityStack.pop();
		LogUtils.infoMethodState(AbstractActivity.class, "onDestroy", 
			"activityStack", ArrayUtils.toString(activityStack.toArray()));
		super.onDestroy();
	}
}
