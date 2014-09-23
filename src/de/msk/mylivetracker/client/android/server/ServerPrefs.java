package de.msk.mylivetracker.client.android.server;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: ServerPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ServerPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 9160688233435283890L;

	public static final int VERSION = 1;
	
	private String server;
	private int port;
	private String path;
	private String smsReceivers;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.server = "";
		this.port = 80;
		this.path = "";
		this.smsReceivers = "";
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
	public String getSmsReceivers() {
		return smsReceivers;
	}
	public void setSmsReceivers(String smsReceivers) {
		this.smsReceivers = smsReceivers;
	}
	@Override
	public String getShortName() {
		return "server";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("ServerPrefs", 
			new ConfigPair[] {
				new ConfigPair("server", this.server),
				new ConfigPair("port", String.valueOf(this.port)),
				new ConfigPair("path", this.path),
				new ConfigPair("smsReceivers", this.smsReceivers),
		});
	}
	@Override
	public String toString() {
		return "ServerPrefs [server=" + server + ", port=" + port + ", path="
			+ path + ", smsReceivers=" + smsReceivers + "]";
	}
}
