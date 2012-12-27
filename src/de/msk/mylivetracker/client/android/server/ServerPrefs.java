package de.msk.mylivetracker.client.android.server;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * ServerPrefs.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 001	2012-12-24 revised for v1.5.x.
 * 000 	2012-12-24 initial.
 * 
 */
public class ServerPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 9160688233435283890L;

	public static final int VERSION = 1;
	
	private String server;
	private int port;
	private String path;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.server = "";
		this.port = 80;
		this.path = "";
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}

	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "ServerPrefs [server=" + server + ", port=" + port + ", path="
			+ path + "]";
	}
}
