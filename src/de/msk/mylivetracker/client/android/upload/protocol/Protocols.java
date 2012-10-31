package de.msk.mylivetracker.client.android.upload.protocol;

/**
 * Protocols.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class Protocols {

	public static IProtocol createProtocolDummy() {
		return new de.msk.mylivetracker.client.android.upload.protocol.DummyProtocol();
	}
	public static IProtocol createProtocolMltUrlparams() {
		return new de.msk.mylivetracker.client.android.upload.protocol.mlt.urlparams.ProtocolEncoder();
	}
	public static IProtocol createProtocolMltDatastrEncrypted() {
		return new de.msk.mylivetracker.client.android.upload.protocol.mlt.datastr.encrypted.ProtocolEncoder();
	}
	public static IProtocol createProtocolXexunTk102() {
		return new de.msk.mylivetracker.client.android.upload.protocol.xexun.tk102.ProtocolEncoder();
	}
	public static IProtocol createProtocolIncutexTk5000() {
		return new de.msk.mylivetracker.client.android.upload.protocol.incutex.tk5000.ProtocolEncoder();
	}
}
