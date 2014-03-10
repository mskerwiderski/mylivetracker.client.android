package de.msk.mylivetracker.client.android.remoteaccess;

import java.io.FileOutputStream;
import java.util.TimeZone;

import android.content.Context;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.dropbox.DropboxPrefs;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils.UploadFileResult;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;
import de.msk.mylivetracker.commons.util.datetime.DateTime;


/**
 * classname: SmsCmdUploadConfig
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-06	origin.
 * 
 */
public class SmsCmdUploadConfig extends ASmsCmdExecutor {

	public static String NAME = "uplconfig";
	public static String SYNTAX = "";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 0);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
		
	}
	
	public SmsCmdUploadConfig(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	public static String createFileNameOfConfigFileDump() {
		String fileName = "MLT_CFG_" + 
			App.getDeviceId() + "_"; 
		DateTime dateTime = new DateTime();
		fileName += "_" + dateTime.getAsStr(
			TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
			"'UTC'yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
		fileName += ".txt";
		return fileName;
	}
	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String result = "";
		if (!PrefsRegistry.get(DropboxPrefs.class).hasValidAccountAndToken()) {
			result = ResponseCreator.getResultOfNotConnectedToDropbox();
		} else {
			try {
				String filename = createFileNameOfConfigFileDump();
				if (FileUtils.fileExists(filename, PathType.AppDataDir)) {
					FileUtils.fileDelete(filename, PathType.AppDataDir);
				}
				FileOutputStream fos = App.get().openFileOutput(filename, 
					Context.MODE_PRIVATE | Context.MODE_APPEND);
				String dump = PrefsDumper.getDump();
				fos.write(dump.getBytes());
				
				UploadFileResult uploadFileResult = 
					DropboxUtils.uploadFile(filename);
				result = ResponseCreator.getResultOfUploadFile(uploadFileResult);
				FileUtils.fileDelete(filename, PathType.AppDataDir);
			} catch (Exception e) {
				result = ResponseCreator.getResultOfError(e.getMessage());
			}
		}
		return result;
	}
}
