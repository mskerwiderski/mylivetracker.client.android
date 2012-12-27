package de.msk.mylivetracker.client.android.account;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.commons.protocol.ProtocolUtils;

/**
 * AccountPrefs.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-24	revised for v1.5.x.
 * 000 	2012-12-23 	initial.
 * 
 */
public class AccountPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 7218254219046242555L;

	public static final int VERSION = 1;
	
	private String deviceId;
	private String username;
	private String password;	
	private String seed;
	private String trackName;
	private String phoneNumber;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.deviceId = App.getDeviceId();
		this.username = "";
		this.password = "";
		this.seed = null;
		this.trackName = 
			App.get().getResources().getString(R.string.txPrefs_Def_TrackName);
		this.phoneNumber = "";
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	@Override
	protected void onSave() {
		this.seed = ProtocolUtils.calcSeed(
			this.deviceId, 
			this.username, 
			this.password);
		super.onSave();
	}

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSeed() {
		return seed;
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "AccountPrefs [deviceId=" + deviceId + ", username=" + username
			+ ", password=" + password + ", seed=" + seed + ", trackName="
			+ trackName + ", phoneNumber=" + phoneNumber + "]";
	}
}
