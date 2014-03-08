package de.msk.mylivetracker.client.android.emergency;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;

/**
 * classname: EmergencyPrefs
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-01	origin.
 * 
 */
public class EmergencyPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 6463824605713788784L;

	public static final int VERSION = 1;
	
	private static final String DEF_MESSAGE = "SOS";
	
	private String messageText;
	
	public static String getEmergencyMessageText() {
		String msg = PrefsRegistry.get(EmergencyPrefs.class).getMessageText();
		if (StringUtils.isEmpty(msg)) {
			msg = DEF_MESSAGE;
		}
		return msg;
	}
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.messageText = DEF_MESSAGE;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("EmergencyPrefs", 
			new ConfigPair[] {
				new ConfigPair("messageText", this.messageText),
		});
	}
	@Override
	public String toString() {
		return "EmergencyPrefs [messageText=" + messageText + "]";
	}
}
