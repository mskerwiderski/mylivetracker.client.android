package de.msk.mylivetracker.client.android.preferences;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: MainPrefs
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-01	origin.
 * 
 */
public class MainPrefs extends APrefs implements Serializable {

	private static final long serialVersionUID = 4107742423666510807L;

	public static final int VERSION = 160;

	@Override
	public int getVersion() {
		return VERSION;
	}

	@Override
	public void initWithDefaults() {
	}

	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("main", 
			new ConfigPair[] {
				new ConfigPair("prefs structure version", 
					String.valueOf(this.getVersion())),
		});
	}

	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
}
