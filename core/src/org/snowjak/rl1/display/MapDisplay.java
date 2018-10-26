/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.map.Map;
import org.snowjak.rl1.screen.AsciiScreen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

/**
 * @author snowjak88
 *
 */
public class MapDisplay extends AbstractDisplay {
	
	public static final int MAP_DISPLAY_INPUT_PRIORITY = 16;
	
	private final MapDisplayInputProcessor inputProcessor;
	private final Map map;
	
	/**
	 * The MapDisplay is currently scrolled across the {@link Map} to this X-point.
	 */
	public int scrollX = 0;
	/**
	 * The MapDisplay is currently scrolled across the {@link Map} to this Y-point.
	 */
	public int scrollY = 0;
	
	/**
	 * The MapDisplay will scroll every frame in the X-direction by this amount.
	 */
	private int continuousScrollX = 0;
	/**
	 * The MapDisplay will scroll every frame in the Y-direction by this amount.
	 */
	private int continuousScrollY = 0;
	
	/**
	 * @param mainScreen
	 */
	public MapDisplay(Map map, AsciiScreen screen) {
		
		super(screen, null, true, BorderType.SINGLE_LINE);
		this.inputProcessor = new MapDisplayInputProcessor(this);
		this.map = map;
		
		Context.get().in().addProcessor(MAP_DISPLAY_INPUT_PRIORITY, inputProcessor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.snowjak.rl1.display.AbstractDisplay#drawContent(org.snowjak.rl1.drawing.
	 * ascii.AsciiScreen)
	 */
	@Override
	public void drawContent(AsciiScreen screen) {
		
		scroll(continuousScrollX, continuousScrollY);
		
		final int halfWidth = screen.getWidthInChars() / 2;
		final int halfHeight = screen.getHeightInChars() / 2;
		
		for (int x = 0; x < screen.getWidthInChars(); x++)
			for (int y = 0; y < screen.getHeightInChars(); y++) {
				
				final float height = (float) map.getHeightFrac(x + scrollX - halfWidth, y + scrollY - halfHeight);
				screen.foreground(new Color(height, height, height, 1));
				screen.put((char) 219, x, y);
				screen.foreground();
				
			}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.display.AbstractDisplay#dispose()
	 */
	@Override
	public void dispose() {
		
		super.dispose();
		Context.get().in().removeProcessor(inputProcessor);
	}
	
	public void scroll(int dx, int dy) {
		
		scrollX += dx;
		scrollY += dy;
	}
	
	public static class MapDisplayInputProcessor extends InputAdapter {
		
		private final MapDisplay map;
		
		private boolean isShiftActive = false;
		
		/**
		 * @param map
		 */
		public MapDisplayInputProcessor(MapDisplay map) {
			
			this.map = map;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.badlogic.gdx.InputAdapter#keyDown(int)
		 */
		@Override
		public boolean keyDown(int keycode) {
			
			switch (keycode) {
			case Keys.SHIFT_LEFT:
			case Keys.SHIFT_RIGHT:
				isShiftActive = true;
				return true;
			case Keys.LEFT:
				map.continuousScrollX = -scrollAmount();
				return true;
			case Keys.RIGHT:
				map.continuousScrollX = +scrollAmount();
				return true;
			case Keys.UP:
				map.continuousScrollY = -scrollAmount();
				return true;
			case Keys.DOWN:
				map.continuousScrollY = +scrollAmount();
				return true;
			}
			
			return false;
		}
		
		private int scrollAmount() {
			
			return (isShiftActive) ? 10 : 1;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.badlogic.gdx.InputAdapter#keyUp(int)
		 */
		@Override
		public boolean keyUp(int keycode) {
			
			switch (keycode) {
			case Keys.SHIFT_LEFT:
			case Keys.SHIFT_RIGHT:
				isShiftActive = false;
				return true;
			case Keys.LEFT:
				map.continuousScrollX = 0;
				return true;
			case Keys.RIGHT:
				map.continuousScrollX = 0;
				return true;
			case Keys.UP:
				map.continuousScrollY = 0;
				return true;
			case Keys.DOWN:
				map.continuousScrollY = 0;
				return true;
			}
			
			return false;
		}
		
	}
}
