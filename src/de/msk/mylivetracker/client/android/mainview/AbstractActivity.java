package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * AbstractActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history 
 * 000	2011-08-19 initial.
 * 
 */
public class AbstractActivity extends Activity {

	private static int activityActiveCounter = 0;
	private static boolean pinCodeValid = false;
	
	protected static boolean isPinCodeValid() {
		return pinCodeValid;
	}

	protected static void setPinCodeValid() {
		AbstractActivity.pinCodeValid = true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setTitle(java.lang.CharSequence)
	 */
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(
			this.getString(R.string.app_name) + 
			" - " + title);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setTitle(int)
	 */
	@Override
	public void setTitle(int titleId) {
		this.setTitle(this.getString(titleId));
	}

	@Override
	protected void onStart() {
		LogUtils.info(this.getClass(), "onStart");
		if (Preferences.get().isPinCodeQuery() && (activityActiveCounter == 0)) {
			LogUtils.info(this.getClass(), "pinCodeQuery");
			this.startActivity(new Intent(this, PinCodeQueryActivity.class));
		}
		activityActiveCounter++;
		super.onStart();
	}

	@Override
	protected void onStop() {
		LogUtils.info(this.getClass(), "onStop");
		activityActiveCounter--;
		if (Preferences.get().isPinCodeQuery() && (activityActiveCounter == 0)) {
			pinCodeValid = false;
			LogUtils.info(this.getClass(), "pinCode invalidated");
		}
		super.onStop();
	}
}
