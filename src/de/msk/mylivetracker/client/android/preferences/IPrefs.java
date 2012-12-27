package de.msk.mylivetracker.client.android.preferences;

/**
 * IPrefs.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-23 	revised for v1.5.x.
 * 000 	2012-12-23 	initial.
 * 
 */
public interface IPrefs {
	public int getVersion();
	public void initWithDefaults();
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr);
}
