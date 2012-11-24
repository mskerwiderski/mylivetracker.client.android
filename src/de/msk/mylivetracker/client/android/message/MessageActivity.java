package de.msk.mylivetracker.client.android.message;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.upload.Uploader;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * MessageActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-11 initial.
 * 
 */
public class MessageActivity extends AbstractActivity {	
	
	private static final class SendMessageDialog extends AbstractYesNoDialog {

		private MessageActivity activity;
		private String message;
				
		public SendMessageDialog(MessageActivity activity, String message) {
			super(activity, R.string.txMsg_QuestionSendMessage);
			this.activity = activity;
			this.message = message;
		}

		@Override
		public void onYes() {			
			sendMessage(this.activity, this.message);					
		}	
	}
	
	private static final class OnClickButtonSendListener implements OnClickListener {
		private MessageActivity activity;
		
		private OnClickButtonSendListener(MessageActivity activity) {
			this.activity = activity;					
		}
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View view) {
			EditText etMsgMessage = (EditText)this.activity.findViewById(R.id.etMsg_Message);
			String message = etMsgMessage.getText().toString();
			boolean valid = 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdMsg_Message, 
					etMsgMessage, 1, 80, true);
					
			if (valid) {
				if (Preferences.get().getConfirmLevel().isMedium()) {
					SendMessageDialog dlg = new SendMessageDialog(this.activity, message);
					dlg.show();
				} else {
					sendMessage(this.activity, message);
				}
			}
		}		
	}
	
	private static void sendMessage(MessageActivity activity, String message) {
		MessageInfo.update(message);					
		Uploader.uploadOneTime();
		Toast.makeText(activity.getApplicationContext(), 
			activity.getString(R.string.txMsg_InfoSendMessageDone),
			Toast.LENGTH_SHORT).show();	
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private MessageActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(MessageActivity activity) {
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
        setContentView(R.layout.message);
        
        this.setTitle(R.string.tiMsg);
        
        Button btMsg_Send = (Button)findViewById(R.id.btMsg_Send);  
        Button btMsg_Cancel = (Button) findViewById(R.id.btMsg_Cancel);
        
        btMsg_Send.setOnClickListener(
			new OnClickButtonSendListener(this));
        btMsg_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
