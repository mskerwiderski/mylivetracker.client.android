package de.msk.mylivetracker.client.android.message;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.Uploader;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.sms.SmsSendUtils;
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
	
	@Override
	protected boolean isPrefsActivity() {
		return false;
	}
	
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
			boolean valid = true;
			if (valid) {
				if (!minimumOneChannelIsSupportedAndEnabled()) {
					SimpleInfoDialog.show(this.activity, R.string.txMsg_SendingMessagesNotSupported);
					valid = false;
				}
			}
			if (valid) {
				valid = ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdMsg_Message, 
					etMsgMessage, 1, 80, true);
			}
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
	
	public static boolean minimumOneChannelIsSupportedAndEnabled() {
		MessagePrefs messagePrefs = PrefsRegistry.get(MessagePrefs.class);
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
		boolean smsChannelActive = App.smsSupported() &&
			messagePrefs.isSendMessageModeAsSmsEnabled();
		boolean protocolChannelActive = 
			protocolPrefs.getTransferProtocol().supportsSendMessage() && 
			messagePrefs.isSendMessageModeToServerEnabled();
		return smsChannelActive || protocolChannelActive;
	}
	
	public static void sendMessage(String message) {
		if (StringUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message must not be empty");
		}
		MessagePrefs messagePrefs = PrefsRegistry.get(MessagePrefs.class);
		if (messagePrefs.isSendMessageModeToServerEnabled()) {
			MessageInfo.update(message);	
			if (!TrackStatus.get().trackIsRunning()) {
				Uploader.uploadOneTime();
			}
		}
		sendMessageAsSms(message);
	}
	
	public static void sendMessageAsSms(String message) {
		if (StringUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message must not be empty");
		}
		MessagePrefs messagePrefs = PrefsRegistry.get(MessagePrefs.class);
		if (App.smsSupported() &&
			messagePrefs.isSendMessageModeAsSmsEnabled()) {
			String smsMessage = App.getAppNameAbbr() + 
				":[MSG]:" + message + ":" + 
				ResponseCreator.getGoogleLatLonUrl(LocationInfo.get());
			SmsSendUtils.sendSms(
				messagePrefs.getSmsReceiver(),
				smsMessage);
		}
	}
	
	private static void sendMessage(Activity activity, String message) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null");
		}
		if (StringUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message must not be empty");
		}
		sendMessage(message);
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
	protected void onResume() {
		super.onResume();
		this.updateSendMessageModeStati();
	}

	private void updateSendMessageModeStati() {
		MessagePrefs messagePrefs = PrefsRegistry.get(MessagePrefs.class);
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
		TextView tvMsg_StatusMessageToServer = (TextView)findViewById(R.id.tvMsg_StatusMessageToServer);
        TextView tvMsg_StatusMessageAsSms = (TextView)findViewById(R.id.tvMsg_StatusMessageAsSms);
        if (messagePrefs.isSendMessageModeToServerEnabled() &&
    		protocolPrefs.getTransferProtocol().supportsSendMessage()) {
        	tvMsg_StatusMessageToServer.setText(R.string.lbMsg_StatusEnabled);
        	tvMsg_StatusMessageToServer.setTextColor(
        		this.getResources().getColor(R.color.colorBlack));
        	tvMsg_StatusMessageToServer.setBackgroundColor(
        		this.getResources().getColor(R.color.colorGreen));
        } else if (!messagePrefs.isSendMessageModeToServerEnabled() &&
    		protocolPrefs.getTransferProtocol().supportsSendMessage()) {
        	tvMsg_StatusMessageToServer.setText(R.string.lbMsg_StatusDisabled);
        	tvMsg_StatusMessageToServer.setTextColor(
        		this.getResources().getColor(R.color.colorWhite));
        	tvMsg_StatusMessageToServer.setBackgroundColor(
        		this.getResources().getColor(R.color.colorRed));
        } else {
        	tvMsg_StatusMessageToServer.setText(
        		R.string.lbMsg_StatusToServerNotSupportedByProtocol);
        	tvMsg_StatusMessageToServer.setTextColor(
        		this.getResources().getColor(R.color.colorWhite));
        	tvMsg_StatusMessageToServer.setBackgroundColor(
        		this.getResources().getColor(R.color.colorRed));
        }
        if (messagePrefs.isSendMessageModeAsSmsEnabled() &&
    		App.smsSupported()) {
        	tvMsg_StatusMessageAsSms.setText(R.string.lbMsg_StatusEnabled);
        	tvMsg_StatusMessageAsSms.setTextColor(
        		this.getResources().getColor(R.color.colorBlack));
        	tvMsg_StatusMessageAsSms.setBackgroundColor(
        		this.getResources().getColor(R.color.colorGreen));
        } else if (!messagePrefs.isSendMessageModeAsSmsEnabled() &&
        	App.smsSupported()) {
        	tvMsg_StatusMessageAsSms.setText(R.string.lbMsg_StatusDisabled);
        	tvMsg_StatusMessageAsSms.setTextColor(
        		this.getResources().getColor(R.color.colorWhite));
        	tvMsg_StatusMessageAsSms.setBackgroundColor(
        		this.getResources().getColor(R.color.colorRed));
        } else {
        	tvMsg_StatusMessageAsSms.setText(
        		R.string.lbMsg_StatusAsSmsNotSupported);
        	tvMsg_StatusMessageAsSms.setTextColor(
        		this.getResources().getColor(R.color.colorWhite));
        	tvMsg_StatusMessageAsSms.setBackgroundColor(
        		this.getResources().getColor(R.color.colorRed));
        }
        
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        this.setTitle(R.string.tiMsg);
        MessagePrefs messagePrefs = PrefsRegistry.get(MessagePrefs.class);
        
        Spinner spMsg_MessageTemplate = (Spinner)
        	findViewById(R.id.spMsg_MessageTemplate);
        ArrayAdapter<String> adapter = 
        	new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
        		messagePrefs.getMessageTemplatesAsComboboxItems());
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
