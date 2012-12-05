package de.msk.mylivetracker.client.android.dropbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.google.gson.Gson;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.StatusDeSerializer;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * DropboxUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-12-05 initial. 
 * 
 */
public class DropboxUtils {

	final static private String DROPBOX_APP_KEY = "a73z7br0b53qrbj";
	final static private String DROPBOX_APP_SECRET = "vk6i3bpo6uv1ra6";
	final static private String DROPBOX_ACCESS_KEY = "lv3ci59kvgal7ow";
	final static private String DROPBOX_ACCESS_SECRET = "l4s9n2lzxa4581z";
	final static private AccessType DROPBOX_ACCESS_TYPE = AccessType.APP_FOLDER;
	final static private String FILENAME_TEMPLATE = "AppReport_#DATETIME_#DEVICEID.txt";
	final static private String LINE_SEP = "\r\n";

	private static AppKeyPair appKeys = new AppKeyPair(
		DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
	private static AccessTokenPair accessTokenPair = new AccessTokenPair(
		DROPBOX_ACCESS_KEY, DROPBOX_ACCESS_SECRET);
	
	private static class UploadReportToSupportProgressDialog<T extends Activity> extends AbstractProgressDialog<T> {
		@Override
		public void beforeTask(T activity) {
		}
		@Override
		public void doTask(T activity) {
			uploadReportToSupportTask();
		}
		@Override
		public void cleanUp(T activity) {
		}
	}
	public static void uploadReportToSupport(Activity activity) {
		UploadReportToSupportProgressDialog<Activity> dlg = 
			new UploadReportToSupportProgressDialog<Activity>();
		dlg.run(activity);
	}
	private static void uploadReportToSupportTask() {
		String filename = FILENAME_TEMPLATE;
		filename = StringUtils.replace(filename, "#DATETIME", 
			DateTime.getCurrentAsUtcStr(DateTime.INTERNAL_DATE_TIME_FMT)); 
		filename = StringUtils.replace(filename, "#DEVICEID", App.getDeviceId());
		
		AndroidAuthSession session = new AndroidAuthSession(appKeys, DROPBOX_ACCESS_TYPE);
		DropboxAPI<AndroidAuthSession> dropboxApi = new DropboxAPI<AndroidAuthSession>(session);
		
		dropboxApi.getSession().setAccessTokenPair(accessTokenPair);
		
		// create report
		Gson gson = StatusDeSerializer.get();
		String report = 
			"Preferences:" + LINE_SEP + gson.toJson(Preferences.get()) + LINE_SEP +
			"TrackStatus:" + LINE_SEP + gson.toJson(TrackStatus.get()) + LINE_SEP +
			"LocationInfo:" + LINE_SEP + gson.toJson(LocationInfo.get() + LINE_SEP);
		
		ByteArrayInputStream byteArrayInputStream = 
			new ByteArrayInputStream(report.getBytes());
		
		try {
		    @SuppressWarnings("unused")
			Entry newEntry = dropboxApi.putFile(
				"/" + filename, byteArrayInputStream,
				report.length(), null, null);
		} catch (DropboxUnlinkedException e) {
			throw new RuntimeException(e);
		} catch (DropboxException e) {
			throw new RuntimeException(e);
		} finally {
		    if (byteArrayInputStream != null) {
		        try {
		        	byteArrayInputStream.close();
		        } catch (IOException e) {}
		    }
		}
	}
}
