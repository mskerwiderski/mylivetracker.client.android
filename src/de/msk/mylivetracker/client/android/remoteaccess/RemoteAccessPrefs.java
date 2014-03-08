package de.msk.mylivetracker.client.android.remoteaccess;

import java.io.Serializable;

import org.apache.commons.lang.BooleanUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: RemoteAccessPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
	public PrefsDump getPrefsDump() {
		return new PrefsDump("RemoteAccessPrefs", 
			new ConfigPair[] {
				new ConfigPair("remoteAccessEnabled", 
					BooleanUtils.toStringTrueFalse(this.remoteAccessEnabled)),
				new ConfigPair("remoteAccessPassword", PrefsDumper.PASSWORD_MASK),
				new ConfigPair("remoteAccessUseReceiver", 
					BooleanUtils.toStringTrueFalse(this.remoteAccessUseReceiver)),
				new ConfigPair("remoteAccessReceiver", this.remoteAccessReceiver),
		});
	}
	@Override
	public String toString() {
		return "RemoteAccessPrefs [remoteAccessEnabled=" + remoteAccessEnabled
			+ ", remoteAccessPassword=" + remoteAccessPassword
			+ ", remoteAccessUseReceiver=" + remoteAccessUseReceiver
			+ ", remoteAccessReceiver=" + remoteAccessReceiver + "]";
	}
}
