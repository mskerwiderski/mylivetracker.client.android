package de.msk.mylivetracker.client.android.httpprotocolparams;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * HttpProtocolParamsPrefs.
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
public class HttpProtocolParamsPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 3657572362812775426L;

	public static final int VERSION = 1;
	
	private HttpProtocolParams httpProtocolParams;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.httpProtocolParams = HttpProtocolParams.create();
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public HttpProtocolParams getHttpProtocolParams() {
		return httpProtocolParams;
	}
	public void setHttpProtocolParams(HttpProtocolParams httpProtocolParams) {
		this.httpProtocolParams = httpProtocolParams;
	}

	@Override
	public String toString() {
		return "HttpProtocolParamsPrefs [httpProtocolParams="
			+ httpProtocolParams + "]";
	}
}