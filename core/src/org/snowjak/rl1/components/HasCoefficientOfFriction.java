/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * @author snowjak88
 *
 */
public class HasCoefficientOfFriction extends PooledComponent {
	
	public float cF = 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		cF = 0;
	}
	
}
