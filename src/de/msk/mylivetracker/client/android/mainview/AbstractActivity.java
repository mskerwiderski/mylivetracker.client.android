package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;

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
	protected void onCreate(Bundle savedInstanceState) {
		this.startActivity(new Intent(this, PinCodeQueryActivity.class));
		super.onCreate(savedInstanceState);
	}
}
