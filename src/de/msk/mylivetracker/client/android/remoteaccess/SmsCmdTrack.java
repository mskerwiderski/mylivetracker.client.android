package de.msk.mylivetracker.client.android.remoteaccess;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.TrackUtils;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: SmsCmdTrack
 * 
 * @author michael skerwiderski, (c)2012
 * @version 001
 * @since 1.5.0
 * 
 * history:
 * 001	2014-02-28	revised for v1.6.0.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class SmsCmdTrack extends ASmsCmdExecutor {

	public static String NAME = "track";
	public static String SYNTAX = "reset|start|stop|info";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 1, 1);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = (params.length == 1);
			matches = 
				StringUtils.equals(params[0], "reset") ||
				StringUtils.equals(params[0], "start") ||
				StringUtils.equals(params[0], "stop") ||
				StringUtils.equals(params[0], "info");
			return matches;
		}
		
	}
	
	public SmsCmdTrack(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		LogUtils.infoMethodIn(this.getClass(), "executeCmdAndCreateSmsResponse", Arrays.toString(params));
		String cmd = params[0];
		String response = "'" + cmd + "' not supported."; 
		if (StringUtils.equals(cmd, "reset")) {
			TrackUtils.resetTrack();
			response = "track resetted.";
		} else if (StringUtils.equals(cmd, "start")) {
			boolean started = TrackUtils.startTrack();
			response = (started ? "track started." : "track already running.");
		} else if (StringUtils.equals(cmd, "stop")) {
			boolean stopped = TrackUtils.stopTrack();
			response = (stopped ? "track stopped." : "track not running.");
		}  else if (StringUtils.equals(cmd, "info")) {
			TrackStatus status = TrackStatus.get();
			response = "track is " + (status.trackIsRunning() ? "running, " : "not running, ");
			DecimalFormat decimalFmt = 
				new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
			Float distance = status.getTrackDistanceInMtr();
			response += "distance (km)=" + 
				((distance == null) ? 
					"0.00km, " : 
					decimalFmt.format(status.getTrackDistanceInMtr()/1000d) + ", ");
			response += "uploaded positions=" +
				((UploadInfo.get() == null) ? 
					"0, " : UploadInfo.get().getCountUploaded() + ", ");
			response += "last location update=";
			LocationInfo locationInfo = LocationInfo.get();
			if (locationInfo == null) {
				response += "unknown.";
			} else {
				DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
				response += dateTime.getAsStr(
					TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
					DateTime.INTERNAL_DATE_TIME_FMT) + ".";
			}
		}
		
		LogUtils.infoMethodOut(this.getClass(), "executeCmdAndCreateSmsResponse", response);
		return response;
	}
}
