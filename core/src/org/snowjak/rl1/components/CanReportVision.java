/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * Indicates that an entity can see other entities, and send its vision-reports
 * to a particular name.
 * 
 * @author snowjak88
 *
 */
public class CanReportVision extends PooledComponent {
	
	public String reportTo = null;
	public double acuity = 1d;
	public double maxDistance = Double.MAX_VALUE;
	
	public CanReportVision() {
		
	}
	/**
	 * @param reportTo
	 */
	public CanReportVision(String reportTo) {
		
		this(reportTo, 1d);
	}
	
	/**
	 * @param reportTo
	 */
	public CanReportVision(String reportTo, double acuity) {
		
		this(reportTo, acuity, Double.MAX_VALUE);
	}
	
	/**
	 * @param reportTo
	 * @param acuity
	 * @param maxDistance
	 */
	public CanReportVision(String reportTo, double acuity, double maxDistance) {
		
		this.reportTo = reportTo;
		this.acuity = acuity;
		this.maxDistance = maxDistance;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		this.reportTo = null;
		this.acuity = 1d;
		this.maxDistance = Double.MAX_VALUE;
	}
	
}
