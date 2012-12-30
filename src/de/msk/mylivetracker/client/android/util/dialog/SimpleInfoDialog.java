package de.msk.mylivetracker.client.android.util.dialog;

import android.content.Context;

/**
 * classname: SimpleInfoDialog
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
