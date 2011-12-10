package de.msk.mylivetracker.client.android.util.dialog;

import android.content.Context;

/**
 * SimpleInfoDialog.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class SimpleInfoDialog extends AbstractInfoDialog {

	public SimpleInfoDialog(Context ctx, int message) {
		super(ctx, message);
	}

	public SimpleInfoDialog(Context ctx, String message) {
		super(ctx, message);
	}

	@Override
	public void onOk() {
		// noop.		
	}
}
