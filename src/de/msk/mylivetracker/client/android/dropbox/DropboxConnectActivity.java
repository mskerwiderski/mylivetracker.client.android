package de.msk.mylivetracker.client.android.dropbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * DropboxConnectActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-24 	revised for v1.5.x.
 * 000 	2012-12-09 	initial.
 * 
 */
public class DropboxConnectActivity extends AbstractActivity {	
	
	private static final class ConnectDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public ConnectDialog(Activity activity) {
			super(activity, R.string.txDropboxConnect_QuestionConnect);
			this.activity = activity;			
		}

		@Override
		public void onYes() {
			DropboxUtils.startAuthentication(this.activity);
		}	
	}
	
	private static final class OnClickButtonConnectListener implements OnClickListener {
		private DropboxConnectActivity activity;
		
		public OnClickButtonConnectListener(DropboxConnectActivity activity) {
			this.activity = activity;
		}
	
		@Override
		public void onClick(View v) {
			DropboxPrefs prefs = PrefsRegistry.get(DropboxPrefs.class);
			if (prefs.hasValidAccountAndTokens()) {
				SimpleInfoDialog.show(this.activity, 
					R.string.txDropboxConnect_ConnectionExists);
			} else {
				ConnectDialog dlg = new ConnectDialog(this.activity);
				dlg.show();
			}
		}		
	}
	
	private static final class ReleaseDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public ReleaseDialog(Activity activity) {
			super(activity, R.string.txDropboxConnect_QuestionRelease);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			DropboxUtils.releaseConnection();
			updateConnectionStatus(this.activity);
		}	
	}
	
	private static final class OnClickButtonReleaseListener implements OnClickListener {
		private DropboxConnectActivity activity;
		
		public OnClickButtonReleaseListener(DropboxConnectActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			DropboxPrefs prefs = PrefsRegistry.get(DropboxPrefs.class);
			if (!prefs.hasValidAccountAndTokens()) {
				SimpleInfoDialog.show(this.activity, 
					R.string.txDropboxConnect_NoConnectionExists);
			} else {
				ReleaseDialog dlg = new ReleaseDialog(this.activity);
				dlg.show();
			}
		}		
	}
	
	private static final class OnClickButtonBackListener implements OnClickListener {
		private DropboxConnectActivity activity;
		
		private OnClickButtonBackListener(DropboxConnectActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Integer infoMsgId = 
			DropboxUtils.completeAuthenticationIfStarted();
		if (infoMsgId != null) {
			SimpleInfoDialog.show(this, infoMsgId);
		}
		updateConnectionStatus(this);
	}

	private static void updateConnectionStatus(Activity activity) {
		DropboxPrefs prefs = PrefsRegistry.get(DropboxPrefs.class);
		EditText etDropboxConnect_Status = 
        	(EditText)activity.findViewById(R.id.etDropboxConnect_Status);
		if (prefs.hasValidAccountAndTokens()) {
			etDropboxConnect_Status.setText(
				App.getCtx().getString(
					R.string.txDropboxConnect_StatusConnected,
					prefs.getAccount())); 
		} else {
			etDropboxConnect_Status.setText(
				R.string.txDropboxConnect_StatusNotConnected);
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dropbox_connect);
        this.setTitle(R.string.tiDropboxConnect);
        
        EditText etDropboxConnect_Status = 
        	(EditText)findViewById(R.id.etDropboxConnect_Status);
        etDropboxConnect_Status.setEnabled(false);
        etDropboxConnect_Status.setFocusable(false);
        etDropboxConnect_Status.setClickable(false);
        updateConnectionStatus(this);
        
        Button btDropboxConnect_Connect = (Button)findViewById(R.id.btDropboxConnect_Connect);
        Button btDropboxConnect_Release = (Button)findViewById(R.id.btDropboxConnect_Release);
        Button btDropboxConnect_Back = (Button)findViewById(R.id.btDropboxConnect_Back);
        
        btDropboxConnect_Connect.setOnClickListener(
			new OnClickButtonConnectListener(this));
        btDropboxConnect_Release.setOnClickListener(
			new OnClickButtonReleaseListener(this));
        btDropboxConnect_Back.setOnClickListener(
			new OnClickButtonBackListener(this));
    }
}