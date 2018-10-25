/**
 * 
 */
package org.snowjak.rl1.drawing.ascii;

import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.util.IntPair;

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
public class AsciiScreen extends ScreenAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(AsciiScreen.class);
	
	public static final Color DEFAULT_FOREGROUND = Color.LIGHT_GRAY;
	public static final Color DEFAULT_BACKGROUND = Color.BLACK;
	
	private final char[][] buffer;
	private final Color[][][] colors;
	
	private final SpriteBatch screenBatch;
	
	private AsciiFont font;
	private Color foregroundColor = DEFAULT_FOREGROUND, backgroundColor = DEFAULT_BACKGROUND;
	
	private IntPair cursor;
	private CursorMode cursorMode;
	
	private float scale = 1, offsetX = 0, offsetY = 0;
	
	/**
	 * Construct a new {@link AsciiScreen} with {@code widthInChars} columns and
	 * {@code heightInChars} rows, using the specified {@link AsciiFont}.
	 * 
	 * @param widthInChars
	 * @param heightInChars
	 * @param font
	 */
	public AsciiScreen(int widthInChars, int heightInChars, AsciiFont font) {
		
		this(widthInChars, heightInChars, font, CursorMode.NOP);
	}
	
	public AsciiScreen(int widthInChars, int heightInChars, AsciiFont font, CursorMode cursorMode) {
		
		assert (widthInChars > 0);
		assert (heightInChars > 0);
		assert (font != null);
		assert (cursorMode != null);
		
		this.font = font;
		this.buffer = new char[widthInChars][heightInChars];
		this.colors = new Color[widthInChars][heightInChars][2];
		this.cursor = new IntPair(0, 0);
		this.cursorMode = cursorMode;
		
		this.screenBatch = new SpriteBatch(Math.min(8191, widthInChars * heightInChars));
		
		clear();
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
		for (int x = 0; x < getWidthInChars(); x++)
			for (int y = 0; y < getHeightInChars(); y++) {
				
				final TextureRegion charTex = font.getChar(get(x, y));
				
				final float screenX = ((float) x * (float) font.getCharWidth() * scale) + offsetX;
				final float screenY = ((float) (getHeightInChars() - y - 1) * (float) font.getCharHeight() * scale)
						+ offsetY;
				
				screenBatch.setColor(colors[x][y][1]);
				screenBatch.draw(fillTex, screenX, screenY, (float) font.getCharWidth() * scale,
						(float) font.getCharHeight() * scale);
				
				screenBatch.enableBlending();
				screenBatch.setColor(colors[x][y][0]);
				screenBatch.draw(charTex, screenX, screenY, (float) font.getCharWidth() * scale,
						(float) font.getCharHeight() * scale);
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ScreenAdapter#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		
		screenBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		
		final float textSizeX = (float) getWidthInChars() * (float) font.getCharWidth();
		final float textSizeY = (float) getHeightInChars() * (float) font.getCharHeight();
		
		final float scaleX = (float) width / textSizeX;
		final float scaleY = (float) height / textSizeY;
		
		if (scaleX <= scaleY) {
			this.scale = scaleX;
			this.offsetX = 0;
			this.offsetY = ((float) height - textSizeY * scale) / 2f;
		} else {
			this.scale = scaleY;
			this.offsetX = ((float) width - textSizeX * scale) / 2f;
			this.offsetY = 0;
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
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 *             if {@code (x,y)} is outside the screen
	 */
	public char get(int x, int y) {
		
		return buffer[x][y];
	}
	
	/**
	 * Clear the screen.
	 */
	public void clear() {
		
		for (int x = 0; x < getWidthInChars(); x++)
			for (int y = 0; y < getHeightInChars(); y++) {
				buffer[x][y] = 0;
				colors[x][y][0] = DEFAULT_FOREGROUND;
				colors[x][y][1] = DEFAULT_BACKGROUND;
			}
	}
	
	public boolean isOnScreen(int x, int y) {
		
		return ((x >= 0 && x < getWidthInChars()) && (y >= 0 && y < getHeightInChars()));
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
		
		this.cursor = cursorMode.operate(this, new IntPair(x, y));
	}
	
	/**
	 * @return the cursorMode
	 */
	public CursorMode getCursorMode() {
		
		return cursorMode;
	}
	
	/**
	 * @param cursorMode
	 *            the cursorMode to set
	 */
	public void setCursorMode(CursorMode cursorMode) {
		
		this.cursorMode = cursorMode;
	}
	
	/**
	 * Advance the cursor to the next available position on the screen
	 * (right-to-left, then row-by-row, wrapping to {@code (0,0)}).
	 */
	public void advanceCursor() {
		
		cursor.setFirst(cursor.getFirst() + 1);
		if (cursor.getFirst() >= buffer.length) {
			cursor.setFirst(0);
			cursor.setSecond(cursor.getSecond() + 1);
			if (cursor.getSecond() >= buffer[cursor.getFirst()].length)
				cursor.setSecond(0);
		}
	}
	
	public int getWidthInChars() {
		
		return buffer.length;
	}
	
	public int getHeightInChars() {
		
		return buffer[0].length;
	}
	
	/**
	 * Denotes how the {@link AsciiScreen} cursor behaves when it is moved past the
	 * screen's bounds.
	 * 
	 * @author snowjak88
	 *
	 */
	public enum CursorMode {
		/**
		 * The cursor will be scrolled to the first valid screen location.
		 * Column-wrapping will be resolved to the next/previous row. Row-wrapping will
		 * be resolved by scrolling up/down.
		 */
		WRAP((as, p) -> {
			IntPair i = p;
			while (i.getFirst() < 0 || i.getFirst() >= as.getWidthInChars() || i.getSecond() < 0
					|| i.getSecond() >= as.getHeightInChars()) {
				if (i.getSecond() < 0)
					i = new IntPair(i.getFirst() - 1, as.getHeightInChars() - 1);
				else if (i.getSecond() >= as.getHeightInChars())
					i = new IntPair(i.getFirst() + 1, 0);
				else if (i.getFirst() < 0)
					i = new IntPair(i.getFirst() + as.getWidthInChars(), i.getSecond());
				else if (i.getFirst() >= as.getWidthInChars())
					i = new IntPair(i.getFirst() - as.getWidthInChars(), i.getSecond());
			}
			return i;
		}),
		/**
		 * The cursor will be moved to lie within {@code (0,0) - (w,h)} using the
		 * modulus operator.
		 */
		MOD((as, p) -> {
			int w = p.getFirst(), h = p.getSecond();
			while (w < 0)
				w += as.getWidthInChars();
			while (h < 0)
				h += as.getHeightInChars();
			return new IntPair(w % as.getWidthInChars(), h % as.getHeightInChars());
		}),
		/**
		 * The cursor will move to the closest row/column on the screen to the directed
		 * position
		 */
		CLIP((as, p) -> new IntPair(Math.min(Math.max(p.getFirst(), 0), as.getWidthInChars() - 1),
				Math.min(Math.max(p.getSecond(), 0), as.getHeightInChars() - 1))),
		/**
		 * The cursor is allowed to be placed wherever.
		 */
		NOP((as, p) -> p),
		/**
		 * The cursor will throw an {@link ArrayIndexOutOfBoundsException} when directed
		 * to move outside the screen's limits
		 */
		EXCEPTION((as, p) -> {
			if (p.getFirst() < 0 || p.getSecond() < 0 || p.getFirst() >= as.getWidthInChars()
					|| p.getSecond() >= as.getHeightInChars())
				throw new ArrayIndexOutOfBoundsException();
			return p;
		});
		
		private final BiFunction<AsciiScreen, IntPair, IntPair> f;
		
		/**
		 * @param f
		 */
		private CursorMode(BiFunction<AsciiScreen, IntPair, IntPair> f) {
			
			this.f = f;
		}
		
		/**
		 * Given the current {@link AsciiScreen} and the desired {@code position},
		 * return the resulting position after applying this CursorMode's behavior.
		 * 
		 * @param ascii
		 * @param position
		 * @return
		 */
		public IntPair operate(AsciiScreen ascii, IntPair position) {
			
			return f.apply(ascii, position);
		}
	}
	
}
