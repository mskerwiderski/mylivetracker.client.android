package de.msk.mylivetracker.client.android.mylivetrackerportal;

import java.net.URL;

import android.os.AsyncTask;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.App.ConfigDsc;
import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mylivetrackerportal.MyLiveTrackerPortalConnectActivity.ProgressDialogHandler;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.BufferSize;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs.TransferProtocol;
import de.msk.mylivetracker.client.android.rpc.JsonRpcHttpClient;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.commons.rpc.ConnectToMyLiveTrackerPortalRequest;
import de.msk.mylivetracker.commons.rpc.ConnectToMyLiveTrackerPortalResponse;
import de.msk.mylivetracker.commons.rpc.RpcResponse.ResultCode;

/**
 * classname: MyLiveTrackerPortalConnectTask
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class MyLiveTrackerPortalConnectTask extends
	AsyncTask<ConnectToMyLiveTrackerPortalRequest, Integer, ConnectToMyLiveTrackerPortalResponse> {

	private ProgressDialogHandler progressDialogHandler = null;
	
	public MyLiveTrackerPortalConnectTask(ProgressDialogHandler progressDialogHandler) {
		this.progressDialogHandler = progressDialogHandler;
	}
	
	@Override
	protected ConnectToMyLiveTrackerPortalResponse doInBackground(ConnectToMyLiveTrackerPortalRequest... requests) {
		ConnectToMyLiveTrackerPortalResponse response = null;
		ServerPrefs serverPrefs = PrefsRegistry.get(ServerPrefs.class);
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
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
				protocolPrefs.setTransferProtocol(TransferProtocol.mltTcpEncrypted);
				serverPrefs.setServer(response.getServerAddress());
				serverPrefs.setPort(response.getServerPort());
				serverPrefs.setPath("");
	        	AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
	        	accountPrefs.setDeviceId(response.getSenderId());
	        	accountPrefs.setUsername(response.getSenderUsername());
	        	accountPrefs.setPassword(response.getSenderPassword());
	        	accountPrefs.setTrackName(response.getTrackName());
	        	protocolPrefs.setCloseConnectionAfterEveryUpload(false);
	        	protocolPrefs.setFinishEveryUploadWithALinefeed(false);
	        	protocolPrefs.setUplPositionBufferSize(BufferSize.pos5);
	        	PrefsRegistry.save(AccountPrefs.class);
	        	PrefsRegistry.save(ServerPrefs.class);
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