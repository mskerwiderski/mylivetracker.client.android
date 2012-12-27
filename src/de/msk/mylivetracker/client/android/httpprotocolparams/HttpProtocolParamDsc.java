package de.msk.mylivetracker.client.android.httpprotocolparams;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.AppStd;

/**
 * HttpProtocolParamDsc.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-12-01 	initial. 
 * 
 */
public class HttpProtocolParamDsc implements Serializable {
	private static final long serialVersionUID = -6750634648823564869L;
	
	private String name;
	private String example;
	private boolean enabled;

	@SuppressWarnings("unused")
	private HttpProtocolParamDsc() {
	}
	
	private HttpProtocolParamDsc(String name, String example, boolean enabled) {
		this.name = name;
		this.example = example;
		this.enabled = enabled;
	}

	protected HttpProtocolParamDsc(int nameId, int exampleId, boolean enabled) {
		this.name = AppStd.get().getString(nameId);
		this.example = AppStd.get().getString(exampleId);
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExample() {
		return example;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected HttpProtocolParamDsc copy() {
		return new HttpProtocolParamDsc(
			this.name,
			this.example,
			this.enabled
		);		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpParamDsc [name=").append(name).append(", example=")
			.append(example).append(", enabled=").append(enabled)
			.append("]");
		return builder.toString();
	}
}
