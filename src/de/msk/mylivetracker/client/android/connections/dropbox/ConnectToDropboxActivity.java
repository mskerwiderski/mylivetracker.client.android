package de.msk.mylivetracker.client.android.connections.dropbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * ConnectToDropboxActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-12-09 initial.
 * 
 */
public class ConnectToDropboxActivity extends AbstractActivity {	
	
	private static final class ConnectDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public ConnectDialog(Activity activity) {
			super(activity, R.string.txConnectToDropbox_QuestionConnect);
			this.activity = activity;			
		}

		@Override
		public void onYes() {
			DropboxUtils.startAuthentication(this.activity);
		}	
	}
	
	private static final class OnClickButtonConnectListener implements OnClickListener {
		private ConnectToDropboxActivity activity;
		
		public OnClickButtonConnectListener(ConnectToDropboxActivity activity) {
			this.activity = activity;
		}
	
		@Override
		public void onClick(View v) {
			Preferences prefs = Preferences.get();
			if (prefs.hasValidDropboxDetails()) {
				SimpleInfoDialog dlg = new SimpleInfoDialog(
					this.activity, R.string.txConnectToDropbox_ConnectionExists);
				dlg.show();
			} else {
				ConnectDialog dlg = new ConnectDialog(this.activity);
				dlg.show();
			}
		}		
	}
	
	private static final class ReleaseDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public ReleaseDialog(Activity activity) {
			super(activity, R.string.txConnectToDropbox_QuestionRelease);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			DropboxUtils.releaseConnection();
			updateConnectionStatus(this.activity);
		}	
	}
	
	private static final class OnClickButtonReleaseListener implements OnClickListener {
		private ConnectToDropboxActivity activity;
		
		public OnClickButtonReleaseListener(ConnectToDropboxActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			Preferences prefs = Preferences.get();
			if (!prefs.hasValidDropboxDetails()) {
				SimpleInfoDialog dlg = new SimpleInfoDialog(
					this.activity, R.string.txConnectToDropbox_NoConnectionExists);
				dlg.show();
			} else {
				ReleaseDialog dlg = new ReleaseDialog(this.activity);
				dlg.show();
			}
		}		
	}
	
	private static final class OnClickButtonBackListener implements OnClickListener {
		private ConnectToDropboxActivity activity;
		
		private OnClickButtonBackListener(ConnectToDropboxActivity activity) {
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
			SimpleInfoDialog dlg = 
				new SimpleInfoDialog(this, infoMsgId);
			dlg.show();
		}
		updateConnectionStatus(this);
	}

	private static void updateConnectionStatus(Activity activity) {
		Preferences prefs = Preferences.get();
		EditText etConnectToDropbox_Status = 
        	(EditText)activity.findViewById(R.id.etConnectToDropbox_Status);
		if (prefs.hasValidDropboxDetails()) {
			etConnectToDropbox_Status.setText(
				App.getCtx().getString(
					R.string.txConnectToDropbox_StatusConnected,
					prefs.getDropboxAccount())); 
		} else {
			etConnectToDropbox_Status.setText(
				R.string.txConnectToDropbox_StatusNotConnected);
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_dropbox);
        this.setTitle(R.string.tiConnectToDropbox);
        
        EditText etConnectToDropbox_Status = 
        	(EditText)findViewById(R.id.etConnectToDropbox_Status);
        etConnectToDropbox_Status.setEnabled(false);
        etConnectToDropbox_Status.setFocusable(false);
        etConnectToDropbox_Status.setClickable(false);
        updateConnectionStatus(this);
        
        Button btConnectToDropbox_Connect = (Button)findViewById(R.id.btConnectToDropbox_Connect);
        Button btConnectToDropbox_Release = (Button)findViewById(R.id.btConnectToDropbox_Release);
        Button btConnectToDropbox_Back = (Button)findViewById(R.id.btConnectToDropbox_Back);
        
        btConnectToDropbox_Connect.setOnClickListener(
			new OnClickButtonConnectListener(this));
        btConnectToDropbox_Release.setOnClickListener(
			new OnClickButtonReleaseListener(this));
        btConnectToDropbox_Back.setOnClickListener(
			new OnClickButtonBackListener(this));
    }
}
