/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * Denotes that an entity is associated with a certain position in the world.
 * 
 * @author snowjak88
 *
 */
public class HasPosition extends PooledComponent {
	
	public int x, y;
	
	public HasPosition() {
		
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public HasPosition(int x, int y) {
		
		this.x = x;
		this.y = y;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		this.x = 0;
		this.y = 0;
	}
	
}
