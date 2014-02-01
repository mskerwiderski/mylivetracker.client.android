package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.TransferProtocol;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.Uploader;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonSosListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 001	2014-02-01	bugfix: call 'Uploader.uploadOneTime()' only if track is not already running.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonSosListener extends ASafeOnClickListener {
		
	private Activity activity;
	OnClickButtonSosListener(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onClick() {
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
						if (!TrackStatus.get().trackIsRunning()) {
							Uploader.uploadOneTime();
						}
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
