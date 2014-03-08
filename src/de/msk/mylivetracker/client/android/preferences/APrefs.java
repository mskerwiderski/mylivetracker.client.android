package de.msk.mylivetracker.client.android.preferences;

import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: APrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class APrefs implements IPrefs {
	protected void onSave() {
		// noop.
	}

	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump(this.getClass().getSimpleName(), 
			new ConfigPair[] {
				new ConfigPair("all", this.toString()),
		});
	}
}
