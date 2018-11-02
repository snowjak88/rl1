/**
 * 
 */
package org.snowjak.rl1.display.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.snowjak.rl1.display.screen.AsciiScreen.ResizeEvent;
import org.snowjak.rl1.util.IntPair;
import org.snowjak.rl1.util.listener.Event;
import org.snowjak.rl1.util.listener.Listenable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

/**
 * Implements an ASCII display on LibGdx.
 * 
 * @author snowjak88
 *
 */
public class AsciiScreen extends ScreenAdapter implements Listenable<ResizeEvent> {
	
	public static final Color DEFAULT_FOREGROUND = Color.LIGHT_GRAY;
	public static final Color DEFAULT_BACKGROUND = Color.BLACK;
	
	private final Map<AsciiScreen.Region, AsciiScreenRegion> regions = new HashMap<>();
	private final SpriteBatch screenBatch;
	private final EventRouter eventRouter = new EventRouter();
	private final boolean canBeRescaled;
	
	private AsciiFont font;
	private Color foregroundColor = DEFAULT_FOREGROUND, backgroundColor = DEFAULT_BACKGROUND;
	
	private IntPair cursor;
	
	private int pixelWidth = 0, pixelHeight = 0;
	private int width = 0, height = 0;
	private float renderScale = 1;
	private char[][] buffer;
	private Color[][][] colors;
	
	/**
	 * Construct a new {@link AsciiScreen} using the specified {@link AsciiFont}.
	 * 
	 * @param font
	 * @param canBeRescaled
	 *            if this screen should respond to calls to
	 *            {@link #setScale(float)}, or if such calls should be ignored
	 */
	public AsciiScreen(AsciiFont font, boolean canBeRescaled) {
		
		assert (font != null);
		
		this.canBeRescaled = canBeRescaled;
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
		
		Gdx.gl.glClearColor(DEFAULT_BACKGROUND.r, DEFAULT_BACKGROUND.g, DEFAULT_BACKGROUND.b, DEFAULT_BACKGROUND.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderContent(screenBatch, 8190);
	}
	
	protected void renderContent(Batch screenBatch, int batchBufferSize) {
		
		screenBatch.begin();
		
		final TextureRegion fillTex = font.getChar((char) 219);
		
		int i = 0;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				
				final char currentChar = get(x, y);
				final TextureRegion charTex = font.getChar(currentChar);
				
				final float screenX = (float) x * (float) font.getCharWidth() / renderScale;
				final float screenY = (float) (getHeightInChars() - y - 1) * (float) font.getCharHeight() / renderScale;
				
				if (colors[x][y][1] != DEFAULT_BACKGROUND) {
					screenBatch.setColor(colors[x][y][1]);
					screenBatch.draw(fillTex, screenX, screenY, (float) font.getCharWidth() / renderScale,
							(float) font.getCharHeight() / renderScale);
					i++;
				}
				
				if (currentChar != 0) {
					
					screenBatch.enableBlending();
					screenBatch.setColor(colors[x][y][0]);
					screenBatch.draw(charTex, screenX, screenY, (float) font.getCharWidth() / renderScale,
							(float) font.getCharHeight() / renderScale);
					i++;
					screenBatch.disableBlending();
				}
				
				if (i >= batchBufferSize) {
					screenBatch.end();
					screenBatch.begin();
					i = 0;
				}
			}
		
		screenBatch.end();
		
		//
		//
		//
		regions.forEach((r, asr) -> {
			
			final Matrix4 prevTransform = screenBatch.getTransformMatrix().cpy();
			
			screenBatch.setTransformMatrix(screenBatch.getTransformMatrix().mul(asr.offset));
			asr.renderContent(screenBatch, batchBufferSize);
			
			screenBatch.setTransformMatrix(prevTransform);
		});
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
		regions.forEach((r, asr) -> asr.dispose());
	}
	
	public void setScale(float scale) {
		
		if (!canBeRescaled)
			return;
		
		assert (scale > 0);
		
		this.renderScale = scale;
		this.regions.forEach((r, s) -> s.setScale(scale));
		
		this.resize(pixelWidth, pixelHeight);
	}
	
	public float getScale() {
		
		return renderScale;
	}
	
	/**
	 * @return the canBeRescaled
	 */
	public boolean canBeRescaled() {
		
		return canBeRescaled;
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
		
		this.regions.forEach((r, s) -> r.apply(this, s));
		
		fireEvent(new ResizeEvent(width, height));
	}
	
	private void rebuildBuffers() {
		
		if (width <= 0 || height <= 0)
			return;
		
		this.buffer = new char[width][height];
		this.colors = new Color[width][height][2];
		
		final IntPair prevCursor = getCursor();
		
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				cursor(x, y);
				color(DEFAULT_FOREGROUND, DEFAULT_BACKGROUND);
				put((char) 0);
			}
		
		cursor(prevCursor.getFirst(), prevCursor.getSecond());
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
	
	public int getWidthInPixels() {
		
		return pixelWidth;
	}
	
	public int getHeightInPixels() {
		
		return pixelHeight;
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
	 * Create an {@link AsciiScreen} instance drawing to the given {@link Region} of
	 * this AsciiScreen. If an AsciiScreen has already been allocated for that
	 * region, this method will first dispose of the pre-existing instance before
	 * creating a new one.
	 * 
	 * @param region
	 * @return
	 */
	public AsciiScreenRegion createSubscreen(AsciiScreen.Region region) {
		
		return createSubscreen(region, canBeRescaled);
	}
	
	/**
	 * Create an {@link AsciiScreen} instance drawing to the given {@link Region} of
	 * this AsciiScreen. If an AsciiScreen has already been allocated for that
	 * region, this method will first dispose of the pre-existing instance before
	 * creating a new one.
	 * 
	 * @param region
	 * @param canBeRescaled
	 *            if the given {@link AsciiScreenRegion} should allow itself to be
	 *            rescaled via {@link AsciiScreen#setScale(float)}, or if such calls
	 *            should be ignored
	 * @return
	 */
	public AsciiScreenRegion createSubscreen(AsciiScreen.Region region, boolean canBeRescaled) {
		
		if (regions.containsKey(region))
			regions.get(region).dispose();
		
		final AsciiScreenRegion subscreen = new AsciiScreenRegion(getFont(), canBeRescaled);
		subscreen.setScale(renderScale);
		regions.put(region, subscreen);
		region.apply(this, subscreen);
		
		return regions.get(region);
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
	
	public enum Region {
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the left-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		LEFT_30((s, r) -> {
			final int regionPixelWidth = (int) ((float) s.getWidthInPixels() * 0.3);
			final int regionPixelHeight = s.getHeightInPixels();
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4();
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the left-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		LEFT_70((s, r) -> {
			final int regionPixelWidth = (int) ((float) s.getWidthInPixels() * 0.7);
			final int regionPixelHeight = s.getHeightInPixels();
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4();
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the right-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		RIGHT_30((s, r) -> {
			final int regionPixelWidth = (int) ((float) s.getWidthInPixels() * 0.3);
			final int regionPixelHeight = s.getHeightInPixels();
			final int regionOffsetX = s.getWidthInPixels() - regionPixelWidth;
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4().setToTranslation(regionOffsetX, 0, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the right-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		RIGHT_70((s, r) -> {
			final int regionPixelWidth = (int) ((float) s.getWidthInPixels() * 0.7);
			final int regionPixelHeight = s.getHeightInPixels();
			final int regionOffsetX = s.getWidthInPixels() - regionPixelWidth;
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4().setToTranslation(regionOffsetX, 0, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the top-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		TOP_30((s, r) -> {
			final int regionPixelWidth = s.getWidthInPixels();
			final int regionPixelHeight = (int) ((float) s.getHeightInPixels() * 0.3);
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4();
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the top-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		TOP_70((s, r) -> {
			final int regionPixelWidth = s.getWidthInPixels();
			final int regionPixelHeight = (int) ((float) s.getHeightInPixels() * 0.7);
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4();
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the bottom-most 30% of its
		 * parent {@link AsciiScreen}.
		 */
		BOTTOM_30((s, r) -> {
			final int regionPixelWidth = s.getWidthInPixels();
			final int regionPixelHeight = (int) ((float) s.getHeightInPixels() * 0.3);
			final int regionOffsetY = s.getHeightInPixels() - regionPixelHeight;
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4().setToTranslation(0, regionOffsetY, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the bottom-most 70% of its
		 * parent {@link AsciiScreen}.
		 */
		BOTTOM_70((s, r) -> {
			final int regionPixelWidth = s.getWidthInPixels();
			final int regionPixelHeight = (int) ((float) s.getHeightInPixels() * 0.7);
			final int regionOffsetY = s.getHeightInPixels() - regionPixelHeight;
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4().setToTranslation(0, regionOffsetY, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy a centered area, 50% of the
		 * width/height of its parent {@link AsciiScreen}.
		 */
		CENTER_50((s, r) -> {
			final int regionPixelWidth = (int) ((float) s.getWidthInPixels() * 0.5);
			final int regionPixelHeight = (int) ((float) s.getHeightInPixels() * 0.5);
			final int regionOffsetX = (s.getWidthInPixels() - regionPixelWidth) / 2;
			final int regionOffsetY = (s.getHeightInPixels() - regionPixelHeight) / 2;
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4().setToTranslation(regionOffsetX, regionOffsetY, 0);
		}),
		/**
		 * The {@link AsciiScreenRegion} is set up to occupy the entirity of the parent
		 * AsciiScreen excepting a border of 1 character's width.
		 */
		CENTER_MINUS_1((s, r) -> {
			final int regionPixelWidth = (int) ((float) s.getWidthInPixels()
					- ((float) s.font.getCharWidth() * 2f / s.renderScale));
			final int regionPixelHeight = (int) ((float) s.getHeightInPixels()
					- ((float) s.font.getCharHeight() * 2f / s.renderScale));
			final int regionOffsetX = (s.getWidthInPixels() - regionPixelWidth) / 2;
			final int regionOffsetY = (s.getHeightInPixels() - regionPixelHeight) / 2;
			
			r.resize(regionPixelWidth, regionPixelHeight);
			r.offset = new Matrix4().setToTranslation(regionOffsetX, regionOffsetY, 0);
		});
		
		private final BiConsumer<AsciiScreen, AsciiScreenRegion> resizer;
		
		/**
		 * @param resizer
		 */
		private Region(BiConsumer<AsciiScreen, AsciiScreenRegion> resizer) {
			
			this.resizer = resizer;
		}
		
		public void apply(AsciiScreen screen, AsciiScreenRegion region) {
			
			this.resizer.accept(screen, region);
		}
	}
	
	public static class AsciiScreenRegion extends AsciiScreen {
		
		private Matrix4 offset = new Matrix4();
		
		/**
		 * @param font
		 * @param canBeRescaled
		 */
		public AsciiScreenRegion(AsciiFont font, boolean canBeRescaled) {
			
			super(font, canBeRescaled);
		}
		
	}
	
	public static class ResizeEvent implements Event {
		
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
