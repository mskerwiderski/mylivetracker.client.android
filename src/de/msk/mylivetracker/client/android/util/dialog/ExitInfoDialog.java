package de.msk.mylivetracker.client.android.util.dialog;

import de.msk.mylivetracker.client.android.exit.ExitService;
import android.content.Context;

/**
 * classname: ExitInfoDialog
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-22	origin.
 * 
 */
public class ExitInfoDialog extends AbstractInfoDialog {

	public static void show(Context ctx, int message) {
		ExitInfoDialog dlg = new ExitInfoDialog(ctx, message);
		dlg.show();
	}
	
	public static void show(Context ctx, int message, Object... args) {
		ExitInfoDialog dlg = new ExitInfoDialog(ctx, message, args);
		dlg.show();
	}
	
	public static void show(Context ctx, String message) {
		ExitInfoDialog dlg = new ExitInfoDialog(ctx, message);
		dlg.show();
	}
	
	private ExitInfoDialog(Context ctx, int message) {
		super(ctx, message);
	}
	
	private ExitInfoDialog(Context ctx, int message, Object... args) {
		super(ctx, ctx.getString(message, args));
	}

	private ExitInfoDialog(Context ctx, String message) {
		super(ctx, message);
	}

	@Override
	public void onOk() {
		ExitService.markAsExit(0);
	}
}
