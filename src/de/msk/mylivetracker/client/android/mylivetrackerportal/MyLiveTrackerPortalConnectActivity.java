package de.msk.mylivetracker.client.android.mylivetrackerportal;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.App.ConfigDsc;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.util.dialog.AbstractInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;
import de.msk.mylivetracker.commons.rpc.ConnectToMyLiveTrackerPortalRequest;
import de.msk.mylivetracker.commons.rpc.ConnectToMyLiveTrackerPortalResponse;
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
public class MyLiveTrackerPortalConnectActivity extends AbstractActivity {	
	
	private static final class OnClickButtonOkListener extends ASafeOnClickListener {
		private MyLiveTrackerPortalConnectActivity activity;
		private EditText etConnectToMyLiveTrackerPortal_PortalUsername;
		private EditText etConnectToMyLiveTrackerPortal_PortalPassword;
		private ProgressDialogHandler progressDialogHandler;
		
		public OnClickButtonOkListener(
			MyLiveTrackerPortalConnectActivity activity,
			EditText etConnectToMyLiveTrackerPortal_PortalUsername,
			EditText etConnectToMyLiveTrackerPortal_PortalPassword) {
			this.activity = activity;
			this.etConnectToMyLiveTrackerPortal_PortalUsername = etConnectToMyLiveTrackerPortal_PortalUsername;
			this.etConnectToMyLiveTrackerPortal_PortalPassword = etConnectToMyLiveTrackerPortal_PortalPassword;		
			this.progressDialogHandler = new ProgressDialogHandler(this.activity);
		}
	
		@Override
		public void onClick() {
			boolean valid = true;
			
			valid = valid && 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdConnectToMyLiveTrackerPortal_PortalUsername, 
					etConnectToMyLiveTrackerPortal_PortalUsername, 
					3, 30, true) &&
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdConnectToMyLiveTrackerPortal_PortalPassword, 
					etConnectToMyLiveTrackerPortal_PortalPassword, 
					3, 30, true);	
							
			if (valid) {				
				ProgressDialogHandler.openProgressDialog(
					this.progressDialogHandler); 
		        
				MyLiveTrackerPortalConnectTask connectToMyLiveTrackerTask = 
					new MyLiveTrackerPortalConnectTask(this.progressDialogHandler);
						
				String hashedPassword = "";
				try {
					MD5 md5 = new MD5();
					md5.Update(
						etConnectToMyLiveTrackerPortal_PortalUsername.getText().toString() + ":" +
						ConfigDsc.getRealm() + ":" +
						etConnectToMyLiveTrackerPortal_PortalPassword.getText().toString(), null);
					hashedPassword = md5.asHex();
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}

				ConnectToMyLiveTrackerPortalRequest request = new ConnectToMyLiveTrackerPortalRequest(
            		MainActivity.getLocale().getLanguage(), 
					App.getDeviceId(), 
					android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL,
					etConnectToMyLiveTrackerPortal_PortalUsername.getText().toString(),
					hashedPassword, DateTime.TIME_ZONE_GERMANY);
	            connectToMyLiveTrackerTask.execute(request);				
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
				EditText etConnectToMyLiveTrackerPortal_PortalUsername = 
					(EditText)this.activity.findViewById(R.id.etConnectToMyLiveTrackerPortal_PortalUsername);
				etConnectToMyLiveTrackerPortal_PortalUsername.setText("");
				etConnectToMyLiveTrackerPortal_PortalUsername.requestFocus();
		        EditText etConnectToMyLiveTrackerPortal_PortalPassword = 
		        	(EditText)this.activity.findViewById(R.id.etConnectToMyLiveTrackerPortal_PortalPassword);
		        etConnectToMyLiveTrackerPortal_PortalPassword.setText("");
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
			ProgressDialogHandler handler, ConnectToMyLiveTrackerPortalResponse response) {
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
            		this.activity.getString(R.string.lbConnectToMyLiveTrackerPortal_ProgressDialog));
            	this.progressDialog.show();
            } else if (code == MESSAGE_CLOSE_PROGRESS_DIALOG){            	
            	this.progressDialog.dismiss();
            	this.progressDialog = null;
            	ConnectToMyLiveTrackerPortalResponse response = (ConnectToMyLiveTrackerPortalResponse)msg.obj;
                String message = this.activity.getString(
                	R.string.txConnectToMyLiveTrackerPortal_InfoConnectingDone);
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
        setContentView(R.layout.connect_to_mylivetracker_portal);
        this.setTitle(R.string.tiConnectToMyLiveTrackerPortal);
        
        EditText etConnectToMyLiveTrackerPortal_PortalUsername = 
        	(EditText)findViewById(R.id.etConnectToMyLiveTrackerPortal_PortalUsername);
        etConnectToMyLiveTrackerPortal_PortalUsername.setText("");
        etConnectToMyLiveTrackerPortal_PortalUsername.requestFocus();
        
        EditText etConnectToMyLiveTrackerPortal_PortalPassword = 
        	(EditText)findViewById(R.id.etConnectToMyLiveTrackerPortal_PortalPassword);
        etConnectToMyLiveTrackerPortal_PortalPassword.setText("");
        
        Button btConnectToMyLiveTrackerPortal_Ok = (Button) findViewById(R.id.btConnectToMyLiveTrackerPortal_Ok);
        Button btConnectToMyLiveTrackerPortal_Cancel = (Button) findViewById(R.id.btConnectToMyLiveTrackerPortal_Cancel);
                    		
        btConnectToMyLiveTrackerPortal_Ok.setOnClickListener(
			new OnClickButtonOkListener(this, 
				etConnectToMyLiveTrackerPortal_PortalUsername,
				etConnectToMyLiveTrackerPortal_PortalPassword));
		
        btConnectToMyLiveTrackerPortal_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
