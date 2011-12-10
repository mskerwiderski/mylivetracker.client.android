package de.msk.mylivetracker.client.android.upload.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

/**
 * HttpProtocolUtils.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public abstract class HttpProtocolUtils {

	public static String addParam(String paramsStr, String paramName, String paramValue) {
		try {
			if (StringUtils.isEmpty(paramsStr)) {
				paramsStr = "?";
			} else {
				paramsStr += "&";
			}		
			paramsStr += 
				URLEncoder.encode(paramName, "UTF-8") + "=" +
				URLEncoder.encode(paramValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// skip param.
		} 
		return paramsStr;
	}
}
