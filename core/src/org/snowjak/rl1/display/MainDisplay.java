/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.map.Map;
import org.snowjak.rl1.screen.AsciiScreen;

/**
 * @author snowjak88
 *
 */
public class MainDisplay extends AbstractDisplay {
	
	private MapDisplay mapDisplay;
	private AbstractDisplay statusDisplay;
	
	/**
	 * @param screen
	 * @param title
	 * @param bordered
	 */
	public MainDisplay() {
		
		super(new AsciiScreen(Context.get().appConfig().getScreenWidth(), Context.get().appConfig().getScreenHeight(),
				Context.get().appConfig().getFont()), null, false, null);
		
		final int mapWidth = Math.max(1, getScreen().getWidthInChars() - 32);
		
		mapDisplay = new MapDisplay(new Map(Context.get().appConfig()),
				getScreen().getRegion(0, 0, mapWidth - 1, getScreen().getHeightInChars() - 1));
		
		statusDisplay = new AbstractDisplay(getScreen().getRegion(mapWidth, 0, getScreen().getWidthInChars() - 1,
				getScreen().getHeightInChars() - 1), "Status", true, BorderType.DOUBLE_LINE) {
			
			@Override
			public void drawContent(AsciiScreen screen) {
				
				screen.color();
				screen.clear();
				screen.cursor(2, 2);
				screen.write("Map scroll:");
				screen.cursor(2, 3);
				screen.write("    X: " + mapDisplay.scrollX);
				screen.cursor(2, 4);
				screen.write("    Y: " + mapDisplay.scrollY);
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.snowjak.rl1.display.AbstractDisplay#drawContent(org.snowjak.rl1.screen.
	 * AsciiScreen)
	 */
	@Override
	public void drawContent(AsciiScreen screen) {
		
		mapDisplay.draw();
		statusDisplay.draw();
	}
	
	public MapDisplay getMapDisplay() {
		
		return mapDisplay;
	}
	
}
