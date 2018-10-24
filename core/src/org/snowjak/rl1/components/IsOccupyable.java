/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;

/**
 * Denotes that an entity can be occupied by one or more other entities.
 * <p>
 * By default, an IsOccupyable entity can have between <code> [0, {@link
 * Integer#MAX_VALUE}]</code> occupiers.
 * </p>
 * 
 * @author snowjak88
 *
 */
public class IsOccupyable extends PooledComponent {
	
	public int minOccupiers = 0, maxOccupiers = Integer.MAX_VALUE;
	
	/**
	 * 
	 */
	public IsOccupyable() {
		
	}
	
	/**
	 * @param minOccupiers
	 * @param maxOccupiers
	 */
	public IsOccupyable(int minOccupiers, int maxOccupiers) {
		
		this.minOccupiers = minOccupiers;
		this.maxOccupiers = maxOccupiers;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		this.minOccupiers = 0;
		this.maxOccupiers = Integer.MAX_VALUE;
	}
	
}
