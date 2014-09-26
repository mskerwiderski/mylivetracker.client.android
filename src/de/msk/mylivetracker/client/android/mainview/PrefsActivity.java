package de.msk.mylivetracker.client.android.mainview;

import android.os.Bundle;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.status.PinCodeStatus;
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

	@Override
	protected boolean isPrefsActivity() {
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		PinCodeStatus.get().reset();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		LogUtils.infoMethodIn(PrefsActivity.class, "onStart", "PrefsActivity");
		if (PinCodeQueryPrefs.protectSettingsOnlyConfigured()) {
			if (PinCodeStatus.get().isCanceled()) {
				LogUtils.info(PrefsActivity.class, "finish activity because pin code query failed");
				this.finish();
			} else if (!PinCodeStatus.get().isSuccessful()) {
				LogUtils.info(PrefsActivity.class, "run pinCodeQuery");
				PinCodeQueryActivity.runPinCodeQuery();
			}
		} 
		super.onStart();
		LogUtils.infoMethodOut(PrefsActivity.class, "onStart", "PrefsActivity");
	}
}
