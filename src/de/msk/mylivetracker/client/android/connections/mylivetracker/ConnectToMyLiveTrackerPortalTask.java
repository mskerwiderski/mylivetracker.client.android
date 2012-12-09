package de.msk.mylivetracker.client.android.connections.mylivetracker;

import java.net.URL;

import android.os.AsyncTask;
import de.msk.mylivetracker.client.android.App.ConfigDsc;
import de.msk.mylivetracker.client.android.connections.mylivetracker.ConnectToMyLiveTrackerPortalActivity.ProgressDialogHandler;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.Preferences.TransferProtocol;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.rpc.JsonRpcHttpClient;
import de.msk.mylivetracker.commons.rpc.ConnectToMyLiveTrackerPortalRequest;
import de.msk.mylivetracker.commons.rpc.ConnectToMyLiveTrackerPortalResponse;
import de.msk.mylivetracker.commons.rpc.RpcResponse.ResultCode;

/**
 * ConnectToMyLiveTrackerPortalTask.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-16 initial.
 * 
 */
public class ConnectToMyLiveTrackerPortalTask extends
	AsyncTask<ConnectToMyLiveTrackerPortalRequest, Integer, ConnectToMyLiveTrackerPortalResponse> {

	private ProgressDialogHandler progressDialogHandler = null;
	
	public ConnectToMyLiveTrackerPortalTask(ProgressDialogHandler progressDialogHandler) {
		this.progressDialogHandler = progressDialogHandler;
	}
	
	@Override
	protected ConnectToMyLiveTrackerPortalResponse doInBackground(ConnectToMyLiveTrackerPortalRequest... requests) {
		ConnectToMyLiveTrackerPortalResponse response = null;
		Preferences prefs = Preferences.get();
		try {	
			if (!MainActivity.get().isDataConnectionActive()) {
				throw new RuntimeException(MainActivity.get().getResources().getString(R.string.txErr_NoDataConnection));
			}
			JsonRpcHttpClient rcpClient = new JsonRpcHttpClient(new URL(
				ConfigDsc.getPortalRpcUrl()));
			rcpClient.setConnectionTimeoutMillis(10000);
			rcpClient.setReadTimeoutMillis(5000);
			response = (ConnectToMyLiveTrackerPortalResponse) rcpClient.invoke("connectToMyLiveTrackerPortal",
				new Object[] { requests[0] }, ConnectToMyLiveTrackerPortalResponse.class);
			
			if (response.getResultCode().isSuccess()) {				
	        	prefs.setTransferProtocol(TransferProtocol.mltTcpEncrypted);
	        	prefs.setServer(response.getServerAddress());
	        	prefs.setPort(response.getServerPort());
	        	prefs.setPath("");
	        	prefs.setDeviceId(response.getSenderId());
	        	prefs.setUsername(response.getSenderUsername());
	        	prefs.setPassword(response.getSenderPassword());
	        	prefs.setTrackName(response.getTrackName());
	        	prefs.setCloseConnectionAfterEveryUpload(false);
	        	prefs.setFinishEveryUploadWithALinefeed(false);
	        	Preferences.save();
			}
		} catch (Throwable e) {
			response = new ConnectToMyLiveTrackerPortalResponse(MainActivity.getLocale().getLanguage(),
				ResultCode.InternalServerError, e.getMessage());
		}
		return response;
	}

	@Override
	protected void onPostExecute(ConnectToMyLiveTrackerPortalResponse response) {
		ProgressDialogHandler.closeProgressDialog(
			progressDialogHandler, response);				
		super.onPostExecute(response);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
}