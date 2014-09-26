package de.msk.mylivetracker.client.android.pincodequery;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.PrefsActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: PinCodeQueryPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class PinCodeQueryPrefsActivity extends PrefsActivity {	
	
	private static final class OnClickButtonOkListener extends ASafeOnClickListener {
		private PinCodeQueryPrefsActivity activity;
		private CheckBox cbPrefsPinCodeQuery_Enable;
		private CheckBox cbPrefsPinCodeQuery_ProtectSettingsOnly;
		private EditText etPrefsPinCodeQuery_PinCode;
		private EditText etPrefsPinCodeQuery_PinCodeReenter;
		
		public OnClickButtonOkListener(
			PinCodeQueryPrefsActivity activity,
			CheckBox cbPrefsPinCodeQuery_Enable,
			CheckBox cbPrefsPinCodeQuery_ProtectSettingsOnly,
			EditText etPrefsPinCodeQuery_PinCode,
			EditText etPrefsPinCodeQuery_PinCodeReenter) {
			this.activity = activity;
			this.cbPrefsPinCodeQuery_Enable = cbPrefsPinCodeQuery_Enable;
			this.cbPrefsPinCodeQuery_ProtectSettingsOnly = cbPrefsPinCodeQuery_ProtectSettingsOnly;
			this.etPrefsPinCodeQuery_PinCode = etPrefsPinCodeQuery_PinCode;
			this.etPrefsPinCodeQuery_PinCodeReenter = etPrefsPinCodeQuery_PinCodeReenter;
		}

		@Override
		public void onClick() {
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
				prefs.setProtectSettingsOnly(cbPrefsPinCodeQuery_ProtectSettingsOnly.isChecked());
				if (cbPrefsPinCodeQuery_Enable.isChecked()) {
					prefs.setPinCode(etPrefsPinCodeQuery_PinCode.getText().toString());
				}
				PrefsRegistry.save(PinCodeQueryPrefs.class);
				this.activity.finish();
			}
		}	
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_code_query_prefs);
     
        this.setTitle(R.string.tiPinCodeQueryPrefs);
        
        PinCodeQueryPrefs prefs = PrefsRegistry.get(PinCodeQueryPrefs.class);
        
        TextView tvPinCodeQueryPrefs_Info = 
        	(TextView)findViewById(R.id.tvPinCodeQueryPrefs_Info);
        tvPinCodeQueryPrefs_Info.setText(App.getCtx().getString(
        	R.string.lbPinCodeQueryPrefs_Info, App.getAppName()));
        
        CheckBox cbPinCodeQueryPrefs_Enable = 
        	(CheckBox)findViewById(R.id.cbPinCodeQueryPrefs_Enable);
        cbPinCodeQueryPrefs_Enable.setChecked(prefs.isPinCodeQueryEnabled());
        CheckBox cbPinCodeQueryPrefs_ProtectSettingsOnly = 
        	(CheckBox)findViewById(R.id.cbPinCodeQueryPrefs_ProtectSettings);
        cbPinCodeQueryPrefs_ProtectSettingsOnly.setChecked(prefs.isProtectSettingsOnly());
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
				cbPinCodeQueryPrefs_ProtectSettingsOnly,
				etPinCodeQueryPrefs_PinCode,
				etPinCodeQueryPrefs_PinCodeReenter));
		
        btPinCodeQueryPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
