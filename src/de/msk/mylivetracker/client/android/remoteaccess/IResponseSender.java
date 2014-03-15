package de.msk.mylivetracker.client.android.remoteaccess;

/**
 * classname: IResponseSender
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-15	origin.
 * 
 */
public interface IResponseSender {
	public void sendResponse(String receiver, String response);
}
