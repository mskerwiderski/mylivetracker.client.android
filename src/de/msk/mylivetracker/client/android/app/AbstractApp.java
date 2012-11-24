package de.msk.mylivetracker.client.android.app;

import android.app.Application;
import android.content.Context;

/**
 * AbstractApp.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-11-04 initial.
 * 
 */
public abstract class AbstractApp extends Application {
	private static AbstractApp app = null;
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
    public static AbstractApp get() {
        return app;
    }
	public static Context getCtx() {
        return context;
    }
	public abstract String getName();
}
