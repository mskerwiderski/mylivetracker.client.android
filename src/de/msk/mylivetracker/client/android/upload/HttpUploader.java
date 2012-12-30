package de.msk.mylivetracker.client.android.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * classname: HttpUploader
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class HttpUploader extends AbstractUploader {
		
	public HttpUploader(IProtocol protocol) {
		super(protocol);		
	}
	
	@Override
	public UploadResult upload(String dataStr) {		
		BufferedReader bufferedReader = null;
		int countPositionsUploaded = 0;
		String resultCode = null;
		ServerPrefs prefs = PrefsRegistry.get(ServerPrefs.class);
		String urlStr = prefs.getServer();
		urlStr += ":" + prefs.getPort();
		urlStr += "/" + prefs.getPath();
		long start = SystemClock.elapsedRealtime();		
		try {			
			this.checkConnection();
			URL url = new URL("http://" + urlStr + dataStr);
			URLConnection urlConn = url.openConnection();
			urlConn.setReadTimeout(5000);
			bufferedReader = new BufferedReader(
				new InputStreamReader(urlConn.getInputStream()));
			resultCode = bufferedReader.readLine();						
			countPositionsUploaded = 1;
		} catch (Exception e) {
			resultCode = MainActivity.get().getString(R.string.txMain_UploadResultFailed);
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();				
			}
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// noop.
				}
			}
		}
		long stop = SystemClock.elapsedRealtime();
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
			stop - start,
			countPositionsUploaded, resultCode);
	}

	@Override
	public void finish() {
		// noop.
	}

	@Override
	public String toString() {
		return "HttpUploader";
	}	
}
