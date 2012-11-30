package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;

/**
 * PrefsHttpProtocolParamsActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-11-30 initial.
 * 
 */
public class PrefsHttpProtocolParamsActivity extends AbstractActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_http_protocol_params);
     
        this.setTitle(R.string.tiPrefsHttpProtocolParams);
        
        Preferences prefs = Preferences.get();
        
        
    }
}
