package de.msk.mylivetracker.client.android.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * classname: StatusDeSerializer
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
