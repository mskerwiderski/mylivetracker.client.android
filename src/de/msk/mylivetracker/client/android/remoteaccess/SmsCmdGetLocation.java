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
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-11-23 	initial.
 * 
 */
public class SmsCmdGetLocation extends ASmsCmdExecutor {

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
