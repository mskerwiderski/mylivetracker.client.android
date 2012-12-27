package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.TransferProtocol;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.upload.Uploader;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * OnClickButtonSosListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-11 initial.
 * 
 */
public class OnClickButtonSosListener implements OnClickListener {
		
	private Activity activity;
	OnClickButtonSosListener(Activity activity) {
		this.activity = activity;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		final MainActivity mainActivity = MainActivity.get();
		TransferProtocol transferProtocol = 
			PrefsRegistry.get(ProtocolPrefs.class).getTransferProtocol(); 
		if (transferProtocol.supportsSendEmergencySignal()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(activity.getString(R.string.txMain_QuestionSendSosSignal))
				.setCancelable(false)
				.setPositiveButton(activity.getString(R.string.btYes), 
					new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						EmergencySignalInfo.update(true);
						Uploader.uploadOneTime();
						mainActivity.updateView();
						dialog.cancel();
						Toast.makeText(activity.getApplicationContext(), 
								activity.getString(R.string.txMain_InfoSendSosSignalDone),
							Toast.LENGTH_SHORT).show();	
					}
				})
				.setNegativeButton(activity.getString(R.string.btNo), 
					new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
			builder.create().show();
		} else {
			String protocol = mainActivity.getResources().getStringArray(
				R.array.transferProtocols)[transferProtocol.ordinal()];
			SimpleInfoDialog.show(activity, 
				R.string.txMain_SendEmergencySignalNotSupportedByTransferProtocol, protocol);
		}
	}	
}
