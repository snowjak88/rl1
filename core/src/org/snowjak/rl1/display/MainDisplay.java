/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.display.screen.AsciiScreen;
import org.snowjak.rl1.display.screen.AsciiScreen.Region;

/**
 * @author snowjak88
 *
 */
public class MainDisplay extends AbstractDisplay {
	
	private MapScrollingDisplay mapDisplay;
	private AbstractDisplay statusDisplay;
	
	/**
	 * @param screen
	 * @param title
	 * @param bordered
	 */
	public MainDisplay() {
		
		super(new AsciiScreen(Context.get().appConfig().getFont(), true), null, false, null, true);
		
		mapDisplay = new MapScrollingDisplay(Context.get().mapGenerator().generate(0, 0),
				getScreen().createSubscreen(Region.LEFT_70)) {
			
			@Override
			public void drawAfterMap(AsciiScreen screen) {
				
			}
		};
		
		statusDisplay = new AbstractDisplay(getScreen().createSubscreen(Region.RIGHT_30, false), "Status", true,
				BorderType.BLOCK, false) {
			
			@Override
			public void drawContent(AsciiScreen screen) {
				
				screen.color();
				screen.clear();
				screen.cursor(2, 2);
				screen.write("Map:");
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
	
	public MapScrollingDisplay getMapDisplay() {
		
		return mapDisplay;
	}
	
}
