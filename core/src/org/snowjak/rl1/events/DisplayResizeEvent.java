/**
 * 
 */
package org.snowjak.rl1.events;

import net.mostlyoriginal.api.event.common.Event;

/**
 * @author snowjak88
 *
 */
public class DisplayResizeEvent implements Event {
	
	public final int xSize, ySize;
	
	/**
	 * @param xSize
	 * @param ySize
	 */
	public DisplayResizeEvent(int xSize, int ySize) {
		
		this.xSize = xSize;
		this.ySize = ySize;
	}
	
}
