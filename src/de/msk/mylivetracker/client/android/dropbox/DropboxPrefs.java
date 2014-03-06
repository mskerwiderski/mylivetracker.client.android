package de.msk.mylivetracker.client.android.dropbox;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * classname: DropboxPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class DropboxPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 617837667667614881L;

	public static final int VERSION = 2;
	
	private String account;
	private String tokenOAuth2;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.resetAccountAndToken();
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public void setAccountAndToken(String account, String tokenOAuth2) {
		if (StringUtils.isEmpty(account)) {
			throw new IllegalArgumentException("account must not be empty!");
		}
		if (StringUtils.isEmpty(tokenOAuth2)) {
			throw new IllegalArgumentException("invalid tokenOAuth2!");
		}
		this.account = account;
		this.tokenOAuth2 = tokenOAuth2;
	}
	public void resetAccountAndToken() {
		this.account = null;
		this.tokenOAuth2 = null;
	}
	public boolean hasValidAccountAndToken() {
		return (!StringUtils.isEmpty(this.account) ||
			!StringUtils.isEmpty(this.tokenOAuth2));
	}
	public String getTokenOAuth2() {
		if (!hasValidAccountAndToken()) {
			throw new IllegalStateException("invalid details!");
		}
		return this.tokenOAuth2;
	}
	public String getAccount() {
		if (!hasValidAccountAndToken()) {
			throw new IllegalStateException("invalid details");
		}
		return account;
	}
	@Override
	public String toString() {
		return "DropboxPrefs [account=" + account + ", tokenOAuth2="
				+ tokenOAuth2 + "]";
	}
}
