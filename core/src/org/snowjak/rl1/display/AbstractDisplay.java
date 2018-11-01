/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.display.CommonDisplayInputProcessor.UpdateScaleEvent;
import org.snowjak.rl1.screen.AsciiScreen;
import org.snowjak.rl1.screen.AsciiScreen.Region;
import org.snowjak.rl1.util.listener.Listenable.Registration;

import com.badlogic.gdx.Screen;

/**
 * @author snowjak88
 *
 */
public abstract class AbstractDisplay implements Screen {
	
	private final AsciiScreen screen;
	private final AsciiScreen contentScreen;
	private final boolean bordered;
	private final BorderType borderType;
	
	private Registration updateScaleRegistration = null;
	
	private String title;
	private boolean isActive;
	
	/**
	 * Construct a new AbstractDisplay.
	 * 
	 * @param screen
	 */
	public AbstractDisplay(AsciiScreen screen, String title, boolean bordered, BorderType borderType,
			boolean isRootDisplay) {
		
		assert ((bordered) == (borderType != null));
		
		this.screen = screen;
		this.title = (title == null) ? "" : title;
		this.bordered = bordered;
		this.borderType = borderType;
		this.isActive = true;
		
		if (bordered)
			this.contentScreen = screen.getSubscreen(Region.CENTER_MINUS_1);
		else
			this.contentScreen = screen;
		
		if (isRootDisplay) {
			final CommonDisplayInputProcessor commonInputProcessor = new CommonDisplayInputProcessor();
			Context.get().in().addProcessor(commonInputProcessor.getPriority(), commonInputProcessor);
			
			updateScaleRegistration = commonInputProcessor.addListener(this, "handleUpdateScaleEvent");
		}
	}
	
	public void handleUpdateScaleEvent(UpdateScaleEvent e) {
		
		screen.setScale(screen.getScale() * e.getScaleMultiplier());
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
	
	/**
	 * Draw this display's content to the given {@link AsciiScreen} (identical to
	 * {@link #getContentScreen()}).
	 * 
	 * @param screen
	 */
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
	 * The {@link AsciiScreen} on which this entire display is drawn.
	 * 
	 * @return the mainScreen
	 * @see #getContentScreen()
	 */
	public AsciiScreen getScreen() {
		
		return screen;
	}
	
	/**
	 * The {@link AsciiScreen} on which this display's content is drawn. This will
	 * not be identical with {@link #getScreen()} if the display is configured to
	 * have a border.
	 * 
	 * @return
	 */
	public AsciiScreen getContentScreen() {
		
		return contentScreen;
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
		
		if (updateScaleRegistration != null)
			updateScaleRegistration.remove();
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
