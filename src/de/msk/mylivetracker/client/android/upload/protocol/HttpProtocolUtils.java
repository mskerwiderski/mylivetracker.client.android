package de.msk.mylivetracker.client.android.upload.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.HttpProtocolParamDsc;

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

	public static String addParamTrueFalse(String paramsStr, HttpProtocolParamDsc paramDsc, Boolean paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		return addParam(paramsStr, paramDsc, paramValue, "true", "false");
	}
	
	public static String addParamOnOff(String paramsStr, HttpProtocolParamDsc paramDsc, Boolean paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		return addParam(paramsStr, paramDsc, paramValue, "on", "off");
	}
	
	public static String addParam(String paramsStr, HttpProtocolParamDsc paramDsc, Boolean paramValue, String trueStr, String falseStr) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		if (trueStr == null) {
			throw new IllegalArgumentException("trueStr must not be null.");
		}
		if (falseStr == null) {
			throw new IllegalArgumentException("falseStr must not be null.");
		}
		return addParam(paramsStr, paramDsc, 
			BooleanUtils.toString(paramValue.booleanValue(), trueStr, falseStr));
	}
	
	public static String addParam(String paramsStr, HttpProtocolParamDsc paramDsc, Float paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		return addParam(paramsStr, paramDsc, String.valueOf(paramValue));
	}
	
	public static String addParam(String paramsStr, HttpProtocolParamDsc paramDsc, Double paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		return addParam(paramsStr, paramDsc, String.valueOf(paramValue));
	}
	
	public static String addParam(String paramsStr, HttpProtocolParamDsc paramDsc, Integer paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		return addParam(paramsStr, paramDsc, String.valueOf(paramValue));
	}
	
	public static String addParam(String paramsStr, HttpProtocolParamDsc paramDsc, Long paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		return addParam(paramsStr, paramDsc, String.valueOf(paramValue));
	}
	
	public static String addParam(String paramsStr, HttpProtocolParamDsc paramDsc, String paramValue) {
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		if (paramDsc.isEnabled() && !StringUtils.isEmpty(paramValue)) {
			paramsStr = addParam(paramsStr, paramDsc.getName(), paramValue);
		}
		return paramsStr;
	}
	
	public static String addParam(String paramsStr, String paramName, String paramValue) {
		if (StringUtils.isEmpty(paramName)) {
			throw new IllegalArgumentException("paramName must not be empty.");
		}
		if (paramValue == null) {
			throw new IllegalArgumentException("paramValue must not be null.");
		}
		if (!StringUtils.isEmpty(paramValue)) {
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
		}
		return paramsStr;
	}
}
