package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.message.MessageActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * OnClickButtonMessageListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class OnClickButtonMessageListener implements OnClickListener {
	
	private Activity activity;
	
	public OnClickButtonMessageListener(Activity activity) {
		this.activity = activity;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {			
		final MainActivity mainActivity = MainActivity.get();
		
		if (Preferences.get().getTransferProtocol().supportsSendMessage()) {
			activity.startActivity(new Intent(activity, MessageActivity.class));
		} else {
			String protocol = mainActivity.getResources().getStringArray(
				R.array.transferProtocols)[Preferences.get().getTransferProtocol().ordinal()];
			String message = mainActivity.getString(
				R.string.txMainDetails_SendMessageNotSupportedByTransferProtocol, protocol);			
			new SimpleInfoDialog(activity, message).show();
		}
	}
}
