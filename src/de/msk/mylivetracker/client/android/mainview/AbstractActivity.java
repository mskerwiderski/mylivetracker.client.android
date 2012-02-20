package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import de.msk.mylivetracker.client.android.R;

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

}
