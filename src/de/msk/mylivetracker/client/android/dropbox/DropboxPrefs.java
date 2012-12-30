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

	public static final int VERSION = 1;
	
	private String account;
	private String tokenKey;
	private String tokenSecret;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.resetAccountAndTokens();
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public void setAccountAndTokens(String account, String key, String secret) {
		if (StringUtils.isEmpty(account)) {
			throw new IllegalArgumentException("account must not be empty!");
		}
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(secret)) {
			throw new IllegalArgumentException("invalid tokens!");
		}
		this.account = account;
		this.tokenKey = key;
		this.tokenSecret = secret;
	}
	public void resetAccountAndTokens() {
		this.account = null;
		this.tokenKey = null;
		this.tokenSecret = null;
	}
	public boolean hasValidAccountAndTokens() {
		return (!StringUtils.isEmpty(this.account) ||
			!StringUtils.isEmpty(this.tokenKey) || 
			!StringUtils.isEmpty(this.tokenSecret));
	}
	public String[] getTokens() {
		if (!hasValidAccountAndTokens()) {
			throw new IllegalArgumentException("invalid details!");
		}
		return new String[] { this.tokenKey, this.tokenSecret };
	}
	public String getAccount() {
		if (!hasValidAccountAndTokens()) {
			throw new IllegalArgumentException("invalid details!");
		}
		return account;
	}

	@Override
	public String toString() {
		return "DropboxPrefs [account=" + account + ", tokenKey=" + tokenKey
			+ ", tokenSecret=" + tokenSecret + "]";
	}
}
