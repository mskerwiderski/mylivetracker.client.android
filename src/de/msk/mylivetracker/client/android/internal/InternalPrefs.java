package de.msk.mylivetracker.client.android.internal;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: InternalPrefs
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-10	origin.
 * 
 */
public class InternalPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 5915614953131956033L;

	public static final int VERSION = 1;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	@Override
	public String getShortName() {
		return "internal";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("InternalPrefs", 
			new ConfigPair[] {
		});
	}
}
