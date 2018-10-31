/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * @author snowjak88
 *
 */
public class HasPosition extends PooledComponent {
	
	public float x = 0, y = 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		x = 0;
		y = 0;
	}
	
}
