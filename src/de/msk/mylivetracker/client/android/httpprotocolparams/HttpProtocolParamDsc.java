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
	private boolean disableAllowed;

	@SuppressWarnings("unused")
	private HttpProtocolParamDsc() {
	}
	
	protected HttpProtocolParamDsc(int nameId, int exampleId, 
		boolean enabled, boolean disableAllowed) {
		this.init(
			AppStd.get().getString(nameId), 
			AppStd.get().getString(exampleId), 
			enabled, disableAllowed);
	}
	
	protected HttpProtocolParamDsc(int nameId, int exampleId, 
		boolean enabled) {
		this.init(
			AppStd.get().getString(nameId), 
			AppStd.get().getString(exampleId), 
			enabled, true);
	}

	private HttpProtocolParamDsc(String name, String example, 
		boolean enabled, boolean disableAllowed) {
		this.init(name, example, 
			enabled, disableAllowed);
	}
	
	private void init(String name, String example, 
		boolean enabled, boolean disableAllowed) {
		this.name = name;
		this.example = example;
		this.enabled = enabled;
		this.disableAllowed = disableAllowed;
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
	public boolean isDisableAllowed() {
		return disableAllowed;
	}
	public void setDisableAllowed(boolean disableAllowed) {
		this.disableAllowed = disableAllowed;
	}

	protected HttpProtocolParamDsc copy() {
		return new HttpProtocolParamDsc(
			this.name,
			this.example,
			this.enabled,
			this.disableAllowed
		);		
	}

	@Override
	public String toString() {
		return "HttpProtocolParamDsc [name=" + name + ", example=" + example
			+ ", enabled=" + enabled + ", disableAllowed=" + disableAllowed
			+ "]";
	}
}
