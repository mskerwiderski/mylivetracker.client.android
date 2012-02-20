package de.msk.mylivetracker.client.android.preferences.linksender;

import java.net.URL;

import android.os.AsyncTask;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.Preferences.TransferProtocol;
import de.msk.mylivetracker.client.android.preferences.linksender.LinkSenderActivity.ProgressDialogHandler;
import de.msk.mylivetracker.client.android.rpc.JsonRpcHttpClient;
import de.msk.mylivetracker.commons.rpc.LinkSenderRequest;
import de.msk.mylivetracker.commons.rpc.LinkSenderResponse;
import de.msk.mylivetracker.commons.rpc.RpcResponse.ResultCode;

/**
 * LinkSenderTask.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-16 initial.
 * 
 */
public class LinkSenderTask extends
	AsyncTask<LinkSenderRequest, Integer, LinkSenderResponse> {

	private ProgressDialogHandler progressDialogHandler = null;
	
	public LinkSenderTask(ProgressDialogHandler progressDialogHandler) {
		this.progressDialogHandler = progressDialogHandler;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected LinkSenderResponse doInBackground(LinkSenderRequest... requests) {
		LinkSenderResponse response = null;
		Preferences prefs = Preferences.get();
		try {	
			if (!MainActivity.get().isDataConnectionActive()) {
				throw new RuntimeException(MainActivity.get().getResources().getString(R.string.txErr_NoDataConnection));
			}
			JsonRpcHttpClient rcpClient = new JsonRpcHttpClient(new URL(
				MainActivity.get().getMyLiveTrackerRpcServiceUrl()));
			rcpClient.setConnectionTimeoutMillis(10000);
			rcpClient.setReadTimeoutMillis(5000);
			response = (LinkSenderResponse) rcpClient.invoke("linkSender",
				new Object[] { requests[0] }, LinkSenderResponse.class);
			
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
			e.printStackTrace();
			MainActivity.logInfo(e.getMessage());
			response = new LinkSenderResponse(MainActivity.getLocale().getLanguage(),
				ResultCode.InternalServerError, e.getMessage());
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(LinkSenderResponse response) {
		ProgressDialogHandler.closeProgressDialog(
			progressDialogHandler, response);				
		super.onPostExecute(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
}