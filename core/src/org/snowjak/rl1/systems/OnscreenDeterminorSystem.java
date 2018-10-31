/**
 * 
 */
package org.snowjak.rl1.systems;

import org.snowjak.rl1.components.HasPosition;
import org.snowjak.rl1.components.IsOnscreen;
import org.snowjak.rl1.display.MapScrollingDisplay;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

/**
 * Determines if an entity that {@link HasPosition} is also {@link IsOnscreen}.
 * 
 * @author snowjak88
 *
 */
@Wire
public class OnscreenDeterminorSystem extends IteratingSystem {
	
	private MapScrollingDisplay display;
	
	private M<HasPosition> hasPosition;
	private M<IsOnscreen> isOnScreen;
	
	/**
	 * @param aspect
	 */
	public OnscreenDeterminorSystem(MapScrollingDisplay display) {
		
		super(Aspect.all(HasPosition.class));
		this.display = display;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.systems.IteratingSystem#process(int)
	 */
	@Override
	protected void process(int entityId) {
		
		final HasPosition pos = hasPosition.get(entityId);
		
		final int screenX = display.getScreenX((int) pos.x), screenY = display.getScreenY((int) pos.y);
		isOnScreen.set(entityId, display.isOnScreen(screenX, screenY));
	}
	
}
