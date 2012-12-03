package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PinCodeQueryActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-11-16 initial.
 * 
 */
public class PrefsPinCodeQueryActivity extends AbstractActivity {	
	
	private static final class OnClickButtonOkListener implements OnClickListener {
		private PrefsPinCodeQueryActivity activity;
		private Preferences preferences;
		private CheckBox cbPrefsPinCodeQuery_Enable;
		private EditText etPrefsPinCodeQuery_PinCode;
		private EditText etPrefsPinCodeQuery_PinCodeReenter;
		
		public OnClickButtonOkListener(
			PrefsPinCodeQueryActivity activity,
			Preferences preferences,
			CheckBox cbPrefsPinCodeQuery_Enable,
			EditText etPrefsPinCodeQuery_PinCode,
			EditText etPrefsPinCodeQuery_PinCodeReenter) {
			this.activity = activity;
			this.preferences = preferences;
			this.cbPrefsPinCodeQuery_Enable = cbPrefsPinCodeQuery_Enable;
			this.etPrefsPinCodeQuery_PinCode = etPrefsPinCodeQuery_PinCode;
			this.etPrefsPinCodeQuery_PinCodeReenter = etPrefsPinCodeQuery_PinCodeReenter;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
			if (cbPrefsPinCodeQuery_Enable.isChecked()) {
				valid = valid && ValidatorUtils.validatePinCodeEqualsPinCodeReentered(
					this.activity, 
					etPrefsPinCodeQuery_PinCode,
					etPrefsPinCodeQuery_PinCodeReenter);
				valid = valid && ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPrefsPinCodeQuery_PinCode, 
					etPrefsPinCodeQuery_PinCode, 4, 6, true);
			}
			if (!valid) {
				etPrefsPinCodeQuery_PinCode.setText("");
				etPrefsPinCodeQuery_PinCodeReenter.setText("");
				etPrefsPinCodeQuery_PinCode.requestFocus();
			} else {
				preferences.setPinCodeQueryEnabled(cbPrefsPinCodeQuery_Enable.isChecked());
				if (cbPrefsPinCodeQuery_Enable.isChecked()) {
					preferences.setPinCode(etPrefsPinCodeQuery_PinCode.getText().toString());
				}
				Preferences.save();
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsPinCodeQueryActivity activity;
		
		private OnClickButtonCancelListener(PrefsPinCodeQueryActivity activity) {
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
        setContentView(R.layout.preferences_pin_code_query);
     
        this.setTitle(R.string.tiPrefsPinCodeQuery);
        
        Preferences prefs = Preferences.get();
        
        CheckBox cbPrefsPinCodeQuery_Enable = (CheckBox)findViewById(R.id.cbPrefsPinCodeQuery_Enable);
        cbPrefsPinCodeQuery_Enable.setChecked(prefs.isPinCodeQueryEnabled());
        EditText etPrefsPinCodeQuery_PinCode = (EditText) findViewById(R.id.etPrefsPinCodeQuery_PinCode);
        etPrefsPinCodeQuery_PinCode.setText("");
        EditText etPrefsPinCodeQuery_PinCodeReenter = (EditText) findViewById(R.id.etPrefsPinCodeQuery_PinCodeReenter);
        etPrefsPinCodeQuery_PinCodeReenter.setText("");
        etPrefsPinCodeQuery_PinCode.requestFocus();
        
        Button btPrefsPinCodeQuery_Ok = (Button) findViewById(R.id.btPrefsPinCodeQuery_Ok);
        Button btPrefsPinCodeQuery_Cancel = (Button) findViewById(R.id.btPrefsPinCodeQuery_Cancel);
                    		
        btPrefsPinCodeQuery_Ok.setOnClickListener(
			new OnClickButtonOkListener(this, prefs,
				cbPrefsPinCodeQuery_Enable,
				etPrefsPinCodeQuery_PinCode,
				etPrefsPinCodeQuery_PinCodeReenter));
		
        btPrefsPinCodeQuery_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
