package de.msk.mylivetracker.client.android.preferences;


import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;

/**
 * classname: OnTrackPhoneTrackerDefaults
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-10-04	origin.
 * 
 */
public class OnTrackPhoneTrackerDefaults {

	public static void run() {
		PinCodeQueryPrefs pinCodeQueryPrefs = PrefsRegistry.get(PinCodeQueryPrefs.class);
		pinCodeQueryPrefs.setPinCodeQueryEnabled(true);
		pinCodeQueryPrefs.setProtectSettingsOnly(true);
		pinCodeQueryPrefs.setPinCode("0000");
		
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
		protocolPrefs.setCloseConnectionAfterEveryUpload(true);
		protocolPrefs.setFinishEveryUploadWithALinefeed(true);
		protocolPrefs.setLogTrackData(true);
		protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.tk102Emulator);
		protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Off);
		protocolPrefs.setUplPositionBufferSize(ProtocolPrefs.BufferSize.disabled);
		protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min2);
		protocolPrefs.setUplTriggerLogic(ProtocolPrefs.UploadTriggerLogic.OR);
		
		ServerPrefs serverPrefs = PrefsRegistry.get(ServerPrefs.class);
		serverPrefs.setPath("");
		serverPrefs.setPort(22022);
		serverPrefs.setServer("online.ontrackgps.net");
		
		TrackingModePrefs trackingModePrefs = PrefsRegistry.get(TrackingModePrefs.class);
		trackingModePrefs.setTrackingMode(TrackingMode.Auto);
		trackingModePrefs.setAutoModeResetTrackMode(AutoModeResetTrackMode.NextDay);
		trackingModePrefs.setStartAfterReboot(true);
		trackingModePrefs.setInterruptibleOnMainWindow(true);
		trackingModePrefs.setRunOnlyIfBattFullOrCharging(true);
	}
}