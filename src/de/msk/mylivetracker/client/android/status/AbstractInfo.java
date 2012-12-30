package de.msk.mylivetracker.client.android.status;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: AbstractInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
		T infoObj = null;
		String infoVar = infoClass.getSimpleName();
		String infoStr = prefs.getString(infoVar, null);
		if (!StringUtils.isEmpty(infoStr)) {
			try {
				infoObj = gson.fromJson(infoStr, infoClass);
			} catch (Exception e) {
				LogUtils.info(AbstractInfo.class, "parsing " + infoVar + "-string failed: " + infoStr);
				LogUtils.info(AbstractInfo.class, "parsing " + infoVar + "-string failed: " + e.toString());
			}
		}
		LogUtils.info(AbstractInfo.class, infoVar + " loaded: " + 
			((infoObj != null) ? infoObj.toString() : "<empty>"));
		return infoObj;
	}
	
	public static <T extends AbstractInfo> void save(SharedPreferences.Editor editor, Gson gson, Class<T> infoClass, AbstractInfo infoObj) {
		if (editor == null) {
			throw new IllegalArgumentException("editor must not be null.");
		}
		if (gson == null) {
			throw new IllegalArgumentException("gson must not be null.");
		}
		if (infoClass == null) {
			throw new IllegalArgumentException("infoClass must not be null.");
		}
		String infoVar = infoClass.getSimpleName();
		String infoStr = (infoObj != null) ? gson.toJson(infoObj) : null;
		editor.putString(infoVar, infoStr);
		LogUtils.info(AbstractInfo.class, infoVar + " saved: " + 
			(!StringUtils.isEmpty(infoStr) ? infoStr : "<empty>"));
	}
}
