package de.msk.mylivetracker.client.android.upload;

import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * classname: DummyUploader
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class DummyUploader extends AbstractUploader {

	private static final long SIMULATED_UPLOAD_TIME_IN_MSECS = 300;
	
	public DummyUploader(IProtocol protocol) {
		super(protocol);
	}

	@Override
	public void finish() {
		// noop.
	}

	@Override
	public UploadResult upload(String dataStr) {
		try {
			Thread.sleep(SIMULATED_UPLOAD_TIME_IN_MSECS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();				
		}
		return new UploadResult(true, SIMULATED_UPLOAD_TIME_IN_MSECS, 1, 
			MainActivity.get().getString(R.string.txMain_UploadResultOk));
	}
}
