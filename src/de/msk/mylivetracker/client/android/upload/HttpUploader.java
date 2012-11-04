package de.msk.mylivetracker.client.android.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * HttpUploader.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class HttpUploader extends AbstractUploader {
		
	public HttpUploader(IProtocol protocol) {
		super(protocol);		
	}
	
	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.upload.AbstractUploader#upload(java.lang.String)
	 */
	@Override
	public UploadResult upload(String dataStr) {		
		BufferedReader bufferedReader = null;
		int countPositionsUploaded = 0;
		String resultCode = null;
				
		Preferences preferences = Preferences.get();
		String urlStr = preferences.getServer();
		urlStr += ":" + preferences.getPort();
		urlStr += "/" + preferences.getPath();
		
		try {			
			this.checkConnection();
			URL url = new URL("http://" + urlStr + dataStr); 
			bufferedReader = new BufferedReader(
				new InputStreamReader(url.openStream()));
			resultCode = bufferedReader.readLine();						
			countPositionsUploaded = 1;
		} catch (Exception e) {
			e.printStackTrace();			
			resultCode = MainActivity.get().getString(R.string.txMain_UploadResultFailed);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// noop.
				}
			}
		}
		
		if (!StringUtils.equals(resultCode, 
				MainActivity.get().getString(R.string.txMain_UploadResultOk)) &&
			!StringUtils.equals(resultCode, 
				MainActivity.get().getString(R.string.txMain_UploadResultFailed)) &&
			!StringUtils.equals(resultCode, 
				MainActivity.get().getString(R.string.txMain_UploadResultInvalid)) &&
			!StringUtils.equals(resultCode,
				MainActivity.get().getString(R.string.txMain_UploadResultError))) {
			resultCode = 
				MainActivity.get().getString(R.string.txMain_UploadResultMltError);
		}	
		
		return new UploadResult(StringUtils.equals(resultCode, 
			MainActivity.get().getString(R.string.txMain_UploadResultOk)), 
			countPositionsUploaded, resultCode);
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
		return "HttpUploader";
	}	
}
