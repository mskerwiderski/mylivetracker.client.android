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

import org.apache.commons.lang3.StringUtils;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.client.android.status.PositionBufferInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * classname: TcpUploader
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
		
	@Override
	public void checkConnection() throws Exception {
		super.checkConnection();
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
		ServerPrefs serverPrefs = PrefsRegistry.get(ServerPrefs.class);
		if (socket == null) {
			int port = serverPrefs.getPort();
			SocketAddress serverAddress = new InetSocketAddress(
				InetAddress.getByName(serverPrefs.getServer()), port);
			socket = new Socket();
			socket.connect(serverAddress, 5000);
			socket.setSoTimeout(3000);
			if (PositionBufferInfo.isEnabled()) {
				socket.setSendBufferSize(
					protocolPrefs.getUplPositionBufferSize().getSize() * 1024);
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
	}
	
	@Override
	public UploadResult upload(String dataStr) {
		ProtocolPrefs prefs = PrefsRegistry.get(ProtocolPrefs.class);
		int countPositionsToUpload = 1;
		String resultCode = null;
		int countPositionsUploaded = 0;
		if (PositionBufferInfo.isEnabled()) {
			PositionBufferInfo.get().add(dataStr);
			dataStr = PositionBufferInfo.get().getAll(
				prefs.getLineSeparator());
			countPositionsToUpload = PositionBufferInfo.get().size();
		}
		if (prefs.isFinishEveryUploadWithALinefeed()) {
			dataStr += prefs.getLineSeparator();
		}
		long start = SystemClock.elapsedRealtime();
		try {
			this.checkConnection();
			writer.print(dataStr);
	        writer.flush();	        
	        if (expectServerResult) {
	        	char[] buffer = new char[256];
	        	int cntChars = reader.read(buffer, 0, buffer.length); 
	        	resultCode = new String(buffer, 0, cntChars);
	        } else {
	        	resultCode = 
        			App.getCtx().getString(R.string.txMain_UploadResultOk);
	        }
	        countPositionsUploaded = countPositionsToUpload;
	        if (PositionBufferInfo.isEnabled()) {
	        	PositionBufferInfo.get().clear();
	        }
	        if (prefs.isCloseConnectionAfterEveryUpload()) {
	        	this.finish();
	        }
		} catch (Exception e) {
			resultCode = App.getCtx().getString(R.string.txMain_UploadResultFailed);		
			this.finish();
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();				
			}
		} 
		long stop = SystemClock.elapsedRealtime();
		if (!StringUtils.equals(resultCode, 
				App.getCtx().getString(R.string.txMain_UploadResultOk)) &&
			!StringUtils.equals(resultCode, 
				App.getCtx().getString(R.string.txMain_UploadResultFailed)) &&
			!StringUtils.equals(resultCode, 
				App.getCtx().getString(R.string.txMain_UploadResultInvalid)) &&
			!StringUtils.equals(resultCode,
				App.getCtx().getString(R.string.txMain_UploadResultError))) {
			resultCode = 
				App.getCtx().getString(R.string.txMain_UploadResultMltError);
		}	
		
		return new UploadResult(StringUtils.equals(resultCode, 
			App.getCtx().getString(R.string.txMain_UploadResultOk)),
			stop - start,
			PositionBufferInfo.isEnabled(), // buffer is active.
			countPositionsUploaded, resultCode);
	}
	
	@Override
	public void finish() {
		if (socket != null) {
			try {
				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();						
			} catch (IOException e) {
				// noop.
			} finally {
				// noop.
			}
		}
		socket = null;
	}
	
	@Override
	public String toString() {
		return "TcpUploader";
	}	
}
