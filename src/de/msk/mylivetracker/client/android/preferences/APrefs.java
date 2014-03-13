package de.msk.mylivetracker.client.android.preferences;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;


/**
 * classname: APrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class APrefs implements IPrefs {
	protected void onSave() {
		// noop.
	}
	
	public String getPrefsDumpAsStr(boolean oneLine) {
		PrefsDump prefsDump = this.getPrefsDump();
		String res = prefsDump.name +
			" (version " + this.getVersion() + ")";
		
		if (oneLine) {
			res += ":";
			if (prefsDump.configPairs == null) {
				res += PrefsDumper.EMPTY;
			} else {
				for (ConfigPair configPair : prefsDump.configPairs) {
					res += configPair.param+ "=" + 
						(!StringUtils.isEmpty(configPair.value) ? 
							configPair.value : PrefsDumper.NOT_SET) + 
						", ";
				}
				res = StringUtils.chop(res);
			}
		} else {
			res += PrefsDumper.LINE_SEP + 
				PrefsDumper.getSimpleLine(res.length()) + 
				PrefsDumper.LINE_SEP;
			if (prefsDump.configPairs == null) {
				res += PrefsDumper.EMPTY + PrefsDumper.LINE_SEP;
			} else {
				for (ConfigPair configPair : prefsDump.configPairs) {
					res += ">: " + 
						configPair.param + " = " + PrefsDumper.QUOTE + 
						(!StringUtils.isEmpty(configPair.value) ? 
							configPair.value : PrefsDumper.NOT_SET) + 
						PrefsDumper.QUOTE + PrefsDumper.LINE_SEP;
				}
			}
		}
		return res;
	}
}
