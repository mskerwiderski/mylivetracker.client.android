package de.msk.mylivetracker.client.android.util.google;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: GoogleUrlShortener
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.8.0
 * 
 * history:
 * 000	2014-10-18	origin.
 * 
 */
public class GoogleUrlShortener {
	
	private GoogleUrlShortener() {
	}
	
	public static String getShortUrl(String longUrl) {
		LogUtils.infoMethodIn(GoogleUrlShortener.class, "getShortUrl", longUrl);
		if (StringUtils.isEmpty(longUrl)) {
			throw new IllegalArgumentException("longUrl must not be null.");
		}
		String shortUrl = longUrl;
		InputStream is = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(
            	GoogleUtils.getGoogleShortenerUrl() + "?key=" + 
    			GoogleUtils.getGoogleApiKey() + "&fields=id");
            httpPost.setEntity(new StringEntity("{\"longUrl\" : \"" + longUrl+ "\"}"));
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            shortUrl = (new JSONObject(sb.toString())).getString("id");
        } catch (Exception e) {
        	LogUtils.infoMethodOut(GoogleUrlShortener.class,
        		"getShortUrl", "url shortening failed", e);
        	shortUrl = longUrl;
        } finally {
        	try {
        		is.close();
        	} catch (Exception e) {
        	}
        }
        LogUtils.infoMethodOut(GoogleUrlShortener.class, "getShortUrl", longUrl);
        return shortUrl;
    }
}
