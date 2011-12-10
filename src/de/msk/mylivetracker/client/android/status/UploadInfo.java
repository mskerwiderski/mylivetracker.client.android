package de.msk.mylivetracker.client.android.status;

import java.util.Date;

import de.msk.mylivetracker.client.android.preferences.Preferences;


/**
 * UploadInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-26
 * 
 */
public class UploadInfo extends AbstractInfo {

	private static UploadInfo uploadInfo = null;
	public static void update(Boolean status, String resultCode,	
		Integer positionsUploaded, Long uploadTimeInMSecs) {
		uploadInfo = createNewUploadInfo(uploadInfo, 
			status, resultCode, positionsUploaded, uploadTimeInMSecs);
	}
	public static UploadInfo get() {
		return uploadInfo;
	}
	public static void reset() {
		uploadInfo = null;
	}
	
	private Boolean status = null;
	private String resultCode = null;	
	private Integer countUploaded = null;
	private Long avgUploadTimeInMSecs = null;
	
	private UploadInfo(Boolean status, String resultCode, Integer countUploaded,
		Long avgUploadTimeInMSecs) {
		this.status = status;
		this.resultCode = resultCode;
		this.countUploaded = countUploaded;
		this.avgUploadTimeInMSecs = avgUploadTimeInMSecs;
	}
	
	public static UploadInfo createNewUploadInfo(
		UploadInfo currUploadInfo,	
		Boolean status, String resultCode,	
		Integer positionsUploaded, Long uploadTimeInMSecs) {		
			
		Integer countUploaded = positionsUploaded;
		if ((currUploadInfo != null) && (currUploadInfo.countUploaded != null)) {
			countUploaded += currUploadInfo.countUploaded;
		}
			
		Long avgUploadTimeInMSecs = null;
		if (positionsUploaded > 0) {
			avgUploadTimeInMSecs = uploadTimeInMSecs;
			if ((currUploadInfo != null) && (currUploadInfo.avgUploadTimeInMSecs != null)) {
				avgUploadTimeInMSecs = 
					((currUploadInfo.avgUploadTimeInMSecs * currUploadInfo.countUploaded) + 
					uploadTimeInMSecs) / countUploaded;
			}
		} else if ((currUploadInfo != null) && (currUploadInfo.avgUploadTimeInMSecs != null)) {
			avgUploadTimeInMSecs = currUploadInfo.avgUploadTimeInMSecs;
		}
		
		return new UploadInfo(status, resultCode, countUploaded, avgUploadTimeInMSecs);
	}
	
	public boolean isSuccess() {
		if ((this.status == null) || !this.status || (this.getTimestamp() == null)) return false;
		int periodOfRestInSecs = Preferences.get().getUplTimeTriggerInSeconds();
		if (periodOfRestInSecs == 0) {
			periodOfRestInSecs = 5;
		} else {
			int addon = Math.round(periodOfRestInSecs * 1.1f);
			if (addon < 5) {
				addon = 5;
			}
			periodOfRestInSecs += addon;
		} 
		int lastSuccUplDetectedInSecs = (int)(((new Date()).getTime() - this.getTimestamp().getTime()) / 1000);
		return lastSuccUplDetectedInSecs <= periodOfRestInSecs;
	}
	
	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}
	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}
	/**
	 * @return the countUploaded
	 */
	public Integer getCountUploaded() {
		return countUploaded;
	}
	/**
	 * @return the avgUploadTimeInMSecs
	 */
	public Long getAvgUploadTimeInMSecs() {
		return avgUploadTimeInMSecs;
	}
}
