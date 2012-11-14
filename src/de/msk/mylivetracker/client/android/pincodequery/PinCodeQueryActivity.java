package de.msk.mylivetracker.client.android.pincodequery;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PinCodeQueryActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-11-13 initial.
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
	
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			if (ValidatorUtils.validatePinCode(
				this.activity, etPinCodeQuery_PinCode)) {
				this.activity.finish();
			}
		}		
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
