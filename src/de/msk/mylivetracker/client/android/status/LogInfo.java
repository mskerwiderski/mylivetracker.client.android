package de.msk.mylivetracker.client.android.status;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FormatUtils;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

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
	private static final String TRKPT_TEMPLATE = "<trkpt lat=\"$LAT\" lon=\"$LON\">$ATTR</trkpt>";
	private static final String ATTR_TIME_TEMPLATE = "<time>$TIME<time>";
	private static final String ATTR_ALTITUDE_TEMPLATE = "<geoidheight>$ALTITUDE<geoidheight>";
	private static final String TIMESTAMP_FMT = "yyyy-MM-dd'T'hh:mm:ss.SSS";
	
	public static String createGpxFileNameOfCurrentTrack() {
		String gpxFileName = DateTime.getCurrentAsUtcStr(DateTime.INTERNAL_DATE_TIME_FMT);
		gpxFileName += "_" + TrackStatus.get().getTrackId() + ".gpx";
		return gpxFileName;
	}
	
	public static void createGpxFileOfCurrentTrack(String gpxFileName) {
		FileOutputStream fos = null;
		try {
			String srcFileName = TrackStatus.get().getLogFileName();
			App.get().deleteFile(gpxFileName);
			FileUtils.copy(srcFileName, gpxFileName);
			fos = App.get().openFileOutput(
				gpxFileName, Context.MODE_APPEND | Context.MODE_PRIVATE);
			fos.write(GPX_FOOTER_TEMPLATE.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.closeStream(fos);
		}
	}
	
	public static boolean logFileExists() {
		String filename = TrackStatus.get().getLogFileName();
		return FileUtils.fileExists(filename);
	}
	
	public static void addLogItem(LocationInfo locationInfo) {
		if ((locationInfo == null) || !locationInfo.hasValidLatLon()) return;
		FileOutputStream fos = null;
		try {
			String filename = TrackStatus.get().getLogFileName();
			boolean writeGpxHead = !FileUtils.fileExists(filename);
			fos = App.get().openFileOutput(filename, 
				Context.MODE_PRIVATE | Context.MODE_APPEND);
			if (writeGpxHead) {
				String gpxHead = GPX_HEAD_TEMPLATE;
				gpxHead = StringUtils.replace(gpxHead, "$MLTVERSION", App.getAppNameComplete());
				gpxHead = StringUtils.replace(gpxHead, "$AUTHOR", Preferences.get().getUsername());
				DateTime dateTime = new DateTime(TrackStatus.get().getStartedInMSecs());
				String timestamp = dateTime.getAsStr(
					TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
					TIMESTAMP_FMT);
				gpxHead = StringUtils.replace(gpxHead, "$TIMESTAMPSTART", timestamp);
				gpxHead = StringUtils.replace(gpxHead, "$TRACKNAME", Preferences.get().getTrackName());
				gpxHead = StringUtils.replace(gpxHead, "$COMMENT", "");
				fos.write(gpxHead.getBytes());
			}
			String trkpt = TRKPT_TEMPLATE;
			trkpt = StringUtils.replace(trkpt, "$LAT", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 8));
			trkpt = StringUtils.replace(trkpt, "$LON", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLongitude(), 8));
			String attr = "";
			DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
			String timestamp = dateTime.getAsStr(
				TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
				TIMESTAMP_FMT);
			attr += StringUtils.replace(ATTR_TIME_TEMPLATE, "$TIME", timestamp);
			if (locationInfo.hasValidAltitude()) {
				attr += StringUtils.replace(ATTR_ALTITUDE_TEMPLATE, "$ALTITUDE", 
					FormatUtils.getDoubleAsSimpleStr(locationInfo.getAltitudeInMtr(), 2));
			}
			trkpt = StringUtils.replace(trkpt, "$ATTR", attr);
			fos.write(trkpt.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.closeStream(fos);
		}
	}
}
