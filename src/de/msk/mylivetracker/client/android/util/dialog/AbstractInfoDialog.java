package de.msk.mylivetracker.client.android.util.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import de.msk.mylivetracker.client.android.app.pro.R;

/**
 * AbstractInfoDialog.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-17
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
