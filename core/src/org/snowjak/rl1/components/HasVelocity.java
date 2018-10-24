/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * Indicates that an entity has velocity, and that its position
 * ({@link HasPosition}) should be updated accordingly.
 * 
 * @author snowjak88
 *
 */
public class HasVelocity extends PooledComponent {
	
	public double dx = 0, dy = 0;
	
	/**
	 * 
	 */
	public HasVelocity() {
		
	}
	
	/**
	 * @param dx
	 * @param dy
	 */
	public HasVelocity(double dx, double dy) {
		
		this.dx = dx;
		this.dy = dy;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		this.dx = 0;
		this.dy = 0;
	}
	
}
