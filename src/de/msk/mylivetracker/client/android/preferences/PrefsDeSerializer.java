package de.msk.mylivetracker.client.android.preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * PrefsDeSerializer.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 001 2012-12-01 initial.
 * 
 */
public class PrefsDeSerializer {
	private static Gson gson = null;
	public static Gson get() {
		if (gson != null) return gson;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.create();
		return gson;
	}
}
