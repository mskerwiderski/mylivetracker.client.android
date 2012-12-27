package de.msk.mylivetracker.client.android.pincodequery;

import org.apache.commons.lang.StringUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * PinCodeQueryActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-23 	revised for v1.5.x.
 * 000 	2012-11-13 	initial.
 * 
 */
public class PinCodeQueryActivity extends AbstractActivity {	
	
	private static final class OnClickButtonOkListener implements OnClickListener {
		private PinCodeQueryActivity activity;
		private EditText etPinCodeQuery_PinCode;
		
		public OnClickButtonOkListener(
			PinCodeQueryActivity activity,
			EditText etPinCodeQuery_PinCode) {
			this.activity = activity;
			this.etPinCodeQuery_PinCode = etPinCodeQuery_PinCode;		
		}
	
		@Override
		public void onClick(View v) {
			boolean valid = StringUtils.equals(
				etPinCodeQuery_PinCode.getText().toString(), 
				PrefsRegistry.get(PinCodeQueryPrefs.class).getPinCode());
			if (!valid) {
				SimpleInfoDialog.show(this.activity, 
					R.string.vdPinCodeQueryPrefs_PinCodeInvalid);
			} else {
				AbstractActivity.setPinCodeValid();
				this.activity.finish();
			}
		}		
	}
	
	@Override
	public void onBackPressed() {
		// noop.
		return;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_code_query);
        this.setTitle(R.string.tiPinCodeQuery);
        EditText etPinCodeQuery_PinCode = (EditText) findViewById(R.id.etPinCodeQuery_PinCode);
        etPinCodeQuery_PinCode.setText("");
        etPinCodeQuery_PinCode.requestFocus();
        Button btPinCodeQuery_Ok = (Button) findViewById(R.id.btPinCodeQuery_Ok);
        btPinCodeQuery_Ok.setOnClickListener(
			new OnClickButtonOkListener(
				this, etPinCodeQuery_PinCode));
    }
}
