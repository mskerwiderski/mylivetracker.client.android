package de.msk.mylivetracker.client.android.message;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
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
		
		public OnClickButtonSaveListener(
			MessagePrefsActivity activity,
			EditText[] etMsgPrefs_MessageTemplates) {
			this.activity = activity;
			this.etMsgPrefs_MessageTemplates = etMsgPrefs_MessageTemplates;
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
			if (valid) {
				MessagePrefs prefs = PrefsRegistry.get(MessagePrefs.class);
				for (int idx = 0; idx < etMsgPrefs_MessageTemplates.length; idx++) {
					prefs.setMessageTemplate(idx+1, 
						etMsgPrefs_MessageTemplates[idx].getText().toString());
				}
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
        
        Button btMsgPrefs_Save = (Button) findViewById(R.id.btMsgPrefs_Save);
        Button btMsgPrefs_Cancel = (Button) findViewById(R.id.btMsgPrefs_Cancel);
                
        btMsgPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this,
				etMsgPrefs_MessageTemplates));
		
        btMsgPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
