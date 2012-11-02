package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * AppRestart.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-11-01 initial.
 * 
 */
public class AppRestart extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.exit();
    }
    public static void doRestart(Activity activity) {
        activity.startActivity(new Intent(activity.getApplicationContext(), AppRestart.class));
    }
}
