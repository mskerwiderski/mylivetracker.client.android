package de.msk.mylivetracker.client.android.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.upload.Uploader;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: MessageActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
	
	private static final class OnClickButtonSendListener extends ASafeOnClickListener {
		private MessageActivity activity;
		private EditText etMsgMessage;
		
		private OnClickButtonSendListener(MessageActivity activity,
			EditText etMsgMessage) {
			this.activity = activity;
			this.etMsgMessage = etMsgMessage;
		}
		
		@Override
		public void onClick() {
			String message = etMsgMessage.getText().toString();
			boolean valid = 
				ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdMsg_Message, 
					etMsgMessage, 1, 80, true);
					
			if (valid) {
				if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMedium()) {
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
	
	private static final class OnClickButtonMessagePrefs extends ASafeOnClickListener {
		private MessageActivity activity;
		
		private OnClickButtonMessagePrefs(MessageActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick() {	
			this.activity.startActivity(
				new Intent(this.activity, MessagePrefsActivity.class));
		}		
	}
	
	private static final class OnMessageTemplateItemSelectedListener implements OnItemSelectedListener {
		private EditText etMsgMessage;
		
		public OnMessageTemplateItemSelectedListener(
			EditText etMsgMessage) {
			this.etMsgMessage = etMsgMessage;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
			int position, long rowId) {
			String text = "";
			if (position != 0) {
				MessagePrefs prefs = PrefsRegistry.get(MessagePrefs.class);
				text = prefs.getMessageTemplate(position);
			}
			updateMessageField(etMsgMessage, text);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// noop.
		}
	}
	
	private static void updateMessageField(EditText etMsgMessage, String text) {
		etMsgMessage.setText(text);
		int position = etMsgMessage.length();
		Editable etext = etMsgMessage.getText();
		Selection.setSelection(etext, position);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        this.setTitle(R.string.tiMsg);
        MessagePrefs prefs = PrefsRegistry.get(MessagePrefs.class);
        
        Spinner spMsg_MessageTemplate = (Spinner)
        	findViewById(R.id.spMsg_MessageTemplate);
        ArrayAdapter<String> adapter = 
        	new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
        		prefs.getMessageTemplatesAsComboboxItems());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMsg_MessageTemplate.setAdapter(adapter);
        spMsg_MessageTemplate.setSelection(0);       
        
        EditText etMsgMessage = (EditText)
			this.findViewById(R.id.etMsg_Message);
        updateMessageField(etMsgMessage, "");
        
        Button btMsg_MessagePrefs = (Button)findViewById(R.id.btMsg_MessagePrefs);
        Button btMsg_Send = (Button)findViewById(R.id.btMsg_Send);  
        Button btMsg_Cancel = (Button) findViewById(R.id.btMsg_Cancel);
        
        spMsg_MessageTemplate.setOnItemSelectedListener(
        	new OnMessageTemplateItemSelectedListener(
        		etMsgMessage));
        btMsg_MessagePrefs.setOnClickListener(
			new OnClickButtonMessagePrefs(this));
        btMsg_Send.setOnClickListener(
			new OnClickButtonSendListener(
				this, etMsgMessage));
        btMsg_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
