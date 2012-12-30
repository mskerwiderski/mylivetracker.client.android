package de.msk.mylivetracker.client.android.util.listener;

import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: ASafeOnClickListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class ASafeOnClickListener implements OnClickListener {

	private static long DEF_WAITING_PERIOD_IN_MSECS = 1000;
	
	private long waitingPeriodInMSecs = DEF_WAITING_PERIOD_IN_MSECS;
	private long lastEventProcessedInMSecs = 0;
	
	public ASafeOnClickListener() {
	}
	
	public ASafeOnClickListener(long waitingPeriodInMSecs) {
		if (waitingPeriodInMSecs < 0) {
			throw new IllegalArgumentException("waitingPeriodInMSecs must be equal or greater than 0!");
		}
		this.waitingPeriodInMSecs = waitingPeriodInMSecs;
	}

	@Override
	public void onClick(View v) {
		long currTimeAsMSecs = (new DateTime()).getAsMSecs();
		if ((currTimeAsMSecs - waitingPeriodInMSecs) < 
			this.lastEventProcessedInMSecs) {
			// waiting period from last processed event not yet expired.
		} else {
			this.lastEventProcessedInMSecs = currTimeAsMSecs;
			this.onClick();
		}
	}
	
	public abstract void onClick();
}
