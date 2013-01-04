package de.msk.mylivetracker.client.android.message;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.APrefs;

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
	
	private String messageTemplate1;
	private String messageTemplate2;
	private String messageTemplate3;	
	private String messageTemplate4;
	private String messageTemplate5;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.messageTemplate1 = 
			App.get().getResources().getString(R.string.txMsgPrefs_MessageTemplate_1_Def);
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public String getMessageTemplate1() {
		return messageTemplate1;
	}
	public void setMessageTemplate1(String messageTemplate1) {
		this.messageTemplate1 = messageTemplate1;
	}
	public String getMessageTemplate2() {
		return messageTemplate2;
	}
	public void setMessageTemplate2(String messageTemplate2) {
		this.messageTemplate2 = messageTemplate2;
	}
	public String getMessageTemplate3() {
		return messageTemplate3;
	}
	public void setMessageTemplate3(String messageTemplate3) {
		this.messageTemplate3 = messageTemplate3;
	}
	public String getMessageTemplate4() {
		return messageTemplate4;
	}
	public void setMessageTemplate4(String messageTemplate4) {
		this.messageTemplate4 = messageTemplate4;
	}
	public String getMessageTemplate5() {
		return messageTemplate5;
	}
	public void setMessageTemplate5(String messageTemplate5) {
		this.messageTemplate5 = messageTemplate5;
	}

	@Override
	public String toString() {
		return "MessagePrefs [messageTemplate1=" + messageTemplate1
			+ ", messageTemplate2=" + messageTemplate2
			+ ", messageTemplate3=" + messageTemplate3
			+ ", messageTemplate4=" + messageTemplate4
			+ ", messageTemplate5=" + messageTemplate5 + "]";
	}
}
