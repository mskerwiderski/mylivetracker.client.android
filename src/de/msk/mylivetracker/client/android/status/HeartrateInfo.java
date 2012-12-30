package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;
import java.util.Date;

import com.wahoofitness.api.data.WFHeartrateData;

/**
 * classname: HeartrateInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class HeartrateInfo extends AbstractInfo implements Serializable {	
	private static final long serialVersionUID = -4613427339180995270L;
	private static HeartrateInfo heartrateInfo = null;
	public static void update(WFHeartrateData heartrateData) {
		heartrateInfo = 
			HeartrateInfo.createNewHeartrateInfo(heartrateInfo, heartrateData);
	}
	public static HeartrateInfo get() {
		return heartrateInfo;
	}
	public static void set(HeartrateInfo heartrateInfo) {
		HeartrateInfo.heartrateInfo = heartrateInfo;
	}
	public static void reset() {
		heartrateInfo = null;
	}
	
	private Long hrInBpm = null;
	private Long beatCnt = null;
	private Long measureCnt = 0L;
	private Long hrBeatsSum = 0L;
	private Long hrMinInBpm = null;
	private Long hrMaxInBpm = null;
	private Long hrAvgInBpm = null;
	
	private HeartrateInfo() {
	}
	
	private HeartrateInfo(Long hrInBpm, String hrInBpmStr, 
		Long beatCnt, Long measureCnt,
		Long hrBeatsSum, Long hrMinInBpm, 
		Long hrMaxInBpm, Long hrAvgInBpm) {
		this.hrInBpm = hrInBpm;
		this.beatCnt = beatCnt;
		this.measureCnt = measureCnt;
		this.hrBeatsSum = hrBeatsSum;
		this.hrMinInBpm = hrMinInBpm;
		this.hrMaxInBpm = hrMaxInBpm;
		this.hrAvgInBpm = hrAvgInBpm;
	}
	
	public static HeartrateInfo createNewHeartrateInfo(
		HeartrateInfo currHeartrateInfo, WFHeartrateData heartrateData) {
	
		HeartrateInfo newHeartrateInfo = currHeartrateInfo;
		
		Long hrInBpm = null;
		Long beatCnt = -1L;
		Long measureCnt = (currHeartrateInfo != null) ? currHeartrateInfo.measureCnt : 0L;
		Long hrBeatsSum = (currHeartrateInfo != null) ? currHeartrateInfo.hrBeatsSum : 0L;
		Long hrMinInBpm = (currHeartrateInfo != null) ? currHeartrateInfo.hrMinInBpm : null;
		Long hrMaxInBpm = (currHeartrateInfo != null) ? currHeartrateInfo.hrMaxInBpm : null;
		Long hrAvgInBpm = (currHeartrateInfo != null) ? currHeartrateInfo.hrAvgInBpm : null;
		
		if ((heartrateData.accumBeatCount > beatCnt) &&
			(heartrateData.computedHeartrate > 0)) {
			hrInBpm = Long.valueOf(heartrateData.computedHeartrate);
			beatCnt = heartrateData.accumBeatCount;
			if (TrackStatus.get().trackIsRunning()) {			
				hrBeatsSum += hrInBpm;
				// calc min.
				if (hrMinInBpm == null) {
					hrMinInBpm = hrInBpm;
				} else {
					hrMinInBpm = Math.min(hrMinInBpm, hrInBpm);
				}
				// calc max.
				if (hrMaxInBpm == null) {
					hrMaxInBpm = hrInBpm;
				} else {
					hrMaxInBpm = Math.max(hrMaxInBpm, hrInBpm);
				}				
				measureCnt++;
				hrAvgInBpm = 
					hrBeatsSum / measureCnt;
			}
			newHeartrateInfo = new HeartrateInfo(
				hrInBpm,
				heartrateData.getFormattedHeartrate(false),
				beatCnt, measureCnt,
				hrBeatsSum, hrMinInBpm, 
				hrMaxInBpm, hrAvgInBpm);
		}
		
		return newHeartrateInfo;
	}
	
	public boolean isUpToDate() {
		if ((this.hrInBpm == null) || (this.getTimestamp() == null)) return false;
		int periodOfRestInSecs = 5;
		int lastHrDetectedInSecs = (int)(((new Date()).getTime() - this.getTimestamp().getTime()) / 1000);
		return lastHrDetectedInSecs <= periodOfRestInSecs;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HeartrateInfo [hrInBpm=").append(hrInBpm)
			.append(", beatCnt=").append(beatCnt).append(", measureCnt=")
			.append(measureCnt).append(", hrBeatsSum=").append(hrBeatsSum)
			.append(", hrMinInBpm=").append(hrMinInBpm)
			.append(", hrMaxInBpm=").append(hrMaxInBpm)
			.append(", hrAvgInBpm=").append(hrAvgInBpm).append("]");
		return builder.toString();
	}
	
	public Long getHrInBpm() {
		return hrInBpm;
	}

	public Long getBeatCnt() {
		return beatCnt;
	}

	public Long getMeasureCnt() {
		return measureCnt;
	}

	public Long getHrBeatsSum() {
		return hrBeatsSum;
	}

	public Long getHrMinInBpm() {
		return hrMinInBpm;
	}

	public Long getHrMaxInBpm() {
		return hrMaxInBpm;
	}

	public Long getHrAvgInBpm() {
		return hrAvgInBpm;
	}	
}
