package de.msk.mylivetracker.client.android.upload;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * DummyUploader.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-10-30 initial.
 * 
 */
public class DummyUploader extends AbstractUploader {

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
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// noop.
		}
		return new UploadResult(true, 1, 
			MainActivity.get().getString(R.string.txMain_UploadResultOk));
	}
}
