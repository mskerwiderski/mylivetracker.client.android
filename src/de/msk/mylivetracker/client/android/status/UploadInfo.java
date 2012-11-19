package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;
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
 * 001 2012-02-04 lastUsedLocationProvider added.
 * 000 2011-08-26 initial.
 * 
 */
public class UploadInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = 3875399789159467945L;
	private static UploadInfo uploadInfo = null;
	public static void update(Boolean status, String resultCode,	
		Integer positionsUploaded, Long uploadTimeInMSecs,
		String lastUsedLocationProvider) {
		uploadInfo = createNewUploadInfo(uploadInfo, 
			status, resultCode, positionsUploaded, 
			uploadTimeInMSecs, lastUsedLocationProvider);
	}
	public static UploadInfo get() {
		return uploadInfo;
	}
	public static void set(UploadInfo uploadInfo) {
		UploadInfo.uploadInfo = uploadInfo;
	}
	public static void reset() {
		uploadInfo = null;
	}

	private Boolean status = null;
	private String resultCode = null;	
	private Integer countUploaded = null;
	private Long avgUploadTimeInMSecs = null;
	private Long realIntervalInMSecs = null;
	private String lastUsedLocationProvider = null;
	
	private UploadInfo() {
	}
	
	private UploadInfo(Boolean status, String resultCode, Integer countUploaded,
		Long avgUploadTimeInMSecs, Long realIntervalInMSecs,
		String lastUsedLocationProvider) {
		this.status = status;
		this.resultCode = resultCode;
		this.countUploaded = countUploaded;
		this.avgUploadTimeInMSecs = avgUploadTimeInMSecs;
		this.realIntervalInMSecs = realIntervalInMSecs;
		this.lastUsedLocationProvider = lastUsedLocationProvider;
	}
	
	private static UploadInfo createNewUploadInfo(
		UploadInfo currUploadInfo,	
		Boolean status, String resultCode,	
		Integer positionsUploaded, Long uploadTimeInMSecs,
		String lastUsedLocationProvider) {		
			
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
		
		Long realIntervalInMSecs = null;
		if (countUploaded >= 2) {
			Long runtimeInMSecs = TrackStatus.get().getRuntimeInMSecs(false);
			realIntervalInMSecs = runtimeInMSecs / (countUploaded - 1);
		} else if ((currUploadInfo != null) && (currUploadInfo.realIntervalInMSecs != null)) {
			realIntervalInMSecs = currUploadInfo.realIntervalInMSecs;
		}
		
		return new UploadInfo(status, resultCode, countUploaded,
			avgUploadTimeInMSecs, realIntervalInMSecs, 
			lastUsedLocationProvider);
	}
	
	public boolean isSuccess() {
		if ((this.status == null) || !this.status || (this.getTimestamp() == null)) return false;
		int periodOfRestInSecs = Preferences.get().getUplTimeTrigger().getSecs();
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
	
	public Boolean getStatus() {
		return status;
	}
	public String getResultCode() {
		return resultCode;
	}
	public Integer getCountUploaded() {
		return countUploaded;
	}
	public Long getAvgUploadTimeInMSecs() {
		return avgUploadTimeInMSecs;
	}
	public Long getRealIntervalInMSecs() {
		return realIntervalInMSecs;
	}
	public String getLastUsedLocationProvider() {
		return lastUsedLocationProvider;
	}
	@Override
	public String toString() {
		return "UploadInfo [status=" + status + ", resultCode=" + resultCode
			+ ", countUploaded=" + countUploaded
			+ ", avgUploadTimeInMSecs=" + avgUploadTimeInMSecs
			+ ", realIntervalInMSecs=" + realIntervalInMSecs
			+ ", lastUsedLocationProvider=" + lastUsedLocationProvider
			+ "]";
	}
	
	
}
