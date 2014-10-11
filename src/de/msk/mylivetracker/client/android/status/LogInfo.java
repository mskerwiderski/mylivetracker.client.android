package de.msk.mylivetracker.client.android.status;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackexport.TrackExportPrefs;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;
import de.msk.mylivetracker.client.android.util.FormatUtils;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: LogInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LogInfo {

	private static final String GPX_HEAD_TEMPLATE = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
	    "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" " +
	    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
	    "version=\"1.1\" creator=\"$MLTVERSION\">" +
	    "<metadata><author><name>$AUTHOR</name></author>" +
	    "<time>$TIMESTAMPSTART</time></metadata>" +
	    "<trk><name>$TRACKNAME</name><cmt>$COMMENT</cmt><trkseg>";
	private static final String GPX_FOOTER_TEMPLATE = "</trkseg></trk></gpx>";
	@SuppressWarnings("unused")
	private static final String WPT_TEMPLATE = "<wpt lat=\"$LAT\" lon=\"$LON\">$ATTR</wpt>";
	private static final String TRKPT_TEMPLATE = "<trkpt lat=\"$LAT\" lon=\"$LON\">$ATTR</trkpt>";
	private static final String ATTR_NAME_TEMPLATE = "<name>$NAME</name>";
	private static final String ATTR_TIME_TEMPLATE = "<time>$TIME</time>";
	private static final String ATTR_ALTITUDE_TEMPLATE = "<ele>$ALTITUDE</ele>";
	private static final String TIMESTAMP_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final String TIMESTAMP_FMT_FOR_FILENAME = "'UTC'yyyy-MM-dd'T'HH-mm-ss-SSS'Z'";
	
	public static String createGpxFileNameOfCurrentTrack() {
		TrackExportPrefs trackExportPrefs = PrefsRegistry.get(TrackExportPrefs.class);
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		
		String gpxFileName = trackExportPrefs.getFilenamePrefix(); 
		if (trackExportPrefs.isFilenameAppendTrackName() &&
			!StringUtils.isEmpty(accountPrefs.getTrackName())) {
			gpxFileName += "_" + accountPrefs.getTrackName();
		}
		if (trackExportPrefs.isFilenameAppendTimestampOfFirstPosition()) {
			DateTime trackStartTime = new DateTime(
				TrackStatus.get().getMarkerFirstPositionReceived());
			gpxFileName += "_" + trackStartTime.getAsStr(
				TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
				TIMESTAMP_FMT_FOR_FILENAME);
		}
		if (trackExportPrefs.isFilenameAppendTimestampOfLastPosition()) {
			DateTime trackStartTime = new DateTime(
				TrackStatus.get().getMarkerLastPositionReceived());
			gpxFileName += "_" + trackStartTime.getAsStr(
				TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
				TIMESTAMP_FMT_FOR_FILENAME);
		}
		if (trackExportPrefs.isFilenameAppendSequenceNumber()) {
			gpxFileName += "_" + 
				StringUtils.leftPad(String.valueOf(
					trackExportPrefs.getFilenameNextSequenceNumber()), 6, '0');
			trackExportPrefs.incNextSequenceNumber();
			PrefsRegistry.save(TrackExportPrefs.class);
		}
		gpxFileName += ".gpx";
		return gpxFileName;
	}
	
	public static void createGpxFileOfCurrentTrack(String gpxFileName) {
		FileOutputStream fos = null;
		try {
			String srcFileName = TrackStatus.get().getLogFileName();
			App.get().deleteFile(gpxFileName);
			FileUtils.copy(srcFileName, PathType.AppDataDir, 
				gpxFileName, PathType.AppDataDir);
			fos = App.get().openFileOutput(
				gpxFileName, Context.MODE_APPEND | Context.MODE_PRIVATE);
			fos.write(GPX_FOOTER_TEMPLATE.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.closeStream(fos);
		}
	}
	
	public static void exportGpxFileOfCurrentTrackToExternalStorage(String gpxFileName) {
		FileUtils.fileExists(gpxFileName, PathType.ExternalStorage);
		createGpxFileOfCurrentTrack(gpxFileName);
		FileUtils.copy(gpxFileName, PathType.AppDataDir, 
			gpxFileName, PathType.ExternalStorage);
	}
	
	public static boolean logFileExists() {
		String filename = TrackStatus.get().getLogFileName();
		return FileUtils.fileExists(filename, PathType.AppDataDir);
	}
	
	public static void addLogItem(LocationInfo locationInfo, 
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo) {
		if (!TrackStatus.get().trackDataExists() || 
			(locationInfo == null) || !locationInfo.hasValidLatLon()) return;
		FileOutputStream fos = null;
		try {
			String filename = TrackStatus.get().getLogFileName();
			boolean writeGpxHead = !FileUtils.fileExists(filename, PathType.AppDataDir);
			fos = App.get().openFileOutput(filename, 
				Context.MODE_PRIVATE | Context.MODE_APPEND);
			if (writeGpxHead) {
				String gpxHead = GPX_HEAD_TEMPLATE;
				gpxHead = StringUtils.replace(gpxHead, "$MLTVERSION", App.getAppNameComplete());
				gpxHead = StringUtils.replace(gpxHead, "$AUTHOR", PrefsRegistry.get(AccountPrefs.class).getUsername());
				//TODO check what happens if track is not running?
				DateTime dateTime = new DateTime(TrackStatus.get().getStartedInMSecs());
				String timestamp = dateTime.getAsStr(
					TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
					TIMESTAMP_FMT);
				gpxHead = StringUtils.replace(gpxHead, "$TIMESTAMPSTART", timestamp);
				gpxHead = StringUtils.replace(gpxHead, "$TRACKNAME", PrefsRegistry.get(AccountPrefs.class).getTrackName());
				gpxHead = StringUtils.replace(gpxHead, "$COMMENT", "");
				fos.write(gpxHead.getBytes());
			}
//			if ((emergencySignalInfo != null) || (messageInfo != null)) {
//				String name = (emergencySignalInfo != null) ? 
//					"SOS" : messageInfo.getMessage(); 
//				String wpt = createGpxPoint(WPT_TEMPLATE, name, locationInfo);
//				fos.write(wpt.getBytes());
//			}
			String trkpt = createGpxPoint(TRKPT_TEMPLATE, 
				locationInfo, emergencySignalInfo, messageInfo);
			fos.write(trkpt.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.closeStream(fos);
		}
	}
	
	private static String createGpxPoint(String template, LocationInfo locationInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo) {
		String pointStr = template;
		String latStr = FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 8);
		String lonStr = FormatUtils.getDoubleAsSimpleStr(locationInfo.getLongitude(), 8);
		DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
		String timestamp = dateTime.getAsStr(
			TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
			TIMESTAMP_FMT);
		String alt = null;
		if (locationInfo.hasValidAltitude()) {
			alt = FormatUtils.getDoubleAsSimpleStr(locationInfo.getAltitudeInMtr(), 2);
		}
		pointStr = StringUtils.replace(pointStr, "$LAT", latStr); 
		pointStr = StringUtils.replace(pointStr, "$LON", lonStr); 
		String attr = "";
		String name = null;
		if (emergencySignalInfo != null) {
			name = "SOS";
		} else if (messageInfo != null) {
			name = messageInfo.getMessage();
		}
		if (!StringUtils.isEmpty(name)) {
			attr += StringUtils.replace(ATTR_NAME_TEMPLATE, "$NAME", name);
		}
		if (!StringUtils.isEmpty(alt)) {
			attr += StringUtils.replace(ATTR_ALTITUDE_TEMPLATE, "$ALTITUDE", alt);
		}
		attr += StringUtils.replace(ATTR_TIME_TEMPLATE, "$TIME", timestamp);
		pointStr = StringUtils.replace(pointStr, "$ATTR", attr);
		return pointStr;
	}
}
