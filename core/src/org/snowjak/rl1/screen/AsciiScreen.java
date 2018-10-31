/**
 * 
 */
package org.snowjak.rl1.screen;

import java.util.function.BiConsumer;

import org.snowjak.rl1.screen.AsciiScreen.ResizeEvent;
import org.snowjak.rl1.util.IntPair;
import org.snowjak.rl1.util.Listenable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Implements an ASCII display on LibGdx.
 * 
 * @author snowjak88
 *
 */
public class AsciiScreen extends ScreenAdapter implements Listenable<ResizeEvent> {
	
	public static final Color DEFAULT_FOREGROUND = Color.LIGHT_GRAY;
	public static final Color DEFAULT_BACKGROUND = Color.BLACK;
	
	private final SpriteBatch screenBatch;
	private final boolean allocateBuffers;
	
	private final EventRouter eventRouter = new EventRouter();
	
	private AsciiFont font;
	private Color foregroundColor = DEFAULT_FOREGROUND, backgroundColor = DEFAULT_BACKGROUND;
	
	private IntPair cursor;
	
	private int pixelWidth = 0, pixelHeight = 0;
	private int width = 0, height = 0;
	private char[][] buffer;
	private Color[][][] colors;
	
	private float renderScale = 1;
	
	/**
	 * Construct a new {@link AsciiScreen} using the specified {@link AsciiFont}.
	 * 
	 * @param widthInChars
	 * @param heightInChars
	 * @param font
	 */
	public AsciiScreen(AsciiFont font) {
		
		this(font, true);
	}
	
	protected AsciiScreen(AsciiFont font, boolean allocateBuffers) {
		
		assert (font != null);
		
		this.allocateBuffers = allocateBuffers;
		this.font = font;
		this.cursor = new IntPair(0, 0);
		this.screenBatch = new SpriteBatch(8191);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#render(float)
	 */
	@Override
	public void render(float delta) {
		
		final TextureRegion fillTex = font.getChar((char) 219);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		screenBatch.begin();
		
		int i = 0;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				
				final TextureRegion charTex = font.getChar(get(x, y));
				
				final float screenX = (float) x * (float) font.getCharWidth() / renderScale;
				final float screenY = (float) (getHeightInChars() - y - 1) * (float) font.getCharHeight() / renderScale;
				
				screenBatch.setColor(colors[x][y][1]);
				screenBatch.draw(fillTex, screenX, screenY, (float) font.getCharWidth() / renderScale,
						(float) font.getCharHeight() / renderScale);
				
				screenBatch.enableBlending();
				screenBatch.setColor(colors[x][y][0]);
				screenBatch.draw(charTex, screenX, screenY, (float) font.getCharWidth() / renderScale,
						(float) font.getCharHeight() / renderScale);
				screenBatch.disableBlending();
				
				i += 2;
				if (i >= 8190) {
					screenBatch.end();
					screenBatch.begin();
					i = 0;
				}
			}
		
		screenBatch.end();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#dispose()
	 */
	@Override
	public void dispose() {
		
		screenBatch.dispose();
		font.dispose();
	}
	
	public void setScale(float scale) {
		
		assert (scale > 0);
		
		this.renderScale = scale;
		
		this.resize(pixelWidth, pixelHeight);
	}
	
	public float getScale() {
		
		return renderScale;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		
		screenBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		
		this.pixelWidth = width;
		this.pixelHeight = height;
		
		final float charWidth = (float) font.getCharWidth() / renderScale;
		final float charHeight = (float) font.getCharHeight() / renderScale;
		
		this.width = (int) ((float) width / charWidth);
		this.height = (int) ((float) height / charHeight);
		
		rebuildBuffers();
		
		fireEvent(new ResizeEvent(width, height));
	}
	
	private void rebuildBuffers() {
		
		if (allocateBuffers) {
			this.buffer = new char[width][height];
			this.colors = new Color[width][height][2];
			
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++) {
					this.colors[x][y][0] = DEFAULT_FOREGROUND;
					this.colors[x][y][1] = DEFAULT_BACKGROUND;
				}
		} else {
			this.buffer = null;
			this.colors = null;
		}
	}
	
	/**
	 * Write the given character to the screen and advance the cursor.
	 * 
	 * @param c
	 * @see #write(CharSequence)
	 * @see #advanceCursor()
	 */
	public void write(char c) {
		
		put(c);
		advanceCursor();
	}
	
	/**
	 * Write the given character-sequence to the screen, advancing the cursor with
	 * each character.
	 * 
	 * @param s
	 * @see #write(char)
	 * @see #advanceCursor()
	 */
	public void write(CharSequence s) {
		
		s.chars().forEachOrdered(c -> write((char) c));
	}
	
	/**
	 * Move the cursor to the indicated position, and writes the character at this
	 * position. Does not advance the cursor afterwards.
	 * <p>
	 * If the desired location falls outside the screen's bounds, this method
	 * consults the configured {@link CursorMode} to decide how to proceed.
	 * </p>
	 * 
	 * @param c
	 * @param x
	 * @param y
	 * @see #setCursorMode(CursorMode)
	 * @see CursorMode
	 */
	public void put(char c, int x, int y) {
		
		cursor(x, y);
		put(c);
	}
	
	/**
	 * Puts the given character on the screen at the current cursor position,
	 * without advancing the cursor.
	 * 
	 * @param c
	 */
	public void put(char c) {
		
		if (!isOnScreen(cursor.getFirst(), cursor.getSecond()))
			return;
		
		if (buffer == null && colors == null)
			return;
		
		buffer[cursor.getFirst()][cursor.getSecond()] = c;
		colors[cursor.getFirst()][cursor.getSecond()][0] = foregroundColor;
		colors[cursor.getFirst()][cursor.getSecond()][1] = backgroundColor;
	}
	
	/**
	 * Get the character at the cursor's position.
	 * 
	 * @return
	 */
	public char get() {
		
		return get(cursor.getFirst(), cursor.getSecond());
	}
	
	/**
	 * Get the {@code char} at the given {@code (x,y)} on the screen.
	 * 
	 * @param x
	 * @param y
	 * @return {@code char(0)} if the given location is outside the screen
	 */
	public char get(int x, int y) {
		
		if (buffer == null || !isOnScreen(x, y))
			return 0;
		
		return buffer[x][y];
	}
	
	/**
	 * Clear the screen (filling it with the current background color).
	 */
	public void clear() {
		
		clear(0, 0, width - 1, height - 1);
	}
	
	/**
	 * Clear a region on the screen (filling it with the current background color).
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void clear(int startX, int startY, int endX, int endY) {
		
		for (int x = startX; x <= endX; x++)
			for (int y = startY; y <= endY; y++)
				put((char) 0, x, y);
	}
	
	public boolean isOnScreen(int x, int y) {
		
		return ((x >= 0 && x < width) && (y >= 0 && y < height));
	}
	
	/**
	 * Reset foreground and background colors to their defaults.
	 * 
	 * @see #DEFAULT_FOREGROUND
	 * @see #DEFAULT_BACKGROUND
	 */
	public void color() {
		
		color(DEFAULT_FOREGROUND, DEFAULT_BACKGROUND);
	}
	
	public void color(Color foreground, Color background) {
		
		foreground(foreground);
		background(background);
	}
	
	/**
	 * Reset foreground color to its default.
	 * 
	 * @see #DEFAULT_FOREGROUND
	 */
	public void foreground() {
		
		foreground(DEFAULT_FOREGROUND);
	}
	
	public void foreground(Color color) {
		
		this.foregroundColor = color;
	}
	
	/**
	 * Reset background color to its default.
	 * 
	 * @see #DEFAULT_BACKGROUND
	 */
	public void background() {
		
		background(DEFAULT_BACKGROUND);
	}
	
	public void background(Color color) {
		
		this.backgroundColor = color;
	}
	
	/**
	 * Moves the cursor to the given {@code (x,y)} position on the screen.
	 * <p>
	 * If the desired location falls outside the screen's bounds, this method
	 * consults the configured {@link CursorMode} to decide how to proceed.
	 * </p>
	 * 
	 * @param x
	 * @param y
	 * @see #setCursorMode(CursorMode)
	 * @see CursorMode
	 */
	public void cursor(int x, int y) {
		
		this.cursor = new IntPair(x, y);
	}
	
	/**
	 * Advance the cursor to the next available position on the screen
	 * (right-to-left, then row-by-row, wrapping to {@code (0,0)}).
	 */
	public void advanceCursor() {
		
		cursor.setFirst(cursor.getFirst() + 1);
		if (cursor.getFirst() >= width) {
			cursor.setFirst(0);
			cursor.setSecond(cursor.getSecond() + 1);
			if (cursor.getSecond() >= height)
				cursor.setSecond(0);
		}
	}
	
	public int getWidthInChars() {
		
		return width;
	}
	
	public int getHeightInChars() {
		
		return height;
	}
	
	/**
	 * @return
	 */
	public AsciiFont getFont() {
		
		return font;
	}
	
	/**
	 * @return the foregroundColor
	 */
	public Color getForegroundColor() {
		
		return foregroundColor;
	}
	
	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		
		return backgroundColor;
	}
	
	/**
	 * @return the cursor
	 */
	public IntPair getCursor() {
		
		return cursor;
	}
	
	/**
	 * Construct an {@link AsciiScreenRegion} delegating to a specific region of
	 * this {@link AsciiScreen}.
	 * 
	 * @param alignment
	 * @return
	 */
	public AsciiScreenRegion getRegion(RegionAlignment alignment) {
		
		return new AsciiScreenRegion(this, alignment.resizer);
	}
	
	public AsciiScreenRegion getRegion(BiConsumer<AsciiScreen, AsciiScreenRegion> resizer) {
		
		return new AsciiScreenRegion(this, resizer);
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
	
	public enum RegionAlignment {
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the left-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		LEFT_30((s, r) -> {
			// final float width = (float) s.pixelWidth * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize((int) (0.3 * (float) s.pixelWidth), s.pixelHeight);
			r.setScale(s.getScale());
			r.setStart(0, 0);
		}),
		
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the left-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		LEFT_70((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize((int) (0.7 * (float) s.pixelWidth), s.pixelHeight);
			r.setScale(s.getScale());
			r.setStart(0, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the right-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		RIGHT_30((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize((int) (0.3 * (float) s.pixelWidth), s.pixelHeight);
			r.setScale(s.getScale());
			r.setStart((int) (0.7 * (float) s.getWidthInChars()), 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the right-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		RIGHT_70((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize((int) (0.7 * (float) s.pixelWidth), s.pixelHeight);
			r.setStart((int) (0.3 * (float) s.getWidthInChars()), 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the top-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		TOP_30((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize(s.pixelWidth, (int) (0.3 * (float) s.pixelHeight));
			r.setStart(0, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the top-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		TOP_70((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize(s.pixelWidth, (int) (0.7 * (float) s.pixelHeight));
			r.setStart(0, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the bottom-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		BOTTOM_30((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize(s.pixelWidth, (int) (0.3 * (float) s.pixelHeight));
			r.setStart(0, (int) (0.7 * (float) s.getHeightInChars()));
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the bottom-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		BOTTOM_70((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize(s.pixelWidth, (int) (0.7 * (float) s.pixelHeight));
			r.setStart(0, (int) (0.3 * (float) s.getHeightInChars()));
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy a centered area, 50% of the
		 * width/height of its parent {@link AsciiScreen}.
		 */
		CENTER_50((s, r) -> {
			// final float width = (float) s.width * s.getScale() * (float)
			// s.font.getCharWidth();
			// final float height = (float) s.height * s.getScale() * (float)
			// s.font.getCharHeight();
			r.resize((int) ((float) s.pixelWidth * 0.5), (int) ((float) s.pixelHeight * 0.5));
			r.setStart((int) (0.25 * (float) s.getWidthInChars()), (int) (0.25 * (float) s.getHeightInChars()));
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the entirity of the parent
		 * AsciiScreen excepting a border of 1 character's width.
		 */
		CENTER_MINUS_1((s, r) -> {
			
			final float widthInChars = (float) s.pixelWidth * s.renderScale / (float) s.font.getCharWidth() - 2;
			final float heightInChars = (float) s.pixelHeight * s.renderScale / (float) s.font.getCharHeight() - 2;
			final int widthInPixels = (int) (widthInChars * (float) s.font.getCharWidth() / s.renderScale);
			final int heightInPixels = (int) (heightInChars * (float) s.font.getCharHeight() / s.renderScale);
			r.resize(widthInPixels, heightInPixels);
			r.setStart(1, 1);
		});
		
		private final BiConsumer<AsciiScreen, AsciiScreenRegion> resizer;
		
		/**
		 * @param resizer
		 */
		private RegionAlignment(BiConsumer<AsciiScreen, AsciiScreenRegion> resizer) {
			
			this.resizer = resizer;
		}
		
	}
	
	/**
	 * Gives access to a specific region of an existing AsciiScreen. Writes made to
	 * this region are clipped to the specified region
	 * <code>(startX,startY) - (endX,endY)</code>.
	 * 
	 * @author snowjak88
	 *
	 */
	public static class AsciiScreenRegion extends AsciiScreen {
		
		private final AsciiScreen screen;
		private final BiConsumer<AsciiScreen, AsciiScreenRegion> resizer;
		private final Registration resizeEventRegistration;
		
		private int startX, startY;
		
		/**
		 * @param screen
		 * @param startX
		 * @param startY
		 * @param endX
		 * @param endY
		 */
		public AsciiScreenRegion(AsciiScreen screen, BiConsumer<AsciiScreen, AsciiScreenRegion> resizer) {
			
			super(screen.getFont(), false);
			this.screen = screen;
			this.resizer = resizer;
			this.resizeEventRegistration = screen.addListener(this, "handleResizeEvent");
		}
		
		public void handleResizeEvent(ResizeEvent e) {
			
			this.resizer.accept(screen, this);
		}
		
		/**
		 * Update the "start-point", where the upper-left corner of the region lies
		 * within its parent AsciiScreen.
		 * 
		 * @param startX
		 * @param startY
		 */
		public void setStart(int startX, int startY) {
			
			this.startX = startX;
			this.startY = startY;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.snowjak.rl1.screen.AsciiScreen#put(char)
		 */
		@Override
		public void put(char c) {
			
			final Color prevForeground = screen.getForegroundColor(), prevBackground = screen.getBackgroundColor();
			final IntPair prevCursor = screen.getCursor();
			screen.color(getForegroundColor(), getBackgroundColor());
			
			screen.cursor(getCursor().getFirst() + startX, getCursor().getSecond() + startY);
			screen.put(c);
			
			screen.cursor(prevCursor.getFirst(), prevCursor.getSecond());
			screen.color(prevForeground, prevBackground);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.snowjak.rl1.drawing.ascii.AsciiScreen#get(int, int)
		 */
		@Override
		public char get(int x, int y) {
			
			if (!isOnScreen(x, y))
				return 0;
			
			return screen.get(x + startX, y + startY);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.snowjak.rl1.drawing.ascii.AsciiScreen#render(float)
		 */
		@Override
		public void render(float delta) {
			
			screen.render(delta);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.snowjak.rl1.screen.AsciiScreen#dispose()
		 */
		@Override
		public void dispose() {
			
			super.dispose();
			resizeEventRegistration.remove();
		}
		
	}
	
	public static class ResizeEvent implements Listenable.Event {
		
		@SuppressWarnings("unused")
		private final int width, height;
		
		/**
		 * @param width
		 * @param height
		 */
		public ResizeEvent(int width, int height) {
			
			this.width = width;
			this.height = height;
		}
		
	}
	
}
