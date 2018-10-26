/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.screen.AsciiScreen;

import com.badlogic.gdx.ScreenAdapter;

/**
 * @author snowjak88
 *
 */
public abstract class AbstractDisplay extends ScreenAdapter {
	
	private final AsciiScreen screen;
	private final AsciiScreen contentScreen;
	private final String title;
	private final boolean bordered;
	
	private final int titleWidth;
	private final int borderCenterX;
	private final int titleStartX;
	private final int titleEndX;
	
	/**
	 * @param screen
	 */
	public AbstractDisplay(AsciiScreen screen, String title, boolean bordered) {
		
		this.screen = screen;
		this.title = (title == null) ? "" : title;
		this.bordered = bordered;
		
		if (bordered)
			this.contentScreen = screen.getRegion(1, 1, screen.getWidthInChars() - 2, screen.getHeightInChars() - 2);
		else
			this.contentScreen = screen;
		
		titleWidth = this.title.length();
		borderCenterX = screen.getWidthInChars() / 2;
		titleStartX = borderCenterX - (titleWidth / 2);
		titleEndX = titleStartX + titleWidth - 1;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#render(float)
	 */
	@Override
	public void render(float delta) {
		
		draw();
		screen.render(delta);
	}
	
	public void draw() {
		
		drawContent(contentScreen);
		
		if (bordered)
			drawBorder();
	}
	
	public abstract void drawContent(AsciiScreen screen);
	
	private void drawBorder() {
		
		screen.color();
		
		final int startX = 0, endX = screen.getWidthInChars() - 1;
		final int startY = 0, endY = screen.getHeightInChars() - 1;
		
		if (startX < titleStartX || startX > titleEndX)
			screen.put((char) 201, startX, startY);
		
		if (endX < titleStartX || endX > titleEndX)
			screen.put((char) 187, endX, startY);
		
		screen.put((char) 200, startX, endY);
		screen.put((char) 188, endX, endY);
		
		for (int x = startX + 1; x < endX; x++) {
			
			if (x < titleStartX || x > titleEndX)
				screen.put((char) 205, x, startY);
			
			if (x == titleStartX) {
				screen.cursor(x, startY);
				screen.write(title);
			}
			
			screen.put((char) 205, x, endY);
		}
		
		for (int y = startY + 1; y < endY; y++) {
			screen.put((char) 186, startX, y);
			screen.put((char) 186, endX, y);
		}
	}
	
	/**
	 * @return the mainScreen
	 */
	public AsciiScreen getScreen() {
		
		return screen;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		
		screen.resize(width, height);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#show()
	 */
	@Override
	public void show() {
		
		screen.show();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#hide()
	 */
	@Override
	public void hide() {
		
		screen.hide();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#pause()
	 */
	@Override
	public void pause() {
		
		screen.pause();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#resume()
	 */
	@Override
	public void resume() {
		
		screen.resume();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#dispose()
	 */
	@Override
	public void dispose() {
		
		screen.dispose();
	}
	
}
