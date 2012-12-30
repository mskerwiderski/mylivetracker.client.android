package de.msk.mylivetracker.client.android.preferences.prefsv144;

import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.auto.AutoPrefs;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.server.ServerPrefs;

/**
 * classname: PrefsV144Updater
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-30	revised for v1.5.x.
 * 
 */
public class PrefsV144Updater {

	public static boolean run() {
		PrefsV144 prefsv144 = PrefsV144.load();
		if (prefsv144 == null) {
			// old preferences not found or 
			// update was not possible or
			// update failed.
			return false;
		}
		
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		accountPrefs.setUsername(prefsv144.getUsername());
		accountPrefs.setPassword(prefsv144.getPassword());
		accountPrefs.setPhoneNumber(prefsv144.getPhoneNumber());
		accountPrefs.setTrackName(prefsv144.getTrackName());
		
		AutoPrefs autoPrefs = PrefsRegistry.get(AutoPrefs.class);
		autoPrefs.setAutoModeEnabled(prefsv144.isAutoModeEnabled());
		if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.Hour1)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.Hour1);
		} else if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.Hours2)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.Hours2);
		} else if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.Hours24)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.Hours24);
		} else if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.Hours4)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.Hours4);
		} else if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.Hours8)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.Hours8);
		} else if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.Never)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.Never);
		} else if (prefsv144.getAutoModeResetTrackMode().equals(PrefsV144.AutoModeResetTrackMode.NextDay)) {
			autoPrefs.setAutoModeResetTrackMode(AutoPrefs.AutoModeResetTrackMode.NextDay);
		}
		
		autoPrefs.setAutoStartEnabled(prefsv144.isAutoStartEnabled());
		
		LocalizationPrefs localizationPrefs = PrefsRegistry.get(LocalizationPrefs.class);
		localizationPrefs.setAccuracyRequiredInMeter(prefsv144.getLocAccuracyRequiredInMeter());
		localizationPrefs.setDistanceTriggerInMeter(prefsv144.getLocDistanceTriggerInMeter());
		localizationPrefs.setDistBtwTwoLocsForDistCalcRequiredInCMtr(prefsv144.getLocDistBtwTwoLocsForDistCalcRequiredInCMtr());
		if (prefsv144.getLocalizationMode().equals(PrefsV144.LocalizationMode.gps)) {
			localizationPrefs.setLocalizationMode(LocalizationPrefs.LocalizationMode.gps);
		} else if (prefsv144.getLocalizationMode().equals(PrefsV144.LocalizationMode.gpsAndNetwork)) {
			localizationPrefs.setLocalizationMode(LocalizationPrefs.LocalizationMode.gpsAndNetwork);
		} else if (prefsv144.getLocalizationMode().equals(PrefsV144.LocalizationMode.network)) {
			localizationPrefs.setLocalizationMode(LocalizationPrefs.LocalizationMode.network);
		}
		localizationPrefs.setTimeTriggerInSeconds(prefsv144.getLocTimeTriggerInSeconds());
		
		OtherPrefs otherPrefs = PrefsRegistry.get(OtherPrefs.class);
		if (prefsv144.getConfirmLevel().equals(PrefsV144.ConfirmLevel.high)) {
			otherPrefs.setConfirmLevel(OtherPrefs.ConfirmLevel.high);
		} else if (prefsv144.getConfirmLevel().equals(PrefsV144.ConfirmLevel.low)) {
			otherPrefs.setConfirmLevel(OtherPrefs.ConfirmLevel.low);
		} else if (prefsv144.getConfirmLevel().equals(PrefsV144.ConfirmLevel.medium)) {
			otherPrefs.setConfirmLevel(OtherPrefs.ConfirmLevel.medium);
		}
		if (prefsv144.getTrackingOneTouchMode().equals(PrefsV144.TrackingOneTouchMode.TrackingLocalization)) {
			otherPrefs.setTrackingOneTouchMode(OtherPrefs.TrackingOneTouchMode.TrackingLocalization);
		} else if (prefsv144.getTrackingOneTouchMode().equals(PrefsV144.TrackingOneTouchMode.TrackingLocalizationHeartrate)) {
			otherPrefs.setTrackingOneTouchMode(OtherPrefs.TrackingOneTouchMode.TrackingLocalizationHeartrate);
		} else if (prefsv144.getTrackingOneTouchMode().equals(PrefsV144.TrackingOneTouchMode.TrackingOnly)) {
			otherPrefs.setTrackingOneTouchMode(OtherPrefs.TrackingOneTouchMode.TrackingOnly);
		}
		
		ProtocolPrefs protocolPrefs = PrefsRegistry.get(ProtocolPrefs.class);
		protocolPrefs.setCloseConnectionAfterEveryUpload(prefsv144.isCloseConnectionAfterEveryUpload());
		protocolPrefs.setFinishEveryUploadWithALinefeed(prefsv144.isFinishEveryUploadWithALinefeed());
		if (prefsv144.getTransferProtocol().equals(PrefsV144.TransferProtocol.fransonGpsGateHttp)) {
			protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.uploadDisabled);
		} else if (prefsv144.getTransferProtocol().equals(PrefsV144.TransferProtocol.mltHttpPlain)) {
			protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.httpUserDefined);
		} else if (prefsv144.getTransferProtocol().equals(PrefsV144.TransferProtocol.mltTcpEncrypted)) {
			protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.mltTcpEncrypted);
		} else if (prefsv144.getTransferProtocol().equals(PrefsV144.TransferProtocol.tk102Emulator)) {
			protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.tk102Emulator);
		} else if (prefsv144.getTransferProtocol().equals(PrefsV144.TransferProtocol.tk5000Emulator)) {
			protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.tk5000Emulator);
		} else if (prefsv144.getTransferProtocol().equals(PrefsV144.TransferProtocol.uploadDisabled)) {
			protocolPrefs.setTransferProtocol(ProtocolPrefs.TransferProtocol.uploadDisabled);
		}
		if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Km1)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Km1);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Km10)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Km10);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Km2)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Km2);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Km5)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Km5);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Km50)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Km50);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Mtr100)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Mtr100);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Mtr200)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Mtr200);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Mtr300)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Mtr300);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Mtr50)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Mtr50);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Mtr500)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Mtr500);
		} else if (prefsv144.getUplDistanceTrigger().equals(PrefsV144.UploadDistanceTrigger.Off)) {
			protocolPrefs.setUplDistanceTrigger(ProtocolPrefs.UploadDistanceTrigger.Off);
		}
		if (prefsv144.getUplPositionBufferSize().equals(PrefsV144.BufferSize.disabled)) {
			protocolPrefs.setUplPositionBufferSize(ProtocolPrefs.BufferSize.disabled);
		} else if (prefsv144.getUplPositionBufferSize().equals(PrefsV144.BufferSize.pos10)) {
			protocolPrefs.setUplPositionBufferSize(ProtocolPrefs.BufferSize.pos10);
		} else if (prefsv144.getUplPositionBufferSize().equals(PrefsV144.BufferSize.pos3)) {
			protocolPrefs.setUplPositionBufferSize(ProtocolPrefs.BufferSize.pos3);
		} else if (prefsv144.getUplPositionBufferSize().equals(PrefsV144.BufferSize.pos5)) {
			protocolPrefs.setUplPositionBufferSize(ProtocolPrefs.BufferSize.pos5);
		}
		if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Hr1)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Hr1);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Min1)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min1);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Min10)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min10);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Min20)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min20);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Min3)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min3);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Min30)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min30);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Min5)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Min5);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Off)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Off);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Sec1)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Sec1);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Secs10)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Secs10);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Secs20)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Secs20);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Secs3)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Secs3);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Secs30)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Secs30);
		} else if (prefsv144.getUplTimeTrigger().equals(PrefsV144.UploadTimeTrigger.Secs5)) {
			protocolPrefs.setUplTimeTrigger(ProtocolPrefs.UploadTimeTrigger.Secs5);
		}
		if (prefsv144.getUplTriggerLogic().equals(PrefsV144.UploadTriggerLogic.AND)) {
			protocolPrefs.setUplTriggerLogic(ProtocolPrefs.UploadTriggerLogic.AND);
		} else if (prefsv144.getUplTriggerLogic().equals(PrefsV144.UploadTriggerLogic.OR)) {
			protocolPrefs.setUplTriggerLogic(ProtocolPrefs.UploadTriggerLogic.OR);
		}
		
		ServerPrefs serverPrefs = PrefsRegistry.get(ServerPrefs.class);
		serverPrefs.setPath(prefsv144.getPath());
		serverPrefs.setPort(prefsv144.getPort());
		serverPrefs.setServer(prefsv144.getServer());
		
		return true;
	}
}
