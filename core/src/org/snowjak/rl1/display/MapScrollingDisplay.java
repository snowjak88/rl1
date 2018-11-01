/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.display.MapScrollingDisplay.MapScrollingDisplayInputProcessor.StartScrollingEvent;
import org.snowjak.rl1.display.MapScrollingDisplay.MapScrollingDisplayInputProcessor.StopScrollingEvent;
import org.snowjak.rl1.map.MapChunk;
import org.snowjak.rl1.map.MapGenerator;
import org.snowjak.rl1.screen.AsciiScreen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

/**
 * Presents a window onto a {@link MapChunk}. Handles input required to scroll
 * the MapGenerator back and forth.
 * 
 * @author snowjak88
 *
 */
public abstract class MapScrollingDisplay extends AbstractDisplay implements Scrollable {
	
	private final MapScrollingDisplayInputProcessor inputProcessor = new MapScrollingDisplayInputProcessor();
	private final MapChunk mapChunk;
	
	/**
	 * The MapDisplay is currently scrolled across the {@link MapGenerator} to this
	 * X-point.
	 */
	public int scrollX = 0;
	/**
	 * The MapDisplay is currently scrolled across the {@link MapGenerator} to this
	 * Y-point.
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
	
	public MapScrollingDisplay(MapChunk mapChunk, AsciiScreen screen) {
		
		super(screen, null, false, BorderType.SINGLE_LINE, false);
		this.mapChunk = mapChunk;
		
		Context.get().in().addProcessor(inputProcessor.getPriority(), inputProcessor);
		inputProcessor.addListener(this, "handleStartScroll");
		inputProcessor.addListener(this, "handleStopScroll");
	}
	
	public void handleStartScroll(StartScrollingEvent e) {
		
		final int scrollAmount = (e.shiftEnabled) ? 10 : 1;
		switch (e.direction) {
		case LEFT:
			continuousScrollX = -scrollAmount;
			break;
		case RIGHT:
			continuousScrollX = +scrollAmount;
			break;
		case UP:
			continuousScrollY = -scrollAmount;
			break;
		case DOWN:
			continuousScrollY = +scrollAmount;
		}
	}
	
	public void handleStopScroll(StopScrollingEvent e) {
		
		switch (e.direction) {
		case LEFT:
		case RIGHT:
			continuousScrollX = 0;
			break;
		case UP:
		case DOWN:
			continuousScrollY = 0;
		}
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
		
		scroll(continuousScrollX, continuousScrollY);
		for (int screenX = 0; screenX < screen.getWidthInChars(); screenX++)
			for (int screenY = 0; screenY < screen.getHeightInChars(); screenY++) {
				
				final int mapX = getMapX(screenX), mapY = getMapY(screenY);
				
				if (mapChunk.isInChunk(mapX, mapY)) {
					
					screen.color(getMapForeground(mapX, mapY), getMapBackground(mapX, mapY));
					screen.put(getMapChar(mapX, mapY), screenX, screenY);
					
				} else {
					
					final int chunkX = mapChunk.getChunkX(mapX), chunkY = mapChunk.getChunkY(mapY);
					
					final boolean isLimitX = (chunkX == -1 || chunkX == mapChunk.getSizeX());
					final boolean isLimitY = (chunkY == -1 || chunkY == mapChunk.getSizeY());
					final boolean isWithinLimitsX = (chunkX >= 0 && chunkX < mapChunk.getSizeX());
					final boolean isWithinLimitsY = (chunkY >= 0 && chunkY < mapChunk.getSizeY());
					
					if ((isLimitX && isWithinLimitsY) || (isLimitY && isWithinLimitsX)) {
						screen.color(Color.LIGHT_GRAY, Color.SLATE);
						screen.put((char) 177, screenX, screenY);
					} else {
						screen.color();
						screen.put((char) 0, screenX, screenY);
					}
					
				}
			}
		
		drawAfterMap(screen);
	}
	
	/**
	 * Draw content after drawing the MapGenerator on-screen.
	 * 
	 * @param screen
	 */
	public abstract void drawAfterMap(AsciiScreen screen);
	
	/**
	 * Convert the given screen X-coordinate to {@link MapGenerator} coordinates
	 * (taking into account mapChunk-scrolling).
	 * 
	 * @param screen
	 * @param screenX
	 * @return
	 */
	public int getMapX(int screenX) {
		
		final int halfWidth = getContentScreen().getWidthInChars() / 2;
		return screenX + scrollX - halfWidth;
	}
	
	/**
	 * Convert the given screen Y-coordinate to {@link MapGenerator} coordinates
	 * (taking into account mapChunk-scrolling).
	 * 
	 * @param screen
	 * @param screenY
	 * @return
	 */
	public int getMapY(int screenY) {
		
		final int halfHeight = getContentScreen().getHeightInChars() / 2;
		return screenY + scrollY - halfHeight;
	}
	
	/**
	 * Get the ASCII character chosen to represent the given {@link MapGenerator}
	 * location.
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public char getMapChar(int mapX, int mapY) {
		
		return (char) 219;
	}
	
	/**
	 * Get the foreground {@link Color} chosen for the given {@link MapGenerator}
	 * location.
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public Color getMapForeground(int mapX, int mapY) {
		
		final float height = (float) mapChunk.getHeightFrac(mapX, mapY);
		return new Color(height, height, height, 1);
	}
	
	/**
	 * Get the background {@link Color} chosen for the given {@link MapGenerator}
	 * location.
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public Color getMapBackground(int mapX, int mapY) {
		
		return Color.BLACK;
	}
	
	/**
	 * Convert the given {@link MapGenerator} X-coordinate to screen coordinates
	 * (taking into account mapChunk-scrolling).
	 * 
	 * @param screen
	 * @param mapX
	 * @return
	 */
	public int getScreenX(int mapX) {
		
		final int halfWidth = getContentScreen().getWidthInChars() / 2;
		return mapX - scrollX + halfWidth;
	}
	
	/**
	 * Convert the given {@link MapGenerator} Y-coordinate to screen coordinates
	 * (taking into account mapChunk-scrolling).
	 * 
	 * @param screen
	 * @param mapY
	 * @return
	 */
	public int getScreenY(int mapY) {
		
		final int halfHeight = getContentScreen().getHeightInChars() / 2;
		return mapY - scrollY + halfHeight;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.display.MapScrolling#scroll(int, int)
	 */
	@Override
	public void scroll(int dx, int dy) {
		
		scrollTo(scrollX + dx, scrollY + dy);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.display.MapScrolling#scrollTo(int, int)
	 */
	@Override
	public void scrollTo(int x, int y) {
		
		scrollX = mapChunk.getMapX(Math.min(Math.max(mapChunk.getChunkX(x), 0), mapChunk.getSizeX() - 1));
		scrollY = mapChunk.getMapY(Math.min(Math.max(mapChunk.getChunkY(y), 0), mapChunk.getSizeY() - 1));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.display.MapScrolling#isOnScreen(int, int)
	 */
	@Override
	public boolean isOnScreen(int x, int y) {
		
		final int halfWidth = getScreen().getWidthInChars() / 2;
		final int halfHeight = getScreen().getHeightInChars() / 2;
		
		return getScreen().isOnScreen(x + scrollX - halfWidth, y + scrollY - halfHeight);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.display.MapScrolling#getScrollX()
	 */
	@Override
	public int getScrollX() {
		
		return scrollX;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.display.MapScrolling#getScrollY()
	 */
	@Override
	public int getScrollY() {
		
		return scrollY;
	}
	
	/**
	 * @return the continuousScrollX
	 */
	public int getContinuousScrollX() {
		
		return continuousScrollX;
	}
	
	/**
	 * @param continuousScrollX
	 *            the continuousScrollX to set
	 */
	public void setContinuousScrollX(int continuousScrollX) {
		
		this.continuousScrollX = continuousScrollX;
	}
	
	/**
	 * @return the continuousScrollY
	 */
	public int getContinuousScrollY() {
		
		return continuousScrollY;
	}
	
	/**
	 * @param continuousScrollY
	 *            the continuousScrollY to set
	 */
	public void setContinuousScrollY(int continuousScrollY) {
		
		this.continuousScrollY = continuousScrollY;
	}
	
	public static class MapScrollingDisplayInputProcessor extends DisplayInputProcessor {
		
		public static final int MAP_SCROLLING_DISPLAY_INPUT_PRIORITY = 16;
		
		private boolean isShiftActive = false;
		
		/**
		 * @param mapChunk
		 */
		public MapScrollingDisplayInputProcessor() {
			
			super(MAP_SCROLLING_DISPLAY_INPUT_PRIORITY);
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
				fireEvent(new StartScrollingEvent(isShiftActive, ScrollDirection.LEFT));
				return true;
			case Keys.RIGHT:
				fireEvent(new StartScrollingEvent(isShiftActive, ScrollDirection.RIGHT));
				return true;
			case Keys.UP:
				fireEvent(new StartScrollingEvent(isShiftActive, ScrollDirection.UP));
				return true;
			case Keys.DOWN:
				fireEvent(new StartScrollingEvent(isShiftActive, ScrollDirection.DOWN));
				return true;
			}
			
			return false;
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
				fireEvent(new StopScrollingEvent(ScrollDirection.LEFT));
				return true;
			case Keys.RIGHT:
				fireEvent(new StopScrollingEvent(ScrollDirection.RIGHT));
				return true;
			case Keys.UP:
				fireEvent(new StopScrollingEvent(ScrollDirection.UP));
				return true;
			case Keys.DOWN:
				fireEvent(new StopScrollingEvent(ScrollDirection.DOWN));
				return true;
			}
			
			return false;
		}
		
		public interface MapScrollingDisplayEvent extends DisplayEvent {
		};
		
		public static class StartScrollingEvent implements MapScrollingDisplayEvent {
			
			private final boolean shiftEnabled;
			private final ScrollDirection direction;
			
			/**
			 * @param shiftEnabled
			 * @param direction
			 */
			public StartScrollingEvent(boolean shiftEnabled, ScrollDirection direction) {
				
				this.shiftEnabled = shiftEnabled;
				this.direction = direction;
			}
		}
		
		public static class StopScrollingEvent implements MapScrollingDisplayEvent {
			
			private final ScrollDirection direction;
			
			/**
			 * @param shiftEnabled
			 * @param direction
			 */
			public StopScrollingEvent(ScrollDirection direction) {
				
				this.direction = direction;
			}
		}
		
		public enum ScrollDirection {
			LEFT, RIGHT, UP, DOWN;
		}
		
	}
}
