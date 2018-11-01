/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.util.listener.Event;
import org.snowjak.rl1.util.listener.Listenable;

import com.badlogic.gdx.InputAdapter;

/**
 * Provides input-handling to AbstractDisplay to handle common events.
 * 
 * @author snowjak88
 *
 */
public abstract class DisplayInputProcessor extends InputAdapter
		implements Listenable<DisplayInputProcessor.DisplayEvent> {
	
	private final int priority;
	private final EventRouter eventRouter = new EventRouter();
	
	/**
	 * @param display
	 * @param priority
	 */
	public DisplayInputProcessor(int priority) {
		
		this.priority = priority;
	}
	
	/**
	 * @return the priority
	 */
	public int getPriority() {
		
		return priority;
	}
	
	public interface DisplayEvent extends Event {
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.util.Listenable#getEventRouter()
	 */
	@Override
	public EventRouter getEventRouter() {
		
		return eventRouter;
	}
}