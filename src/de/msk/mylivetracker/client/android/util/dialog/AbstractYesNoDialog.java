package de.msk.mylivetracker.client.android.util.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import de.msk.mylivetracker.client.android.R;

/**
 * classname: AbstractYesNoDialog
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractYesNoDialog extends AlertDialog {
		
	protected AbstractYesNoDialog(Context ctx, int question) {
		super(ctx);
		init(ctx, question, R.string.btYes, R.string.btNo);
	}	
	
	protected AbstractYesNoDialog(Context ctx, int question, 
		int yesButton, int noButton) {
		super(ctx);
		init(ctx, question, yesButton, noButton);
	}
	
	protected void init(Context ctx, int question, int yesButton, int noButton) {
		this.setMessage(ctx.getString(question));
		this.setCancelable(true);
		this.setButton(ctx.getString(yesButton), 
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				onYes();				
			}
		});
		this.setButton2(ctx.getString(noButton), 
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {						
				dialog.dismiss();
				onNo();
			}
		});
	}
	
	public abstract void onYes();
	
	public void onNo() {		
	}
}
