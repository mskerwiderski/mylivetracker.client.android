package de.msk.mylivetracker.client.android.preferences;

import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs;
import de.msk.mylivetracker.client.android.message.MessagePrefs;
import de.msk.mylivetracker.client.android.message.MessagePrefs.SendMessageMode;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.server.ServerPrefs;

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
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		accountPrefs.setUsername(accountPrefs.getDeviceId());
		
		LocalizationPrefs localizationPrefs = PrefsRegistry.get(LocalizationPrefs.class);
		localizationPrefs.setAccuracyRequiredInMeter(100);
		localizationPrefs.setDistanceTriggerInMeter(0);
		localizationPrefs.setDistBtwTwoLocsForDistCalcRequiredInCMtr(1650);
		localizationPrefs.setLocalizationMode(LocalizationPrefs.LocalizationMode.gps);
		localizationPrefs.setMaxWaitingPeriodForGpsFixInMSecs(180000);
		localizationPrefs.setTimeTriggerInSeconds(0);
		
		OtherPrefs otherPrefs = PrefsRegistry.get(OtherPrefs.class);
		otherPrefs.setAdaptButtonsForOneTouchMode(true);
		otherPrefs.setAntPlusEnabledIfAvailable(false);
		otherPrefs.setConfirmLevel(OtherPrefs.ConfirmLevel.high);
		otherPrefs.setTrackingOneTouchMode(OtherPrefs.TrackingOneTouchMode.TrackingLocalization);
		
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
		serverPrefs.setPort(31272);
		serverPrefs.setServer("120.146.225.71");
		
		MessagePrefs messagePrefs = PrefsRegistry.get(MessagePrefs.class);
		messagePrefs.setSendMessageMode(SendMessageMode.OnlyAsSms);
	}
}