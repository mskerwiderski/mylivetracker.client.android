package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
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
public class AbstractActivity extends Activity {

	private static int activityActiveCounter = 0;
	private static boolean pinCodeValid = false;
	
	protected static boolean isPinCodeValid() {
		return pinCodeValid;
	}

	protected static void setPinCodeValid() {
		AbstractActivity.pinCodeValid = true;
	}

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
	protected void onStart() {
		LogUtils.info(this.getClass(), "onStart");
		if (PrefsRegistry.get(PinCodeQueryPrefs.class).isPinCodeQueryEnabled() && 
			(activityActiveCounter == 0)) {
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
		if (PrefsRegistry.get(PinCodeQueryPrefs.class).isPinCodeQueryEnabled() && 
			(activityActiveCounter == 0)) {
			pinCodeValid = false;
			LogUtils.info(this.getClass(), "pinCode invalidated");
		}
		super.onStop();
	}
}
