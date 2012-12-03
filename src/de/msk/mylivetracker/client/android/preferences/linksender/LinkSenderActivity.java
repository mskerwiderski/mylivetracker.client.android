package de.msk.mylivetracker.client.android.preferences.linksender;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.App.ConfigDsc;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.AbstractInfoDialog;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;
import de.msk.mylivetracker.commons.rpc.LinkSenderRequest;
import de.msk.mylivetracker.commons.rpc.LinkSenderResponse;
import de.msk.mylivetracker.commons.util.datetime.DateTime;
import de.msk.mylivetracker.commons.util.md5.MD5;

/**
 * LinkSenderActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-16 initial.
 * 
 */
public class LinkSenderActivity extends AbstractActivity {	
	
	private static final class OnClickButtonOkListener implements OnClickListener {
		private LinkSenderActivity activity;
		private EditText etLinkSender_PortalUsername;
		private EditText etLinkSender_PortalPassword;
		private ProgressDialogHandler progressDialogHandler;
		
		public OnClickButtonOkListener(
			LinkSenderActivity activity,
			Preferences preferences, 
			EditText etLinkSender_PortalUsername,
			EditText etLinkSender_PortalPassword) {
			this.activity = activity;
			this.etLinkSender_PortalUsername = etLinkSender_PortalUsername;
			this.etLinkSender_PortalPassword = etLinkSender_PortalPassword;		
			this.progressDialogHandler = new ProgressDialogHandler(this.activity);
		}
	
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdLinkSender_PortalUsername, 
					etLinkSender_PortalUsername, 
					3, 30, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdLinkSender_PortalPassword, 
					etLinkSender_PortalPassword, 
					3, 30, true);	
							
			if (valid) {				
				ProgressDialogHandler.openProgressDialog(
					this.progressDialogHandler); 
		        
				LinkSenderTask linkSenderTask = 
					new LinkSenderTask(this.progressDialogHandler);
						
				String hashedPassword = "";
				try {
					MD5 md5 = new MD5();
					md5.Update(
						etLinkSender_PortalUsername.getText().toString() + ":" +
						ConfigDsc.getRealm() + ":" +
						etLinkSender_PortalPassword.getText().toString(), null);
					hashedPassword = md5.asHex();
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}

				String imei = 
					((TelephonyManager)MainActivity.get().getSystemService(
					Context.TELEPHONY_SERVICE)).getDeviceId();
	            LinkSenderRequest request = new LinkSenderRequest(
            		MainActivity.getLocale().getLanguage(), 
					imei, 
					android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL,
					etLinkSender_PortalUsername.getText().toString(),
					hashedPassword, DateTime.TIME_ZONE_GERMANY);
	            linkSenderTask.execute(request);				
			}
		}		
	}
	
	protected static class ResultDialog extends AbstractInfoDialog {
		private LinkSenderActivity activity;
		private boolean success;
		public ResultDialog(Context ctx, String message, 
			LinkSenderActivity activity, boolean success) {
			super(ctx, message);
			this.activity = activity;
			this.success = success;
		}

		@Override
		public void onOk() {
			if (success) {
				this.activity.finish();
			} else {
				EditText etLinkSender_PortalUsername = 
					(EditText)this.activity.findViewById(R.id.etLinkSender_PortalUsername);
		        etLinkSender_PortalUsername.setText("");
		        EditText etLinkSender_PortalPassword = 
		        	(EditText)this.activity.findViewById(R.id.etLinkSender_PortalPassword);
		        etLinkSender_PortalPassword.setText("");
		        etLinkSender_PortalUsername.requestFocus();
			}
		}		
	}
	
	protected static class ProgressDialogHandler extends Handler {
		private static final int MESSAGE_OPEN_PROGRESS_DIALOG = 1;
		private static final int MESSAGE_CLOSE_PROGRESS_DIALOG = -1;
		
		private LinkSenderActivity activity;
		private ProgressDialog progressDialog;
		
		public static void openProgressDialog(ProgressDialogHandler handler) {
			Message msg = handler.obtainMessage();
	        msg.what = ProgressDialogHandler.MESSAGE_OPEN_PROGRESS_DIALOG;		        
	        handler.sendMessage(msg);
		}
		public static void closeProgressDialog(
			ProgressDialogHandler handler, LinkSenderResponse response) {
			Message msg = handler.obtainMessage();
	        msg.what = ProgressDialogHandler.MESSAGE_CLOSE_PROGRESS_DIALOG;
	        msg.obj = response;
	        handler.sendMessage(msg);	
		}
		public ProgressDialogHandler(LinkSenderActivity activity) {
			this.activity = activity;			
		}
		public void handleMessage(Message msg) {			
            int code = msg.what;
            if (code == MESSAGE_OPEN_PROGRESS_DIALOG){
            	this.progressDialog = new ProgressDialog(this.activity);
            	this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	this.progressDialog.setCancelable(false);
            	this.progressDialog.setMessage(
            		this.activity.getString(R.string.lbLinkSender_ProgressDialog));
            	this.progressDialog.show();
            } else if (code == MESSAGE_CLOSE_PROGRESS_DIALOG){            	
            	this.progressDialog.dismiss();
            	this.progressDialog = null;
                LinkSenderResponse response = (LinkSenderResponse)msg.obj;
                String message = this.activity.getString(
                	R.string.txLinkSender_InfoLinkSenderDone);
                if (!response.getResultCode().isSuccess()) {
	                message = "[" + response.getResultCode().getCode() + "] " + 
    					response.getResultMessage();
	                if (!StringUtils.isEmpty(response.getAddInfo())) {
	                	message += " (" +
        					response.getAddInfo() + ")";
	                }
                }                 
                ResultDialog resultDlg = new ResultDialog(
        			this.activity, message, 
        			this.activity, response.getResultCode().isSuccess());
                resultDlg.show();    			
            }                      
        }
    };
    
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private LinkSenderActivity activity;
		
		private OnClickButtonCancelListener(LinkSenderActivity activity) {
			this.activity = activity;
		}
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_sender);
     
        this.setTitle(R.string.tiLinkSender);
        
        Preferences prefs = Preferences.get();
        
        EditText etLinkSender_PortalUsername = (EditText) findViewById(R.id.etLinkSender_PortalUsername);
        etLinkSender_PortalUsername.setText("");
        EditText etLinkSender_PortalPassword = (EditText) findViewById(R.id.etLinkSender_PortalPassword);
        etLinkSender_PortalPassword.setText("");
        etLinkSender_PortalUsername.requestFocus();
        
        Button btLinkSender_Ok = (Button) findViewById(R.id.btLinkSender_Ok);
        Button btLinkSender_Cancel = (Button) findViewById(R.id.btLinkSender_Cancel);
                    		
        btLinkSender_Ok.setOnClickListener(
			new OnClickButtonOkListener(this, prefs,
				etLinkSender_PortalUsername,
				etLinkSender_PortalPassword));
		
        btLinkSender_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
