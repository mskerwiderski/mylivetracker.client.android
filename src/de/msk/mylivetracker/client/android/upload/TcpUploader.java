package de.msk.mylivetracker.client.android.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.PositionBuffer;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * TcpUploader.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class TcpUploader extends AbstractUploader {

	private boolean expectServerResult = false;	
	
	public TcpUploader(IProtocol protocol, boolean expectServerResult) {
		super(protocol);
		this.expectServerResult = expectServerResult;		
	}

	private Socket socket = null;
	private PrintWriter writer = null;
	private BufferedReader reader = null;
		
	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.upload.AbstractUploader#checkConnection()
	 */
	@Override
	public void checkConnection() throws Exception {
		super.checkConnection();
		MainActivity.logInfo("data connection exists");
		Preferences prefs = Preferences.get();
		if (socket == null) {
			MainActivity.logInfo("socket is null, establish new data connection.");
			int port = prefs.getPort();
			SocketAddress serverAddress = new InetSocketAddress(
				InetAddress.getByName(prefs.getServer()), port);
			socket = new Socket();
			socket.connect(serverAddress, 3000);
			socket.setSoTimeout(3000);
			if (PositionBuffer.isEnabled()) {
				socket.setSendBufferSize(prefs.getUplPositionBufferSize().getSize() * 1024);
			} else {
				socket.setSendBufferSize(1024);
			}
			writer = new PrintWriter(
				new OutputStreamWriter(socket.getOutputStream()));
			if (expectServerResult) {
				reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()), 512);
			}			
		} 
		MainActivity.logInfo("data connection established.");
	}
	
	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.upload.AbstractUploader#upload(java.lang.String)
	 */
	@Override
	public UploadResult upload(String dataStr) {
		Preferences prefs = Preferences.get();
		int countPositionsToUpload = 1;
		String resultCode = null;
		int countPositionsUploaded = 0;
		if (PositionBuffer.isEnabled()) {
			PositionBuffer.get().add(dataStr);
			dataStr = PositionBuffer.get().getAll(
				prefs.getLineSeperator());
			countPositionsToUpload = PositionBuffer.get().size();
		}
		if (prefs.isFinishEveryUploadWithALinefeed()) {
			dataStr += prefs.getLineSeperator();
		}
		try {
			this.checkConnection();
			MainActivity.logInfo("send data: " + 
				StringUtils.length(dataStr) + " bytes");
			MainActivity.logInfo("data: " + dataStr);
			writer.print(dataStr);
	        writer.flush();	        
	        if (expectServerResult) {
	        	char[] buffer = new char[256];
	        	int cntChars = reader.read(buffer, 0, buffer.length); 
	        	resultCode = new String(buffer, 0, cntChars);
	        	MainActivity.logInfo("resultCode: " + resultCode);	        
	        } else {
	        	resultCode = 
	        		MainActivity.get().getString(R.string.txMain_UploadResultOk);
	        }
	        countPositionsUploaded = countPositionsToUpload;
	        if (PositionBuffer.isEnabled()) {
	        	PositionBuffer.get().clear();
	        }
	        if (prefs.isCloseConnectionAfterEveryUpload()) {
	        	this.finish();
	        }
	        MainActivity.logInfo("data successfully sent");
		} catch (Exception e) {
			e.printStackTrace();
			resultCode = MainActivity.get().getString(R.string.txMain_UploadResultFailed);		
			this.finish();
			MainActivity.logInfo("exception occured: finish data connection");
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
			PositionBuffer.isEnabled(), // buffer is active.
			countPositionsUploaded, resultCode);
	}
	
	@Override
	public void finish() {
		if (socket != null) {
			try {
				MainActivity.logInfo("finish data connection ...");
				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();						
				MainActivity.logInfo("finish data connection ... done.");
			} catch (IOException e) {
				// noop.
			} finally {				
				MainActivity.logInfo("data connection closed.");
			}
		}
		socket = null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TcpUploader";
	}	
}
