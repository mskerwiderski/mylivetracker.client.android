package de.msk.mylivetracker.client.android.mylivetrackerportal;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.App.ConfigDsc;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.PrefsActivity;
import de.msk.mylivetracker.client.android.util.dialog.AbstractInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;
import de.msk.mylivetracker.commons.rpc.RegisterSenderRequest;
import de.msk.mylivetracker.commons.rpc.RegisterSenderResponse;
import de.msk.mylivetracker.commons.util.datetime.DateTime;
import de.msk.mylivetracker.commons.util.md5.MD5;

/**
 * classname: MyLiveTrackerPortalConnectActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class MyLiveTrackerPortalConnectActivity extends PrefsActivity {	
	
	private static final class OnClickButtonOkListener extends ASafeOnClickListener {
		private MyLiveTrackerPortalConnectActivity activity;
		private EditText etMyLiveTrackerPortalConnect_PortalUsername;
		private EditText etMyLiveTrackerPortalConnect_PortalPassword;
		private ProgressDialogHandler progressDialogHandler;
		
		public OnClickButtonOkListener(
			MyLiveTrackerPortalConnectActivity activity,
			EditText etMyLiveTrackerPortalConnect_PortalUsername,
			EditText etMyLiveTrackerPortalConnect_PortalPassword) {
			this.activity = activity;
			this.etMyLiveTrackerPortalConnect_PortalUsername = etMyLiveTrackerPortalConnect_PortalUsername;
			this.etMyLiveTrackerPortalConnect_PortalPassword = etMyLiveTrackerPortalConnect_PortalPassword;		
			this.progressDialogHandler = new ProgressDialogHandler(this.activity);
		}
	
		@Override
		public void onClick() {
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdMyLiveTrackerPortalConnect_PortalUsername, 
					etMyLiveTrackerPortalConnect_PortalUsername, 
					3, 30, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdMyLiveTrackerPortalConnect_PortalPassword, 
					etMyLiveTrackerPortalConnect_PortalPassword, 
					3, 30, true);	
							
			if (valid) {				
				ProgressDialogHandler.openProgressDialog(
					this.progressDialogHandler); 
		        
				MyLiveTrackerPortalConnectTask myLiveTrackerPortalConnectTask = 
					new MyLiveTrackerPortalConnectTask(this.progressDialogHandler);
						
				String hashedPassword = "";
				try {
					MD5 md5 = new MD5();
					md5.Update(
						etMyLiveTrackerPortalConnect_PortalUsername.getText().toString() + ":" +
						ConfigDsc.getRealm() + ":" +
						etMyLiveTrackerPortalConnect_PortalPassword.getText().toString(), null);
					hashedPassword = md5.asHex();
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}

				RegisterSenderRequest request = new RegisterSenderRequest(
            		App.getLocale().getLanguage(), 
					App.getDeviceId(), 
					App.getDeviceModel(),
					etMyLiveTrackerPortalConnect_PortalUsername.getText().toString(),
					hashedPassword, DateTime.TIME_ZONE_GERMANY);
				myLiveTrackerPortalConnectTask.execute(request);				
			}
		}		
	}
	
	protected static class ResultDialog extends AbstractInfoDialog {
		private MyLiveTrackerPortalConnectActivity activity;
		private boolean success;
		public ResultDialog(Context ctx, String message, 
			MyLiveTrackerPortalConnectActivity activity, boolean success) {
			super(ctx, message);
			this.activity = activity;
			this.success = success;
		}

		@Override
		public void onOk() {
			if (success) {
				this.activity.finish();
			} else {
				EditText etMyLiveTrackerPortalConnect_PortalUsername = 
					(EditText)this.activity.findViewById(R.id.etMyLiveTrackerPortalConnect_PortalUsername);
				etMyLiveTrackerPortalConnect_PortalUsername.setText("");
				etMyLiveTrackerPortalConnect_PortalUsername.requestFocus();
		        EditText etMyLiveTrackerPortalConnect_PortalPassword = 
		        	(EditText)this.activity.findViewById(R.id.etMyLiveTrackerPortalConnect_PortalPassword);
		        etMyLiveTrackerPortalConnect_PortalPassword.setText("");
			}
		}		
	}
	
	protected static class ProgressDialogHandler extends Handler {
		private static final int MESSAGE_OPEN_PROGRESS_DIALOG = 1;
		private static final int MESSAGE_CLOSE_PROGRESS_DIALOG = -1;
		
		private MyLiveTrackerPortalConnectActivity activity;
		private ProgressDialog progressDialog;
		
		public static void openProgressDialog(ProgressDialogHandler handler) {
			Message msg = handler.obtainMessage();
	        msg.what = ProgressDialogHandler.MESSAGE_OPEN_PROGRESS_DIALOG;		        
	        handler.sendMessage(msg);
		}
		public static void closeProgressDialog(
			ProgressDialogHandler handler, RegisterSenderResponse response) {
			Message msg = handler.obtainMessage();
	        msg.what = ProgressDialogHandler.MESSAGE_CLOSE_PROGRESS_DIALOG;
	        msg.obj = response;
	        handler.sendMessage(msg);	
		}
		public ProgressDialogHandler(MyLiveTrackerPortalConnectActivity activity) {
			this.activity = activity;			
		}
		public void handleMessage(Message msg) {			
            int code = msg.what;
            if (code == MESSAGE_OPEN_PROGRESS_DIALOG){
            	this.progressDialog = new ProgressDialog(this.activity);
            	this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	this.progressDialog.setCancelable(false);
            	this.progressDialog.setMessage(
            		this.activity.getString(R.string.lbMyLiveTrackerPortalConnect_ProgressDialog));
            	this.progressDialog.show();
            } else if (code == MESSAGE_CLOSE_PROGRESS_DIALOG){            	
            	this.progressDialog.dismiss();
            	this.progressDialog = null;
            	RegisterSenderResponse response = (RegisterSenderResponse)msg.obj;
                String message = this.activity.getString(
                	R.string.txMyLiveTrackerPortalConnect_InfoConnectingDone);
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
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylivetrackerportal_connect);
        this.setTitle(R.string.tiMyLiveTrackerPortalConnect);
        
        EditText etMyLiveTrackerPortalConnect_PortalUsername = 
        	(EditText)findViewById(R.id.etMyLiveTrackerPortalConnect_PortalUsername);
        etMyLiveTrackerPortalConnect_PortalUsername.setText("");
        etMyLiveTrackerPortalConnect_PortalUsername.requestFocus();
        
        EditText etMyLiveTrackerPortalConnect_PortalPassword = 
        	(EditText)findViewById(R.id.etMyLiveTrackerPortalConnect_PortalPassword);
        etMyLiveTrackerPortalConnect_PortalPassword.setText("");
        
        Button btMyLiveTrackerPortalConnect_Ok = (Button) findViewById(R.id.btMyLiveTrackerPortalConnect_Ok);
        Button btMyLiveTrackerPortalConnect_Cancel = (Button) findViewById(R.id.btMyLiveTrackerPortalConnect_Cancel);
                    		
        btMyLiveTrackerPortalConnect_Ok.setOnClickListener(
			new OnClickButtonOkListener(this, 
				etMyLiveTrackerPortalConnect_PortalUsername,
				etMyLiveTrackerPortalConnect_PortalPassword));
		
        btMyLiveTrackerPortalConnect_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
