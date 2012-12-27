package de.msk.mylivetracker.client.android.antplus;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.os.Bundle;

import com.wahoofitness.api.WFAntException;
import com.wahoofitness.api.WFAntNotSupportedException;
import com.wahoofitness.api.WFAntServiceNotInstalledException;
import com.wahoofitness.api.WFDisplaySettings;
import com.wahoofitness.api.WFHardwareConnector;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.status.TrackStatus;

/**
 * AntPlusHardware.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25	revised for v1.5.x.
 * 000 	2012-12-25 	initial.
 * 
 */
public class AntPlusHardware {

	private static WFHardwareConnector antPlusHwConnector = null;
	
	public static boolean supported() {
		return WFHardwareConnector.hasAntSupport(App.getCtx());
	}
	
	public static boolean initialized() {
		return antPlusHwConnector != null;
	}
	
	public static WFHardwareConnector getConn() {
		return antPlusHwConnector;
	}
	
	public static void init(Object lastAntPlusHwConnector, Bundle savedInstanceState) {
		Context context = App.getCtx();
		AntPlusManager antPlusListener = AntPlusManager.get();
		
		String statusStr = null;
		// check for ANT hardware support.
        if (supported()) {
	        try {
	        	boolean bResumed = false;
	        	// attempt to retrieve the previously suspended WFHardwareConnector instance.
	        	//
	        	// see the onRetainNonConfigurationInstance method.
	        	antPlusHwConnector = (WFHardwareConnector)lastAntPlusHwConnector;
	        	if (antPlusHwConnector != null) {
	        		// attempt to resume the WFHardwareConnector instance.
	        		if (!(bResumed = antPlusHwConnector.resume(antPlusListener))) {
	        			// if the WFHardwareConnector instance failed to resume,
	        			// it must be re-initialized.
	        			antPlusHwConnector.connectAnt();
	        		}
	        	}
	        	// if there is no suspended WFHardwareConnector instance,
	        	// configure the singleton instance.
	        	else {
			         // get the hardware connector singleton instance.
	        		antPlusHwConnector = WFHardwareConnector.getInstance(
			        	context, antPlusListener);
	        		antPlusHwConnector.connectAnt();
	        	}
		        // restore connection state only if the previous
		        // WFHardwareConnector instance was not resumed.
		        if (!bResumed) {
			        // the connection state is cached in the state
			        // bundle (onSaveInstanceState).  this is used to
			        // restore previous connections.  if the Bundle
			        // is null, no connections are configured.
		        	antPlusHwConnector.restoreInstanceState(savedInstanceState);
		        }
		        // configure the display settings.
		        //
		        // this demonstrates how to use the display
		        // settings.  if this step is skipped, the
		        // default settings will be used.
		        WFDisplaySettings settings = antPlusHwConnector.getDisplaySettings();
		        settings.staleDataTimeout = 5.0f;          // seconds, default = 5
		        settings.staleDataString = "--";           // string to display when data is stale, default = "--"
		        settings.useMetricUnits = true;            // display metric units, default = false
		        settings.bikeWheelCircumference = 2.07f;   // meters, default = 2.07
		        settings.bikeCoastingTimeout = 3.0f;       // seconds, default = 3    
		        antPlusHwConnector.setDisplaySettings(settings);
	        } catch (WFAntNotSupportedException nse) {
	        	// ANT hardware not supported.
	        	statusStr = context.getString(R.string.antPlus_NotSupported);
	        } catch (WFAntServiceNotInstalledException nie) {
	        	statusStr = context.getString(R.string.antPlus_NotInstalled);
	        } catch (WFAntException e) {
	        	statusStr = context.getString(R.string.antPlus_InitError);
			} catch (Exception e) {
				statusStr = context.getString(R.string.antPlus_InitError);
			}
        } else {
        	// ANT hardware not supported.
        	statusStr = context.getString(R.string.antPlus_NotSupported);
        }  
        if (!StringUtils.isEmpty(statusStr)) {
        	TrackStatus.get().setAntPlusStatus(statusStr);
        	antPlusHwConnector = null;
        }                      
	}
}
