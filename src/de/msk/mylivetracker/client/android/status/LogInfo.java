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
	private static final String TRKPT_TEMPLATE = "<trkpt lat=\"$LAT\" lon=\"$LON\"><time>$TIME</time></trkpt>";
	private static final String TIMESTAMP_FMT = "yyyy-MM-dd'T'hh:mm:ss.SSS";
	
	public static String createGpxFileLogItemsToGpxFile() {
		String gpxFileName = null;
		FileOutputStream fos = null;
		try {
			gpxFileName = DateTime.getCurrentAsUtcStr(DateTime.INTERNAL_DATE_TIME_FMT);
			gpxFileName += "_" + Preferences.get().getTrackName() + ".gpx";
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
		return gpxFileName;
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
				fos.write(gpxHead.getBytes());
			}
			
			String trkpt = TRKPT_TEMPLATE;
			trkpt = StringUtils.replace(trkpt, "$LAT", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLatitude(), 8));
			trkpt = StringUtils.replace(trkpt, "$LON", 
				FormatUtils.getDoubleAsSimpleStr(locationInfo.getLongitude(), 8));
			DateTime dateTime = new DateTime(locationInfo.getTimestamp().getTime());
			String timestamp = dateTime.getAsStr(
				TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
				TIMESTAMP_FMT);
			trkpt = StringUtils.replace(trkpt, "$TIME", timestamp);
			fos.write(trkpt.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.closeStream(fos);
		}
	}
}
