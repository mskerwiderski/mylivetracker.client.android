package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.util.LogUtils;


/**
 * classname: PrefsActivity
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-09-26	origin.
 * 
 */
public class PrefsActivity extends AbstractActivity {

	private boolean inProtectionMode = 
		PinCodeQueryPrefs.protectSettingsOnlyConfigured();
	
	@Override
	protected boolean isPrefsActivity() {
		return true;
	}
	
	@Override
	protected void onStart() {
		LogUtils.infoMethodIn(PrefsActivity.class, "onStart", this.getClass());
		if (inProtectionMode) {
			LogUtils.info(PrefsActivity.class, "start pinCodeQuery");
			PinCodeQueryActivity.runPinCodeQuery();
		} 
		inProtectionMode = false;		
		super.onStart();
		LogUtils.infoMethodState(PrefsActivity.class, "onStart",
			"inProtectionMode", inProtectionMode);
		LogUtils.infoMethodOut(PrefsActivity.class, "onStart", this.getClass());
	}
}
