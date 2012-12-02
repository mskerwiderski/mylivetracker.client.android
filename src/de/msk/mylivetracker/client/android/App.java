package de.msk.mylivetracker.client.android;

import de.msk.mylivetracker.client.android.app.AbstractApp;

/**
 * App.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-11-24 initial.
 * 
 */
public class App extends AbstractApp {

	@Override
	public String getName() {
		return "MyLiveTracker";
	}

	@Override
	protected boolean isProAux() {
		return false;
	}
}
