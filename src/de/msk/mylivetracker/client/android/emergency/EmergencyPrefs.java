package de.msk.mylivetracker.client.android.emergency;

import java.io.Serializable;

import org.apache.commons.lang3.BooleanUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

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
	
	private String messageText;
	private boolean sendAsSms;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.messageText = App.getCtx().getString(
			R.string.txMain_EmergencyActivated);
		this.sendAsSms = false;
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
	public boolean isSendAsSms() {
		return sendAsSms;
	}
	public void setSendAsSms(boolean sendAsSms) {
		this.sendAsSms = sendAsSms;
	}
	@Override
	public String getShortName() {
		return "emergency";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("EmergencyPrefs", 
			new ConfigPair[] {
				new ConfigPair("messageText", this.messageText),
				new ConfigPair("sendAsSms",
					BooleanUtils.toStringYesNo(this.sendAsSms)),
		});
	}
	@Override
	public String toString() {
		return "EmergencyPrefs [messageText=" + messageText + ", sendAsSms="
			+ sendAsSms + "]";
	}
}
