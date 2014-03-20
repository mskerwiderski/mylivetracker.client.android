package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.App;

import android.content.Context;

/**
 * classname: ARemoteCmdDsc
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-28	origin.
 * 
 */
public abstract class ARemoteCmdDsc {

	public static final String OPT_SEP = "|";
	
	private String name;
	private String paramDsc;
	private int minParams;
	private int maxParams;
	private int descriptionId;
	private boolean needsAppRunning;
	
	public ARemoteCmdDsc(String name, String paramDsc, 
		int minParams, int maxParams, int descriptionId) {
		this.init(name, paramDsc, minParams, maxParams, 
			descriptionId, true);
	}
	
	public ARemoteCmdDsc(String name, String paramDsc, 
		int minParams, int maxParams, int descriptionId,
		boolean needsAppRunning) {
		this.init(name, paramDsc, minParams, maxParams, 
			descriptionId, needsAppRunning);
	}
	
	private void init(String name, String paramDsc, 
		int minParams, int maxParams, int descriptionId,
		boolean needsAppRunning) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name must not be empty.");
		}
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if ((minParams < 0) || (maxParams < 0)) {
			throw new IllegalArgumentException("minParams and maxParams must not be less than or equals 0.");
		}
		if (maxParams < minParams) {
			throw new IllegalArgumentException("maxParams must not be less than minParams.");
		}		
		if (descriptionId <= 0) {
			throw new IllegalArgumentException("descriptionId not valid.");
		}
		this.name = name;
		this.paramDsc = paramDsc;
		this.minParams = minParams;
		this.maxParams = maxParams;
		this.descriptionId = descriptionId;
		this.needsAppRunning = needsAppRunning;
	}
	
	public abstract boolean matchesSyntax(String[] params);
	
	public String getSyntax() {
		String syntax = this.name;
		if (!StringUtils.isEmpty(this.paramDsc)) {
			syntax += " " + this.paramDsc;
		}
		return syntax;
	}
	
	public String getDescription() {
		String dsc = this.getSyntax();
		String dscStr = App.getCtx().getString(this.descriptionId);
		if (!StringUtils.isEmpty(dscStr)) {
			dsc += " - " + dscStr;
		}
		return dsc;
	}
	
	public static <E extends Enum<E>> String createSyntaxStr(Class<E> options) {	
		String syntax = "";
		List<E> enumList = EnumUtils.getEnumList(options);
		for (Enum<?> e : enumList) {
			if (!StringUtils.isEmpty(syntax)) {
				syntax += OPT_SEP;
			}
			syntax += e.name();
		}
		return syntax;
	}
	
	public String getName() {
		return name;
	}
	public String getParamDsc() {
		return paramDsc;
	}
	public int getMinParams() {
		return minParams;
	}
	public int getMaxParams() {
		return maxParams;
	}
	public int getDescriptionId() {
		return descriptionId;
	}
	public String getDescriptionStr(Context ctx) {
		String str = ctx.getString(this.descriptionId);
		if (StringUtils.isEmpty(str)) {
			throw new IllegalStateException("description string not found.");
		}
		return str;
	}

	public boolean isNeedsAppRunning() {
		return needsAppRunning;
	}

	@Override
	public String toString() {
		return "ARemoteCmdDsc [name=" + name + ", paramDsc=" + paramDsc
			+ ", minParams=" + minParams + ", maxParams=" + maxParams
			+ ", descriptionId=" + descriptionId + ", needsAppRunning="
			+ needsAppRunning + "]";
	}
}
