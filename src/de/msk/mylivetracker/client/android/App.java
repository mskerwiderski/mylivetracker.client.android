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
	private static App app = null;
	private static Context context;
    public void onCreate(){
        super.onCreate();
        app = this;
        context = getApplicationContext();
    }
    @Override
	public void onTerminate() {
		context = null;
		app = null;
		super.onTerminate();
	}
    public static App get() {
        return app;
    }
	public static Context getCtx() {
        return context;
    }
}
