/**
 * 
 */
package org.snowjak.rl1.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

/**
 * Denotes that an entity is working on a particular goal.
 * 
 * @author snowjak88
 *
 */
public class HasGoal extends PooledComponent {
	
	/**
	 * This goal's parent. The parent goal, if present, is more abstract than the
	 * child goal.
	 */
	public HasGoal parent = null;
	
	/**
	 * Goal-name being worked on.
	 */
	public String name = null;
	/**
	 * Target of this goal.
	 */
	@EntityId
	public int target = -1;
	
	public HasGoal() {
		
	}
	
	/**
	 * @param name
	 */
	public HasGoal(String name) {
		
		this(name, -1);
	}
	
	/**
	 * @param name
	 * @param target
	 */
	public HasGoal(String name, int target) {
		
		this(null, name, target);
	}
	
	/**
	 * @param parent
	 * @param name
	 * @param target
	 */
	public HasGoal(HasGoal parent, String name, int target) {
		
		this.parent = parent;
		this.name = name;
		this.target = target;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		this.parent = null;
		this.name = null;
		this.target = -1;
	}
	
}
