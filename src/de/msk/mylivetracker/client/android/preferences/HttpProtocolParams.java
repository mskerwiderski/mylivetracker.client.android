package de.msk.mylivetracker.client.android.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * HttpProtocolParams.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-12-01 initial. 
 * 
 */
public class HttpProtocolParams implements Serializable {

	private static final long serialVersionUID = -5362511109345782859L;

	public enum ParamId {
		Latitude,
		Longitude,
	}
	
	public List<HttpProtocolParamDsc> params = new ArrayList<HttpProtocolParamDsc>();
	
	public static HttpProtocolParams create() {
		HttpProtocolParams httpProtocolParams = new HttpProtocolParams();
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameLatitude,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleLatitude, 
			true));
		httpProtocolParams.params.add(new HttpProtocolParamDsc(
			R.string.txPrefsHttpProtocolParams_ParamNameLongitude,
			R.string.txPrefsHttpProtocolParams_ParamValueExampleLongitude, 
			true));
		return httpProtocolParams;
	}
	
	public HttpProtocolParams copy() {
		LogUtils.infoMethodIn(this.getClass(), "copy");
		HttpProtocolParams httpProtocolParams = new HttpProtocolParams();
		for (HttpProtocolParamDsc dsc : this.params) {
			httpProtocolParams.params.add(dsc.copy());
			LogUtils.infoMethodState(this.getClass(), "copy", "param", dsc);
		}
		LogUtils.infoMethodOut(this.getClass(), "copy", httpProtocolParams.params.size());
		return httpProtocolParams;
	}
	
	private HttpProtocolParams() {
	}

	public HttpProtocolParamDsc getParamDsc(ParamId id) {
		return params.get(id.ordinal());
	}
	public HttpProtocolParamDsc getParamDsc(int id) {
		return params.get(id);
	}
}
