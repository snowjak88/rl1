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
	private final boolean bordered;
	private final BorderType borderType;
	
	private String title;
	private boolean isActive;
	
	/**
	 * @param screen
	 */
	public AbstractDisplay(AsciiScreen screen, String title, boolean bordered, BorderType borderType) {
		
		assert ((bordered) == (borderType != null));
		
		this.screen = screen;
		this.title = (title == null) ? "" : title;
		this.bordered = bordered;
		this.borderType = borderType;
		this.isActive = true;
		
		if (bordered)
			this.contentScreen = screen.getRegion(1, 1, screen.getWidthInChars() - 2, screen.getHeightInChars() - 2);
		else
			this.contentScreen = screen;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#render(float)
	 */
	@Override
	public void render(float delta) {
		
		if (isActive) {
			draw();
			screen.render(delta);
		}
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		
		return title;
	}
	
	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		
		this.title = title;
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		
		return isActive;
	}
	
	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(boolean isActive) {
		
		this.isActive = isActive;
	}
	
	public void draw() {
		
		drawContent(contentScreen);
		
		if (bordered)
			drawBorder();
	}
	
	public abstract void drawContent(AsciiScreen screen);
	
	private void drawBorder() {
		
		screen.color();
		
		final int titleWidth = this.title.length();
		final int borderCenterX = screen.getWidthInChars() / 2;
		final int titleStartX = borderCenterX - (titleWidth / 2);
		final int titleEndX = titleStartX + titleWidth - 1;
		
		final int startX = 0, endX = screen.getWidthInChars() - 1;
		final int startY = 0, endY = screen.getHeightInChars() - 1;
		
		if (startX < titleStartX || startX > titleEndX)
			screen.put(borderType.getUpperLeft(), startX, startY);
		
		if (endX < titleStartX || endX > titleEndX)
			screen.put(borderType.getUpperRight(), endX, startY);
		
		screen.put(borderType.getLowerLeft(), startX, endY);
		screen.put(borderType.getLowerRight(), endX, endY);
		
		for (int x = startX + 1; x < endX; x++) {
			
			if (x < titleStartX || x > titleEndX)
				screen.put(borderType.getTopHorizontal(), x, startY);
			
			if (x == titleStartX) {
				screen.cursor(x, startY);
				screen.write(title);
			}
			
			screen.put(borderType.getBottomHorizontal(), x, endY);
		}
		
		for (int y = startY + 1; y < endY; y++) {
			screen.put(borderType.getLeftVertical(), startX, y);
			screen.put(borderType.getRightVertical(), endX, y);
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
	
	public enum BorderType {
		DOUBLE_LINE(201, 187, 200, 188, 205, 205, 186, 186), SINGLE_LINE(218, 191, 192, 217, 196, 196, 179,
				179), BLOCK(219, 219, 219, 219, 220, 223, 222, 221);
		
		private final char ul, ur, ll, lr, th, bh, lv, rv;
		
		/**
		 * @param ul
		 * @param ur
		 * @param ll
		 * @param lr
		 * @param th
		 * @param bh
		 * @param lv
		 * @param rv
		 */
		private BorderType(int ul, int ur, int ll, int lr, int th, int bh, int lv, int rv) {
			
			this.ul = (char) ul;
			this.ur = (char) ur;
			this.ll = (char) ll;
			this.lr = (char) lr;
			this.th = (char) th;
			this.bh = (char) bh;
			this.lv = (char) lv;
			this.rv = (char) rv;
		}
		
		/**
		 * @return the ul
		 */
		public char getUpperLeft() {
			
			return ul;
		}
		
		/**
		 * @return the ur
		 */
		public char getUpperRight() {
			
			return ur;
		}
		
		/**
		 * @return the ll
		 */
		public char getLowerLeft() {
			
			return ll;
		}
		
		/**
		 * @return the lr
		 */
		public char getLowerRight() {
			
			return lr;
		}
		
		/**
		 * @return the th
		 */
		public char getTopHorizontal() {
			
			return th;
		}
		
		/**
		 * @return the bh
		 */
		public char getBottomHorizontal() {
			
			return bh;
		}
		
		/**
		 * @return the lv
		 */
		public char getLeftVertical() {
			
			return lv;
		}
		
		/**
		 * @return the rv
		 */
		public char getRightVertical() {
			
			return rv;
		}
	}
}
