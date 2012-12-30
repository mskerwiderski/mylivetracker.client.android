package de.msk.mylivetracker.client.android.mainview;

import android.os.Bundle;
import de.msk.mylivetracker.client.android.R;

/**
 * classname: MainDetailsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class MainDetailsActivity extends AbstractMainActivity {

	private static MainDetailsActivity mainDetailsActivity = null;	
	
	public static MainDetailsActivity get() {
		return mainDetailsActivity;
	}
	
	public static boolean isActive() {
		return mainDetailsActivity != null;
	}
	
	public static void close() {
		if (mainDetailsActivity != null) {
			mainDetailsActivity.finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_details);
		mainDetailsActivity = this;
		
		this.setTitle(R.string.tiMainDetails);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MainActivity.get().updateView();		
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mainDetailsActivity = null;
	}

	@Override
	public void onSwitchToView(boolean next) {
		close();
	}	
	
	@Override
	public void onBackPressed() {
		close();
	}
}
