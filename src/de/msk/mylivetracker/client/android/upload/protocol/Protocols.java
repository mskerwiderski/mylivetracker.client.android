package de.msk.mylivetracker.client.android.upload.protocol;

/**
 * classname: Protocols
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class Protocols {

	public static IProtocol createProtocolDummy() {
		return new de.msk.mylivetracker.client.android.upload.protocol.DummyProtocol();
	}
	public static IProtocol createProtocolMltUrlparams() {
		return new de.msk.mylivetracker.client.android.upload.protocol.http.userdefined.ProtocolEncoder();
	}
	public static IProtocol createProtocolMltDatastrEncrypted() {
		return new de.msk.mylivetracker.client.android.upload.protocol.mylivetracker.ProtocolEncoder();
	}
	public static IProtocol createProtocolXexunTk102() {
		return new de.msk.mylivetracker.client.android.upload.protocol.xexun.tk102.ProtocolEncoder();
	}
	public static IProtocol createProtocolIncutexTk5000() {
		return new de.msk.mylivetracker.client.android.upload.protocol.incutex.tk5000.ProtocolEncoder();
	}
}
