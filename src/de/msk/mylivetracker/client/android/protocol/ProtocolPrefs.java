package de.msk.mylivetracker.client.android.protocol;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * classname: ProtocolPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ProtocolPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = -8803446433815331163L;

	public static final int VERSION = 1;
	
	public enum TransferProtocol {
		uploadDisabled("Upload disabled", false, false),
		mltTcpEncrypted("MLT TCP (encrypted)", true, true),
		tk102Emulator("Tk102 Emulator", false, true),
		tk5000Emulator("Tk5000 Emulator", false, true),
		httpUserDefined("HTTP (user defined)", true, true); 
		
		private String dsc;
		private boolean supportsSendMessage;
		private boolean supportsSendEmergencySignal;
		
		private TransferProtocol(String dsc,
			boolean supportsSendMessage,
			boolean supportsSendEmergencySignal) {
			this.dsc = dsc;
			this.supportsSendMessage = supportsSendMessage;
			this.supportsSendEmergencySignal = supportsSendEmergencySignal;
		}
		public String getDsc() {
			return dsc;
		}
		public boolean supportsSendMessage() {
			return this.supportsSendMessage;
		}
		public boolean supportsSendEmergencySignal() {
			return this.supportsSendEmergencySignal;
		}
	};
	
	public enum UploadTimeTrigger {
		Off("off", 0),
		Sec1("1 sec", 1),
		Secs3("3 secs", 3),
		Secs5("5 secs", 5),
		Secs10("10 secs", 10),
		Secs20("20 secs", 20),
		Secs30("30 secs", 30),
		Min1("1 min", 60),
		Min2("2 min", 120),
		Min3("3 mins", 180),
		Min5("5 mins", 300),
		Min10("10 mins", 600),
		Min20("20 mins", 1200),
		Min30("30 mins", 1800),
		Hr1("1 hr", 3600);
        
		private String dsc;
		private int secs;
		
		private UploadTimeTrigger(String dsc, int secs) {
			this.dsc = dsc;
			this.secs = secs;
		}
		public String getDsc() {
			return dsc;
		}
		public int getSecs() {
			return secs;
		}
	};
	
	public enum UploadTriggerLogic {
		OR("OR", false),
		AND("AND", true);
        
		private String dsc;
		private boolean logic;
		
		private UploadTriggerLogic(String dsc, boolean logic) {
			this.dsc = dsc;
			this.logic = logic;
		}
		public String getDsc() {
			return dsc;
		}
		public boolean OR() {
			return logic == false;
		}
		public boolean AND() {
			return logic == true;
		}
	};
	
	public enum UploadDistanceTrigger {
		Off("off", 0),
		Mtr50("50 mtrs", 50),
		Mtr100("100 mtrs", 100),
		Mtr200("200 mtrs", 200),
		Mtr300("300 mtrs", 300),
		Mtr500("500 mtrs", 500),
		Km1("1 km", 1000),
		Km2("2 kms", 2000),
		Km5("5 kms", 5000),
		Km10("10 kms", 10000),
		Km50("50 kms", 50000);
        
		private String dsc;
		private int mtrs;
		
		private UploadDistanceTrigger(String dsc, int mtrs) {
			this.dsc = dsc;
			this.mtrs = mtrs;
		}
		public String getDsc() {
			return dsc;
		}
		public int getMtrs() {
			return mtrs;
		}
		public static UploadDistanceTrigger findSuitable(int mtrs) {
			if (mtrs == 0) return Off;
			if (mtrs <= 50) return Mtr50;
			if (mtrs <= 100) return Mtr100;
			if (mtrs <= 200) return Mtr200;
			if (mtrs <= 300) return Mtr300;
			if (mtrs <= 500) return Mtr500;
			if (mtrs <= 1000) return Km1;
			if (mtrs <= 2000) return Km2;
			if (mtrs <= 5000) return Km5;
			if (mtrs <= 50000) return Km10;
			return Km50;
		}
	};
	
	public enum BufferSize {
		disabled(0), pos3(3), pos5(5), pos10(10);	
		private Integer size;				
		private BufferSize(Integer size) {
			this.size = size;
		}
		public Integer getSize() {
			return size;
		}
		public boolean isDisabled() {
			return size == 0;
		}
	}
	
	private TransferProtocol transferProtocol;
	private boolean closeConnectionAfterEveryUpload;
	private boolean finishEveryUploadWithALinefeed;
	private String lineSeparator;
	private UploadTimeTrigger uplTimeTrigger;
	private UploadTriggerLogic uplTriggerLogic;
	private UploadDistanceTrigger uplDistanceTrigger;
	private BufferSize uplPositionBufferSize;
	private boolean logTrackData;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.transferProtocol = TransferProtocol.uploadDisabled;
		this.closeConnectionAfterEveryUpload = false;
		this.finishEveryUploadWithALinefeed = false;
		this.lineSeparator = "\r\n";
		this.uplTimeTrigger = UploadTimeTrigger.Secs10;
		this.uplTriggerLogic = UploadTriggerLogic.OR;
		this.uplDistanceTrigger = UploadDistanceTrigger.Off;
		this.uplPositionBufferSize = BufferSize.disabled;
		this.logTrackData = true;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}

	public TransferProtocol getTransferProtocol() {
		return transferProtocol;
	}
	public void setTransferProtocol(TransferProtocol transferProtocol) {
		this.transferProtocol = transferProtocol;
	}
	public boolean isCloseConnectionAfterEveryUpload() {
		return closeConnectionAfterEveryUpload;
	}
	public void setCloseConnectionAfterEveryUpload(
		boolean closeConnectionAfterEveryUpload) {
		this.closeConnectionAfterEveryUpload = closeConnectionAfterEveryUpload;
	}
	public boolean isFinishEveryUploadWithALinefeed() {
		return finishEveryUploadWithALinefeed;
	}
	public void setFinishEveryUploadWithALinefeed(
		boolean finishEveryUploadWithALinefeed) {
		this.finishEveryUploadWithALinefeed = finishEveryUploadWithALinefeed;
	}
	public String getLineSeparator() {
		return lineSeparator;
	}
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	public UploadTimeTrigger getUplTimeTrigger() {
		return uplTimeTrigger;
	}
	public void setUplTimeTrigger(UploadTimeTrigger uplTimeTrigger) {
		this.uplTimeTrigger = uplTimeTrigger;
	}
	public UploadTriggerLogic getUplTriggerLogic() {
		return uplTriggerLogic;
	}
	public void setUplTriggerLogic(UploadTriggerLogic uplTriggerLogic) {
		this.uplTriggerLogic = uplTriggerLogic;
	}
	public UploadDistanceTrigger getUplDistanceTrigger() {
		return uplDistanceTrigger;
	}
	public void setUplDistanceTrigger(UploadDistanceTrigger uplDistanceTrigger) {
		this.uplDistanceTrigger = uplDistanceTrigger;
	}
	public BufferSize getUplPositionBufferSize() {
		return uplPositionBufferSize;
	}
	public void setUplPositionBufferSize(BufferSize uplPositionBufferSize) {
		this.uplPositionBufferSize = uplPositionBufferSize;
	}
	public boolean isLogTrackData() {
		return logTrackData;
	}
	public void setLogTrackData(boolean logTrackData) {
		this.logTrackData = logTrackData;
	}

	@Override
	public String toString() {
		return "ProtocolPrefs [transferProtocol=" + transferProtocol
			+ ", closeConnectionAfterEveryUpload="
			+ closeConnectionAfterEveryUpload
			+ ", finishEveryUploadWithALinefeed="
			+ finishEveryUploadWithALinefeed + ", lineSeparator="
			+ lineSeparator + ", uplTimeTrigger=" + uplTimeTrigger
			+ ", uplTriggerLogic=" + uplTriggerLogic
			+ ", uplDistanceTrigger=" + uplDistanceTrigger
			+ ", uplPositionBufferSize=" + uplPositionBufferSize
			+ ", logTrackData=" + logTrackData + "]";
	}
}
