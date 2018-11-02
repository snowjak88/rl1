/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.components.IsDrawable;
import org.snowjak.rl1.display.screen.AsciiScreen;
import org.snowjak.rl1.drawable.Drawable;
import org.snowjak.rl1.map.MapChunk;
import org.snowjak.rl1.util.IntPair;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Color;

/**
 * @author snowjak88
 *
 */
public class EntityRenderingMapScrollingDisplay extends MapScrollingDisplay {
	
	private ComponentMapper<IsDrawable> isDrawable;
	
	/**
	 * @param mapChunk
	 * @param screen
	 */
	public EntityRenderingMapScrollingDisplay(MapChunk mapChunk, AsciiScreen screen) {
		
		super(mapChunk, screen);
		
		isDrawable = Context.get().odb().getMapper(IsDrawable.class);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.snowjak.rl1.display.MapScrollingDisplay#drawAfterMap(org.snowjak.rl1.
	 * display.screen.AsciiScreen)
	 */
	@Override
	public void drawAfterMap(AsciiScreen screen) {
		
		for (int screenX = 0; screenX < screen.getWidthInChars(); screenX++)
			for (int screenY = 0; screenY < screen.getHeightInChars(); screenY++) {
				
				final IntPair mapPoint = new IntPair(getMapX(screenX), getMapY(screenY));
				
				if (!getMapChunk().getEntitiesAt(mapPoint).isEmpty()) {
					for (int entityID : getMapChunk().getEntitiesAt(mapPoint).getData()) {
						if (!isDrawable.has(entityID))
							continue;
						final Drawable d = isDrawable.get(entityID).getDrawable();
						final char[][] c = d.getRepresentation(
								Math.abs(79 * mapPoint.hashCode()) % d.getCountRepresentations());
						final Color color = d
								.getColor(Math.abs(37 * entityID + mapPoint.hashCode()) % d.getCountColors());
						
						screen.foreground(color);
						screen.put(c[0][0], screenX, screenY);
					}
				}
			}
	}
	
}
