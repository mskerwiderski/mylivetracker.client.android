package de.msk.mylivetracker.client.android.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * StatusDeSerializer.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 001 2012-11-19 initial.
 * 
 */
public class StatusDeSerializer {

	private static Gson gson = null;
	public static Gson get() {
		if (gson != null) return gson;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.create();
		return gson;
	}
}
