package de.msk.mylivetracker.client.android.message;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.message.MessagePrefs.SendMessageMode;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: MessagePrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-05	revised for v1.5.x.
 * 
 */
public class MessagePrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private MessagePrefsActivity activity;
		private EditText[] etMsgPrefs_MessageTemplates;
		private Spinner spMsgPrefs_SendMessageMode;
		private EditText etMsgPrefs_Receiver;
		
		public OnClickButtonSaveListener(
			MessagePrefsActivity activity,
			EditText[] etMsgPrefs_MessageTemplates,
			Spinner spMsgPrefs_SendMessageMode,
			EditText etMsgPrefs_Receiver) {
			this.activity = activity;
			this.etMsgPrefs_MessageTemplates = etMsgPrefs_MessageTemplates;
			this.spMsgPrefs_SendMessageMode = spMsgPrefs_SendMessageMode;
			this.etMsgPrefs_Receiver = etMsgPrefs_Receiver;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			for (int idx = 0; valid && (idx < etMsgPrefs_MessageTemplates.length); idx++) {
				valid = valid && 
					ValidatorUtils.validateEditTextString(
						this.activity, 
						R.string.fdMsg_Message, 
						etMsgPrefs_MessageTemplates[idx], 1, 40, true);
			}
			SendMessageMode selSendMessageMode = SendMessageMode.values()
				[spMsgPrefs_SendMessageMode.getSelectedItemPosition()];
			if (valid && selSendMessageMode.isModeAsSmsEnabled()) {
				valid = ValidatorUtils.validateIfPhoneNumber(
					this.activity, R.string.fdMsgPrefs_Receiver,
					this.etMsgPrefs_Receiver, true);
			}
			if (valid) {
				MessagePrefs prefs = PrefsRegistry.get(MessagePrefs.class);
				for (int idx = 0; idx < etMsgPrefs_MessageTemplates.length; idx++) {
					prefs.setMessageTemplate(idx+1, 
						etMsgPrefs_MessageTemplates[idx].getText().toString());
				}
				prefs.setSendMessageMode(SendMessageMode.values()
					[spMsgPrefs_SendMessageMode.getSelectedItemPosition()]);
				prefs.setSmsReceiver(this.etMsgPrefs_Receiver.getText().toString());
				PrefsRegistry.save(MessagePrefs.class);
				this.activity.finish();
			}
		}		
	}
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_prefs);
     
        this.setTitle(R.string.tiMsgPrefs);
        
        MessagePrefs prefs = PrefsRegistry.get(MessagePrefs.class);

        EditText[] etMsgPrefs_MessageTemplates =
        	new EditText[] {
        		(EditText)findViewById(R.id.etMsgPrefs_MessageTemplate1),
        		(EditText)findViewById(R.id.etMsgPrefs_MessageTemplate2),
        		(EditText)findViewById(R.id.etMsgPrefs_MessageTemplate3),
        		(EditText)findViewById(R.id.etMsgPrefs_MessageTemplate4),
        		(EditText)findViewById(R.id.etMsgPrefs_MessageTemplate5),
        	};
        for (int idx = 0; idx < etMsgPrefs_MessageTemplates.length; idx++) {
			etMsgPrefs_MessageTemplates[idx].
				setText(prefs.getMessageTemplate(idx+1));
		}
        
        Spinner spMsgPrefs_SendMessageMode = (Spinner)
    		findViewById(R.id.spMsgPrefs_SendMessageMode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.sendMessageModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMsgPrefs_SendMessageMode.setAdapter(adapter);
        spMsgPrefs_SendMessageMode.setSelection(prefs.getSendMessageMode().ordinal());
        
        EditText etMsgPrefs_Receiver = (EditText)
        	findViewById(R.id.etMsgPrefs_Receiver);
        etMsgPrefs_Receiver.setText(prefs.getSmsReceiver());
        
        Button btMsgPrefs_Save = (Button) findViewById(R.id.btMsgPrefs_Save);
        Button btMsgPrefs_Cancel = (Button) findViewById(R.id.btMsgPrefs_Cancel);
                
        btMsgPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this,
				etMsgPrefs_MessageTemplates,
				spMsgPrefs_SendMessageMode,
				etMsgPrefs_Receiver));
		
        btMsgPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
