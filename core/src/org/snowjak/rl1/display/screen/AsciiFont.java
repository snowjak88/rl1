/**
 * 
 */
package org.snowjak.rl1.display.screen;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author snowjak88
 *
 */
public class AsciiFont implements Disposable {
	
	private final Texture fontTexture;
	private final Map<Character, TextureRegion> cache;
	private final int charWidth, charHeight;
	
	public AsciiFont(File fontFile, int charWidth, int charHeight) {
		
		final FileHandle fileHandle = Gdx.files.internal(fontFile.getPath());
		final Pixmap fontPixmap = new Pixmap(fileHandle);
		
		final int width = higherPowerOf2(fontPixmap.getWidth());
		final int height = higherPowerOf2(fontPixmap.getHeight());
		
		fontTexture = new Texture(width, height, fontPixmap.getFormat());
		fontTexture.draw(fontPixmap, 0, 0);
		
		this.cache = new HashMap<>();
		
		this.charWidth = charWidth;
		this.charHeight = charHeight;
	}
	
	/**
	 * @return the fontTexture
	 */
	public Texture getFontTexture() {
		
		return fontTexture;
	}
	
	/**
	 * @return the charWidth
	 */
	public int getCharWidth() {
		
		return charWidth;
	}
	
	/**
	 * @return the charHeight
	 */
	public int getCharHeight() {
		
		return charHeight;
	}
	
	/**
	 * Return a {@link TextureRegion} corresponding to the character {@code c} in
	 * the configured font-{@link Texture}.
	 * 
	 * @param c
	 * @return
	 */
	public TextureRegion getChar(char c) {
		
		if (cache.containsKey(c))
			return cache.get(c);
		
		final int col = c >> 4;
		final int row = c & 15;
		
		final int regionStartX = row * charWidth;
		final int regionStartY = col * charHeight;
		
		final TextureRegion tex = new TextureRegion(fontTexture, regionStartX, regionStartY, charWidth, charHeight);
		cache.put(c, tex);
		return tex;
	}
	
	private int higherPowerOf2(int x) {
		
		return (int) Math.pow(2.0, Math.ceil(log(x, 2.0)));
	}
	
	private double log(double x, double base) {
		
		return Math.log(x) / Math.log(base);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.utils.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		
		fontTexture.dispose();
	}
}
