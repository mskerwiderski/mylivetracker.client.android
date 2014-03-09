package de.msk.mylivetracker.client.android.httpprotocolparams;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParams.ParamId;
import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: HttpProtocolParamsPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
	public PrefsDump getPrefsDump() {
		ConfigPair[] configPairs = new ConfigPair[this.httpProtocolParams.getParams().size()];
		for (int idx=0; idx < this.httpProtocolParams.getParams().size(); idx++) {
			HttpProtocolParamDsc paramDsc = this.getHttpProtocolParams().getParamDsc(idx);
			configPairs[idx] = new ConfigPair(ParamId.values()[idx].name(), paramDsc.getName());
		}
		return new PrefsDump("HttpProtocolParamsPrefs", configPairs);
	}
	@Override
	public String getShortName() {
		return "httpprotocolparams";
	}
	@Override
	public String toString() {
		return "HttpProtocolParamsPrefs [httpProtocolParams="
			+ httpProtocolParams + "]";
	}
}
