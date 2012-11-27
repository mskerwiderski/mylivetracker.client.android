package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.status.LocationInfo;

/**
 * SmsCmdGetLocation.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public class SmsCmdGetLocation extends AbstractSmsCmdExecutor {

	public SmsCmdGetLocation(String cmdName, String sender, String... params) {
		super(cmdName, sender, params);
	}

	@Override
	public CmdDsc getCmdDsc() {
		return new CmdDsc("[detect [timeout]]", 0, 2);
	}

	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String response = "no valid location found.";
		LocationInfo locationInfo = LocationInfo.get();
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			response = ResponseCreator.getLocationInfoValues(locationInfo);
		}
		return response;
	}
}
