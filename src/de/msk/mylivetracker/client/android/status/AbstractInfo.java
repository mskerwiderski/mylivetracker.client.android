package de.msk.mylivetracker.client.android.status;

import java.util.Date;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * AbstractInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public abstract class AbstractInfo {
	
	private Date timestamp = new Date();

	public long getId() {
		return this.timestamp.getTime();
	}

	public boolean isUpToDate(AbstractInfo info) {
		if (info == null) return true;
		return this.getId() > info.getId();
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public static <T extends AbstractInfo> T load(SharedPreferences prefs, Gson gson, Class<T> infoClass) {
		if (prefs == null) {
			throw new IllegalArgumentException("prefs must not be null.");
		}
		if (gson == null) {
			throw new IllegalArgumentException("gson must not be null.");
		}
		if (infoClass == null) {
			throw new IllegalArgumentException("infoClass must not be null.");
		}
		String infoVar = infoClass.getSimpleName();
		String infoStr = prefs.getString(infoVar, null);
		T infoObj = gson.fromJson(infoStr, infoClass);
		LogUtils.info(AbstractInfo.class, infoVar + " loaded: " + infoObj.toString());
		return infoObj;
	}
	
	public static void save(SharedPreferences.Editor editor, Gson gson, AbstractInfo infoObj) {
		if (editor == null) {
			throw new IllegalArgumentException("editor must not be null.");
		}
		if (gson == null) {
			throw new IllegalArgumentException("gson must not be null.");
		}
		if (infoObj != null) {
			String infoVar = infoObj.getClass().getSimpleName();
			String infoStr = gson.toJson(infoObj);
			editor.putString(infoVar, infoStr);
			LogUtils.info(AbstractInfo.class, infoVar + " saved: " + infoStr);
		}
	}
}
