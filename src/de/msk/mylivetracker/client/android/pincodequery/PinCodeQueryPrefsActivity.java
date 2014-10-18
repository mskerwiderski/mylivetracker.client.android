package de.msk.mylivetracker.client.android.pincodequery;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.PrefsActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs.PinCodeQueryMode;
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
		private Spinner spPinCodeQueryPrefs_Mode;
		private EditText etPrefsPinCodeQuery_PinCode;
		private EditText etPrefsPinCodeQuery_PinCodeReenter;
		
		public OnClickButtonOkListener(
			PinCodeQueryPrefsActivity activity,
			Spinner spPinCodeQueryPrefs_Mode,
			EditText etPrefsPinCodeQuery_PinCode,
			EditText etPrefsPinCodeQuery_PinCodeReenter) {
			this.activity = activity;
			this.spPinCodeQueryPrefs_Mode = spPinCodeQueryPrefs_Mode;
			this.etPrefsPinCodeQuery_PinCode = etPrefsPinCodeQuery_PinCode;
			this.etPrefsPinCodeQuery_PinCodeReenter = etPrefsPinCodeQuery_PinCodeReenter;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			PinCodeQueryMode pinCodeQueryMode = (PinCodeQueryMode.values()
				[spPinCodeQueryPrefs_Mode.getSelectedItemPosition()]);
			if (!pinCodeQueryMode.equals(PinCodeQueryMode.Disabled)) {
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
				prefs.setPinCodeQueryMode(pinCodeQueryMode);
				if (!pinCodeQueryMode.equals(PinCodeQueryMode.Disabled)) {
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
        
        Spinner spPinCodeQueryPrefs_Mode = (Spinner) findViewById(R.id.spPinCodeQueryPrefs_Mode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.pinCodeQueryModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPinCodeQueryPrefs_Mode.setAdapter(adapter);
        spPinCodeQueryPrefs_Mode.setSelection(prefs.getPinCodeQueryMode().ordinal());
        
        EditText etPinCodeQueryPrefs_PinCode = 
        	(EditText)findViewById(R.id.etPinCodeQueryPrefs_PinCode);
        etPinCodeQueryPrefs_PinCode.setText(prefs.getPinCode());
        EditText etPinCodeQueryPrefs_PinCodeReenter = 
        	(EditText)findViewById(R.id.etPinCodeQueryPrefs_PinCodeReenter);
        etPinCodeQueryPrefs_PinCodeReenter.setText(prefs.getPinCode());
        etPinCodeQueryPrefs_PinCode.requestFocus();
        
        Button btPinCodeQueryPrefs_Ok = 
        	(Button)findViewById(R.id.btPinCodeQueryPrefs_Ok);
        btPinCodeQueryPrefs_Ok.setOnClickListener(
			new OnClickButtonOkListener(this,
				spPinCodeQueryPrefs_Mode,
				etPinCodeQueryPrefs_PinCode,
				etPinCodeQueryPrefs_PinCodeReenter));

        Button btPinCodeQueryPrefs_Cancel = 
        	(Button)findViewById(R.id.btPinCodeQueryPrefs_Cancel);
        btPinCodeQueryPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
