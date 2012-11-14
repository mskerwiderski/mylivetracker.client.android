package de.msk.mylivetracker.client.android.upload;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.os.SystemClock;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.rpc.JsonRpcHttpClient;
import de.msk.mylivetracker.client.android.status.PositionBuffer;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.client.android.util.MyLiveTrackerUtils;
import de.msk.mylivetracker.commons.rpc.RpcResponse.ResultCode;
import de.msk.mylivetracker.commons.rpc.UplEncDataPacketsRequest;
import de.msk.mylivetracker.commons.rpc.UplEncDataPacketsResponse;

/**
 * RpcUploader.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-11-04 initial.
 * 
 */
public class RpcUploader extends AbstractUploader {
		
	public RpcUploader(IProtocol protocol) {
		super(protocol);		
	}
	
	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.upload.AbstractUploader#upload(java.lang.String)
	 */
	@Override
	public UploadResult upload(String dataStr) {		
		int countPositionsUploaded = 0;
		String resultCodeStr = null;
		if (PositionBuffer.isEnabled()) {
			PositionBuffer.get().add(dataStr);
		}		
		List<String> positionsAsList = null;
		if (PositionBuffer.isEnabled()) {
			positionsAsList = PositionBuffer.get().getAsList();
		} else {
			positionsAsList = new ArrayList<String>();
			positionsAsList.add(dataStr);
		}
		UplEncDataPacketsRequest request = new UplEncDataPacketsRequest(
			MainActivity.getLocale().getLanguage(), positionsAsList);
		long start = SystemClock.elapsedRealtime();
		try {			
			this.checkConnection();
			JsonRpcHttpClient rcpClient = new JsonRpcHttpClient(new URL(
				MyLiveTrackerUtils.getPortalRpcUrl()));
				rcpClient.setConnectionTimeoutMillis(10000);
				rcpClient.setReadTimeoutMillis(5000);
			UplEncDataPacketsResponse response = (UplEncDataPacketsResponse)rcpClient.
				invoke("uploadDataPackets",
					new Object[] { request }, UplEncDataPacketsResponse.class);
			countPositionsUploaded = response.getCntPacketsReceived();
	        if (PositionBuffer.isEnabled()) {
	        	PositionBuffer.get().clear();
	        }						
			countPositionsUploaded = 1;
			if (response.getResultCode().equals(ResultCode.Ok)) {
				resultCodeStr = MainActivity.get().getString(R.string.txMain_UploadResultOk);
			} else if (response.getResultCode().equals(ResultCode.InvalidData)) {
				resultCodeStr = MainActivity.get().getString(R.string.txMain_UploadResultInvalid);
			} else if (response.getResultCode().equals(ResultCode.InternalServerError)) {
				resultCodeStr = MainActivity.get().getString(R.string.txMain_UploadResultError);
			} else if (response.getResultCode().equals(ResultCode.SenderNotAuthorized)) {
				resultCodeStr = MainActivity.get().getString(R.string.txMain_UploadResultFailed);
			}
		} catch (Throwable e) {
			resultCodeStr = MainActivity.get().getString(R.string.txMain_UploadResultFailed);
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();				
			}
		} 
		long stop = SystemClock.elapsedRealtime();
		if (!StringUtils.equals(resultCodeStr, 
				MainActivity.get().getString(R.string.txMain_UploadResultOk)) &&
			!StringUtils.equals(resultCodeStr, 
				MainActivity.get().getString(R.string.txMain_UploadResultFailed)) &&
			!StringUtils.equals(resultCodeStr, 
				MainActivity.get().getString(R.string.txMain_UploadResultInvalid)) &&
			!StringUtils.equals(resultCodeStr,
				MainActivity.get().getString(R.string.txMain_UploadResultError))) {
			resultCodeStr = 
				MainActivity.get().getString(R.string.txMain_UploadResultMltError);
		}	
		
		return new UploadResult(StringUtils.equals(resultCodeStr, 
			MainActivity.get().getString(R.string.txMain_UploadResultOk)), 
			stop - start,
			countPositionsUploaded, resultCodeStr);
	}

	@Override
	public void finish() {
		// noop.
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RpcUploader";
	}	
}
