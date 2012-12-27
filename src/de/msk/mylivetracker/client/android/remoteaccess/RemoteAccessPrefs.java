package de.msk.mylivetracker.client.android.remoteaccess;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * RemoteAccessPrefs.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-12-25 	initial.
 * 
 */
public class RemoteAccessPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 5284108073917449298L;

	public static final int VERSION = 1;
	
	private boolean remoteAccessEnabled;
	private String remoteAccessPassword;
	private boolean remoteAccessUseReceiver;
	private String remoteAccessReceiver;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.remoteAccessEnabled = false;
		this.remoteAccessPassword = "";
		this.remoteAccessUseReceiver = false;
		this.remoteAccessReceiver = "";
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public boolean isRemoteAccessEnabled() {
		return remoteAccessEnabled;
	}
	public void setRemoteAccessEnabled(boolean remoteAccessEnabled) {
		this.remoteAccessEnabled = remoteAccessEnabled;
	}
	public String getRemoteAccessPassword() {
		return remoteAccessPassword;
	}
	public void setRemoteAccessPassword(String remoteAccessPassword) {
		this.remoteAccessPassword = remoteAccessPassword;
	}
	public boolean isRemoteAccessUseReceiver() {
		return remoteAccessUseReceiver;
	}
	public void setRemoteAccessUseReceiver(boolean remoteAccessUseReceiver) {
		this.remoteAccessUseReceiver = remoteAccessUseReceiver;
	}
	public String getRemoteAccessReceiver() {
		return remoteAccessReceiver;
	}
	public void setRemoteAccessReceiver(String remoteAccessReceiver) {
		this.remoteAccessReceiver = remoteAccessReceiver;
	}

	@Override
	public String toString() {
		return "RemoteAccessPrefs [remoteAccessEnabled=" + remoteAccessEnabled
			+ ", remoteAccessPassword=" + remoteAccessPassword
			+ ", remoteAccessUseReceiver=" + remoteAccessUseReceiver
			+ ", remoteAccessReceiver=" + remoteAccessReceiver + "]";
	}
}
