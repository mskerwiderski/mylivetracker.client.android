package de.msk.mylivetracker.client.android.app.pro;

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
		return "MLTAppPro";
	}

	@Override
	protected boolean isProAux() {
		return true;
	}
}
