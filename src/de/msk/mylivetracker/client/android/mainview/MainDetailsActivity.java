package de.msk.mylivetracker.client.android.mainview;

import android.os.Bundle;
import android.widget.Button;
import de.msk.mylivetracker.client.android.R;

/**
 * MainDetailsActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-18 initial.
 * 
 */
public class MainDetailsActivity extends AbstractMainActivity {

	private static MainDetailsActivity mainDetailsActivity = null;	
	
	public static MainDetailsActivity get() {
		return mainDetailsActivity;
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
	
	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.mainview.AbstractMainActivity#onSwitchToView(boolean)
	 */
	@Override
	public void onSwitchToView(boolean next) {
		this.finish();
		mainDetailsActivity = null;	
	}	
}
