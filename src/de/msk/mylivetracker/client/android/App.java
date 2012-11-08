package de.msk.mylivetracker.client.android;

import android.app.Application;
import android.content.Context;

/**
 * App.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-11-04 initial.
 * 
 */
public class App extends Application {
	private static Context context;

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getCtx() {
        return App.context;
    }
}
