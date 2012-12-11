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

	public static void show(Context ctx, int message) {
		SimpleInfoDialog dlg = new SimpleInfoDialog(ctx, message);
		dlg.show();
	}
	
	public static void show(Context ctx, int message, Object... args) {
		SimpleInfoDialog dlg = new SimpleInfoDialog(ctx, message, args);
		dlg.show();
	}
	
	public static void show(Context ctx, String message) {
		SimpleInfoDialog dlg = new SimpleInfoDialog(ctx, message);
		dlg.show();
	}
	
	private SimpleInfoDialog(Context ctx, int message) {
		super(ctx, message);
	}
	
	private SimpleInfoDialog(Context ctx, int message, Object... args) {
		super(ctx, ctx.getString(message, args));
	}

	private SimpleInfoDialog(Context ctx, String message) {
		super(ctx, message);
	}

	@Override
	public void onOk() {
		// noop.		
	}
}
