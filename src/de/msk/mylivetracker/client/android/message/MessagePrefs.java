package de.msk.mylivetracker.client.android.message;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: MessagePrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-03	revised for v1.5.x.
 * 
 */
public class MessagePrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 4724718228589133830L;

	public static final int VERSION = 1;
	
	private static final int COUNT_MESSAGES = 6;
	
	public enum SendMessageMode {
		OnlyToServer,
		OnlyAsSms,
		ToServerAndAsSms;
		
		public boolean isModeToServerEnabled() {
			return 
				this.equals(SendMessageMode.OnlyToServer) ||
				this.equals(SendMessageMode.ToServerAndAsSms);
		}
		public boolean isModeAsSmsEnabled() {
			return 
				this.equals(SendMessageMode.OnlyAsSms) ||
				this.equals(SendMessageMode.ToServerAndAsSms);
		}
	};
	
	private String[] messageTemplates;
	private SendMessageMode sendMessageMode;
	private String smsReceiver;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.messageTemplates = new String[COUNT_MESSAGES];
		int idx = 0;
		this.messageTemplates[idx++] = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate0Def);
		this.messageTemplates[idx++] = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate1Def);
		this.messageTemplates[idx++] = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate2Def);
		this.messageTemplates[idx++] = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate3Def);
		this.messageTemplates[idx++] = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate4Def);
		this.messageTemplates[idx++] = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate5Def);
		this.sendMessageMode = SendMessageMode.OnlyToServer;
		this.smsReceiver = "";
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public String getMessageTemplate(int idx) {
		if ((idx < 0) || (idx > COUNT_MESSAGES)) {
			throw new IllegalArgumentException(
				"idx must be greater than 0 and less than " + COUNT_MESSAGES + "!");
		}
		return messageTemplates[idx];
	}
	public void setMessageTemplate(int idx, String messageTemplate) {
		if ((idx < 0) || (idx > COUNT_MESSAGES)) {
			throw new IllegalArgumentException(
				"idx must be greater than 0 and less than " + COUNT_MESSAGES + "!");
		}
		if (messageTemplate == null) {
			throw new IllegalArgumentException("messageTemplate must not be null!");
		}
		this.messageTemplates[idx] = messageTemplate;
	}

	public String[] getMessageTemplatesAsComboboxItems() {
		String[] messageTemplatesAsComboboxItems = new String[COUNT_MESSAGES];
		for (int idx = 0; idx < COUNT_MESSAGES; idx++) {
			messageTemplatesAsComboboxItems[idx] =
				StringUtils.abbreviate(this.messageTemplates[idx], 25);
		}
		return messageTemplatesAsComboboxItems;
	}
	
	public SendMessageMode getSendMessageMode() {
		return sendMessageMode;
	}
	public void setSendMessageMode(SendMessageMode sendMessageMode) {
		this.sendMessageMode = sendMessageMode;
	}
	public boolean isSendMessageModeToServerEnabled() {
		return this.sendMessageMode.isModeToServerEnabled();			
	}
	public boolean isSendMessageModeAsSmsEnabled() {
		return this.sendMessageMode.isModeAsSmsEnabled();
	}
	public String getSmsReceiver() {
		return smsReceiver;
	}
	public void setSmsReceiver(String smsReceiver) {
		this.smsReceiver = smsReceiver;
	}
	@Override
	public String getShortName() {
		return "message";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("MessagePrefs", 
			new ConfigPair[] {
				new ConfigPair("sendMessageMode", this.sendMessageMode.name()),
				new ConfigPair("messageTemplates", 
					ArrayUtils.toString(this.messageTemplates)),
				new ConfigPair("smsReceiver", this.smsReceiver),
		});
	}
	@Override
	public String toString() {
		return "MessagePrefs [messageTemplates="
			+ Arrays.toString(messageTemplates) + ", sendMessageMode="
			+ sendMessageMode + ", smsReceiver=" + smsReceiver + "]";
	}
}
