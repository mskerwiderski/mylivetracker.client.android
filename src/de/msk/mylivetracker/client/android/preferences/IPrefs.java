package de.msk.mylivetracker.client.android.preferences;

import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: IPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public interface IPrefs {
	public int getVersion();
	public void initWithDefaults();
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr);
	public PrefsDump getPrefsDump();
}
