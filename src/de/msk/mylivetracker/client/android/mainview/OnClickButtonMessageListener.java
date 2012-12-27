package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.message.MessageActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.TransferProtocol;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * OnClickButtonMessageListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-11 initial.
 * 
 */
public class OnClickButtonMessageListener implements OnClickListener {
	
	private Activity activity;
	
	public OnClickButtonMessageListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {			
		final MainActivity mainActivity = MainActivity.get();
		TransferProtocol transferProtocol = 
			PrefsRegistry.get(ProtocolPrefs.class).getTransferProtocol();		
		if (transferProtocol.supportsSendMessage()) {
			activity.startActivity(new Intent(activity, MessageActivity.class));
		} else {
			String protocol = mainActivity.getResources().getStringArray(
				R.array.transferProtocols)[transferProtocol.ordinal()];
			SimpleInfoDialog.show(activity, 
				R.string.txMain_SendMessageNotSupportedByTransferProtocol, protocol);
		}
	}
}
