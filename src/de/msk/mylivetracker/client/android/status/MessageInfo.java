package de.msk.mylivetracker.client.android.status;

/**
 * MessageInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class MessageInfo extends AbstractInfo {
	private static MessageInfo messageInfo = null;
	public static void update(String message) {
		messageInfo = 
			MessageInfo.createNewMessageInfo(
				messageInfo, message);
	}
	public static MessageInfo get() {
		return messageInfo;
	}
	public static void reset() {
		messageInfo = null;
	}
	
	private String message;
	
	private MessageInfo(String message) {
		this.message = message;
	}	

	public static MessageInfo createNewMessageInfo(
		MessageInfo currMessageInfo,
		String message) {
		String newMessage = message;		
		return new MessageInfo(newMessage); 
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(message).append("]");
		return builder.toString();
	}

	public String getMessage() {
		return message;
	}	
}
