package de.msk.mylivetracker.client.android.remoteaccess.commands;

import java.io.FileOutputStream;
import java.util.TimeZone;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.dropbox.DropboxPrefs;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils.UploadFileResult;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.LogInfo;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: RemoteCmdUpload
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-06	origin.
 * 
 */
public class RemoteCmdUpload extends ARemoteCmdExecutor {

	public static final String NAME = "upl";
	public static enum Options {
		track, cfg;
	}
	public static final String SYNTAX = 
		CmdDsc.createSyntaxStr(Options.class);
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, SYNTAX, 1, 1, 
				R.string.txRemoteCommand_Upload, false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 1) &&
				EnumUtils.isValidEnum(
					Options.class, params[0]);
		}
	}
	
	private Result uploadTrack() {
		boolean success = true;
		String response;
		if (!LogInfo.logFileExists()) {
			success = false;
			response = "no track file exists";
		} else {
			String gpxFileName = LogInfo.createGpxFileNameOfCurrentTrack();
			LogInfo.createGpxFileOfCurrentTrack(gpxFileName);
			UploadFileResult uploadFileResult = DropboxUtils.uploadFile(gpxFileName);
			Result result = ResponseCreator.getResultOfUploadFile(uploadFileResult);
			success = result.isSuccess();
			response = result.getResponse();
			FileUtils.fileDelete(gpxFileName, PathType.AppDataDir);
		}
		return new Result(success, response);
	}
	
	private Result uploadConfig() {
		boolean success = true;
		String response;
		String filename = "MLT_CFG_" + 
				App.getDeviceId() + "_"; 
		DateTime dateTime = new DateTime();
		filename += "_" + dateTime.getAsStr(
			TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
			"'UTC'yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
		filename += ".txt";
		if (FileUtils.fileExists(filename, PathType.AppDataDir)) {
			FileUtils.fileDelete(filename, PathType.AppDataDir);
		}
		try {
			FileOutputStream fos = App.get().openFileOutput(filename, 
				Context.MODE_PRIVATE | Context.MODE_APPEND);
			String dump = PrefsDumper.getDump();
			fos.write(dump.getBytes());
			
			UploadFileResult uploadFileResult = 
				DropboxUtils.uploadFile(filename);
			Result result = ResponseCreator.getResultOfUploadFile(uploadFileResult);
			success = result.isSuccess();
			response = result.getResponse();
		} catch (Exception e) {
			success = false;
			response = "upload was not successful";
		} finally {
			FileUtils.fileDelete(filename, PathType.AppDataDir);
		}
		return new Result(success, response);
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		Result result = null;
		if (!PrefsRegistry.get(DropboxPrefs.class).hasValidAccountAndToken()) {
			result = new Result(false, 
				ResponseCreator.getResultOfNotConnectedToDropbox());
		} else if (StringUtils.equals(params[0], Options.track.name())) {
			result = this.uploadTrack();
		} else if (StringUtils.equals(params[0], Options.cfg.name())) {
			result = this.uploadConfig();
		}
		return result;
	}
}
