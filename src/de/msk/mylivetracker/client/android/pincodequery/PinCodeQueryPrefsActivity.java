package de.msk.mylivetracker.client.android.pincodequery;

import org.apache.commons.lang.StringUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PinCodeQueryPrefsActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-23 	revised for v1.5.x.
 * 000 	2012-11-16 	initial.
 * 
 */
public class PinCodeQueryPrefsActivity extends AbstractActivity {	
	
	private static final class OnClickButtonOkListener implements OnClickListener {
		private PinCodeQueryPrefsActivity activity;
		private CheckBox cbPrefsPinCodeQuery_Enable;
		private EditText etPrefsPinCodeQuery_PinCode;
		private EditText etPrefsPinCodeQuery_PinCodeReenter;
		
		public OnClickButtonOkListener(
			PinCodeQueryPrefsActivity activity,
			CheckBox cbPrefsPinCodeQuery_Enable,
			EditText etPrefsPinCodeQuery_PinCode,
			EditText etPrefsPinCodeQuery_PinCodeReenter) {
			this.activity = activity;
			this.cbPrefsPinCodeQuery_Enable = cbPrefsPinCodeQuery_Enable;
			this.etPrefsPinCodeQuery_PinCode = etPrefsPinCodeQuery_PinCode;
			this.etPrefsPinCodeQuery_PinCodeReenter = etPrefsPinCodeQuery_PinCodeReenter;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
			if (cbPrefsPinCodeQuery_Enable.isChecked()) {
				valid = StringUtils.equals(
					etPrefsPinCodeQuery_PinCode.getText().toString(), 
					etPrefsPinCodeQuery_PinCodeReenter.getText().toString());
				if (!valid) {
					SimpleInfoDialog.show(this.activity, 
						R.string.vdPinCodeQueryPrefs_ReenteredPinCodeNotEqualToPinCode);
				}
				valid = valid && ValidatorUtils.validateEditTextString(
					this.activity, 
					R.string.fdPinCodeQueryPrefs_PinCode, 
					etPrefsPinCodeQuery_PinCode, 4, 6, true);
			}
			if (!valid) {
				etPrefsPinCodeQuery_PinCode.setText("");
				etPrefsPinCodeQuery_PinCodeReenter.setText("");
				etPrefsPinCodeQuery_PinCode.requestFocus();
			} else {
				PinCodeQueryPrefs prefs = PrefsRegistry.get(PinCodeQueryPrefs.class);
				prefs.setPinCodeQueryEnabled(cbPrefsPinCodeQuery_Enable.isChecked());
				if (cbPrefsPinCodeQuery_Enable.isChecked()) {
					prefs.setPinCode(etPrefsPinCodeQuery_PinCode.getText().toString());
				}
				PrefsRegistry.save(PinCodeQueryPrefs.class);
				this.activity.finish();
			}
		}	
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PinCodeQueryPrefsActivity activity;
		
		private OnClickButtonCancelListener(PinCodeQueryPrefsActivity activity) {
			this.activity = activity;
		}
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_code_query_prefs);
     
        this.setTitle(R.string.tiPinCodeQueryPrefs);
        
        PinCodeQueryPrefs prefs = PrefsRegistry.get(PinCodeQueryPrefs.class);
        
        CheckBox cbPinCodeQueryPrefs_Enable = 
        	(CheckBox)findViewById(R.id.cbPinCodeQueryPrefs_Enable);
        cbPinCodeQueryPrefs_Enable.setChecked(prefs.isPinCodeQueryEnabled());
        EditText etPinCodeQueryPrefs_PinCode = 
        	(EditText)findViewById(R.id.etPinCodeQueryPrefs_PinCode);
        etPinCodeQueryPrefs_PinCode.setText("");
        EditText etPinCodeQueryPrefs_PinCodeReenter = 
        	(EditText)findViewById(R.id.etPinCodeQueryPrefs_PinCodeReenter);
        etPinCodeQueryPrefs_PinCodeReenter.setText("");
        etPinCodeQueryPrefs_PinCode.requestFocus();
        
        Button btPinCodeQueryPrefs_Ok = 
        	(Button)findViewById(R.id.btPinCodeQueryPrefs_Ok);
        Button btPinCodeQueryPrefs_Cancel = 
        	(Button)findViewById(R.id.btPinCodeQueryPrefs_Cancel);
                    		
        btPinCodeQueryPrefs_Ok.setOnClickListener(
			new OnClickButtonOkListener(this,
				cbPinCodeQueryPrefs_Enable,
				etPinCodeQueryPrefs_PinCode,
				etPinCodeQueryPrefs_PinCodeReenter));
		
        btPinCodeQueryPrefs_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}