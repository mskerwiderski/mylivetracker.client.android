package de.msk.mylivetracker.client.android.mainview;

import android.os.Bundle;
import android.widget.Button;
import de.msk.mylivetracker.client.android.R;

/**
 * MainDetailsActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001  2012-02-21 
 * 		o 'close' implemented.
 * 		o 'isActive' implemented-
 * 		o 'onBackPressed' implemented.
 * 		o 'onSwitchToView' and 'onBackPressed' call 'close'.
 * 000 	2011-08-18 initial.
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_details);
		mainDetailsActivity = this;
		
		this.setTitle(R.string.tiMainDetails);
		
		((Button)findViewById(R.id.btMainDetails_SendSos)).setOnClickListener(
			new OnClickButtonSosListener(this));
		((Button)findViewById(R.id.btMainDetails_SendMessage)).setOnClickListener(
			new OnClickButtonMessageListener(this));			
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
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

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.mainview.AbstractMainActivity#onSwitchToView(boolean)
	 */
	@Override
	public void onSwitchToView(boolean next) {
		close();
	}	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		close();
	}
}
