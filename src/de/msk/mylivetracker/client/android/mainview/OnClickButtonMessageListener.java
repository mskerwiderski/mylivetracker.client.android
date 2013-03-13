package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import de.msk.mylivetracker.client.android.message.MessageActivity;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonMessageListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonMessageListener extends ASafeOnClickListener {
	
	private Activity activity;
	
	public OnClickButtonMessageListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick() {			
		activity.startActivity(new Intent(activity, MessageActivity.class));
	}
}
