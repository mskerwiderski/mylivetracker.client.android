package de.msk.mylivetracker.client.android.preferences.liontrack;

import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.server.ServerPrefs;

/**
 * classname: LiontrackDefaults
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-30	revised for v1.5.x.
 * 
 */
public class LiontrackDefaults {

	public static void run() {
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		accountPrefs.setUsername(accountPrefs.getDeviceId());
		
		LocalizationPrefs localizationPrefs = PrefsRegistry.get(LocalizationPrefs.class);
		localizationPrefs.setAccuracyRequiredInMeter(100);
		localizationPrefs.setDistanceTriggerInMeter(0);
		localizationPrefs.setDistBtwTwoLocsForDistCalcRequiredInCMtr(1650);
		localizationPrefs.setLocalizationMode(LocalizationPrefs.LocalizationMode.gps);
		localizationPrefs.setMaxWaitingPeriodForGpsFixInMSecs(1800);
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
	}
}
