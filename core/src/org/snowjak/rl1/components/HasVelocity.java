/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * @author snowjak88
 *
 */
public class HasVelocity extends PooledComponent {
	
	public float dx = 0, dy = 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		dx = 0;
		dy = 0;
	}
}
