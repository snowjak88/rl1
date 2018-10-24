/**
 * 
 */
package org.snowjak.rl1.events;

import net.mostlyoriginal.api.event.common.Event;

/**
 * @author snowjak88
 *
 */
public class VisionEvent implements Event {
	
	public final int seer, seen;
	
	/**
	 * @param seer
	 * @param seen
	 */
	public VisionEvent(int seer, int seen) {
		
		this.seer = seer;
		this.seen = seen;
	}
	
}
