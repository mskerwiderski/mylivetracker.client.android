package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

/**
 * classname: MessageInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class MessageInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = 820167424418185243L;

	private static MessageInfo messageInfo = null;
	
	public static void update(String message) {
		if (TrackStatus.isInResettingState()) return;
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
	public static void set(MessageInfo messageInfo) {
		MessageInfo.messageInfo = messageInfo;
	}
	
	private String message;
	
	private MessageInfo() {
	}
	
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
		builder.append("MessageInfo [message=").append(message).append("]");
		return builder.toString();
	}
	
	public String getMessage() {
		return message;
	}	
}
