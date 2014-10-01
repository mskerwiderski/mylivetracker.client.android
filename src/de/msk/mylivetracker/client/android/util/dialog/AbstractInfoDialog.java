package de.msk.mylivetracker.client.android.util.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;

/**
 * classname: AbstractInfoDialog
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractInfoDialog extends AlertDialog {
	public AbstractInfoDialog(Context ctx, int message) {
		super(ctx);
		init(ctx, message, R.string.btOk);
	}	
	
	public AbstractInfoDialog(Context ctx, String message) {
		super(ctx);
		init(ctx, message, R.string.btOk);
	}
	
	public AbstractInfoDialog(Context ctx, 
		int message, int button) {
		super(ctx);
		init(ctx, message, button);
	}
	
	public AbstractInfoDialog(Context ctx, 
		String message, int button) {
		super(ctx);
		init(ctx, message, button);
	}
	
	protected void init(Context ctx, int message, int button) {
		init(ctx, ctx.getString(message), button);
	}
	
	protected void init(Context ctx, String message, int button) {
		this.setMessage(message);		
		this.setButton(ctx.getString(button), 
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();		
				onOk();
			}
		});		
	}	
	
	public abstract void onOk();
}
