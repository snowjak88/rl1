/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;

/**
 * Denotes that an entity has other Entities as its occupants.
 * 
 * @author snowjak88
 *
 */
public class IsOccupied extends PooledComponent {
	
	@EntityId
	public IntBag occupiers = new IntBag();
	
	public IsOccupied() {
		
	}
	
	public IsOccupied(int entityID) {
		
		occupiers.add(entityID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		occupiers.clear();
	}
	
}
