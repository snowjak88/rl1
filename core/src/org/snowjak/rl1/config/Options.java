/**
 * 
 */
package org.snowjak.rl1.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.drawing.ascii.AsciiFont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author snowjak88
 *
 */
@Command(mixinStandardHelpOptions = true)
public class Options implements Disposable {
	
	private static final Logger LOG = LoggerFactory.getLogger(Options.class);
	
	/**
	 * Default config-file name
	 */
	private static final File CONFIG_FILE = new File("./rl1.properties");
	
	private static final String KEY_FONT_FILENAME = "font.filename", KEY_FONT_WIDTH = "font.width",
			KEY_FONT_HEIGHT = "font.height";
	private static final String KEY_SCREEN_WIDTH = "screen.width", KEY_SCREEN_HEIGHT = "screen.height";
	
	@Option(names = { "--font-file" }, paramLabel = "FONT-FILENAME", description = "Font-filename.")
	private File fontFile = new File("fonts/cp437_12x12.png");
	@Option(names = { "--font-width" }, paramLabel = "FONT-WIDTH", description = "Font character-width.")
	private Integer fontWidth = 12;
	@Option(names = { "--font-height" }, paramLabel = "FONT-HEIGHT", description = "Font character-height.")
	private Integer fontHeight = 12;
	
	private AsciiFont asciiFont = null;
	private int screenWidth = 80, screenHeight = 25;
	
	/**
	 * Construct a new {@link Options} instance. If {@link #CONFIG_FILE} exists and
	 * can be loaded, this will load the contents of {@link #CONFIG_FILE} into this
	 * {@link Options} instance.
	 */
	public Options() {
		
		if (CONFIG_FILE.exists() && CONFIG_FILE.isFile()) {
			try {
				
				final Properties p = new Properties();
				p.load(new FileReader(CONFIG_FILE));
				
				if (p.containsKey(KEY_FONT_FILENAME) && p.containsKey(KEY_FONT_WIDTH) && p.containsKey(KEY_FONT_HEIGHT)
						&& !(p.getProperty(KEY_FONT_FILENAME).trim().isEmpty())
						&& !(p.getProperty(KEY_FONT_WIDTH).trim().isEmpty())
						&& !(p.getProperty(KEY_FONT_HEIGHT).trim().isEmpty())) {
					
					final int fontWidth, fontHeight;
					try {
						fontWidth = Integer.parseInt(p.getProperty(KEY_FONT_WIDTH));
						fontHeight = Integer.parseInt(p.getProperty(KEY_FONT_HEIGHT));
						
						if (fontWidth < 1 || fontHeight < 1)
							throw new NumberFormatException();
						
						this.fontFile = new File(p.getProperty(KEY_FONT_FILENAME));
						this.fontWidth = fontWidth;
						this.fontHeight = fontHeight;
						LOG.info("Using font \"{}\" ({}x{}).", fontFile, fontWidth, fontHeight);
						
					} catch (NumberFormatException e) {
						Gdx.app.log("Options", "Cannot parse font width/height -- not a positive integer!", e);
					}
				} else {
					
					LOG.info("WARNING: no font defined in config file. Falling back to default.");
					
				}
				
				try {
					this.screenWidth = Integer.parseInt(p.getProperty(KEY_SCREEN_WIDTH, Integer.toString(screenWidth)));
				} catch (NumberFormatException e) {
					LOG.error("Cannot parse [" + KEY_SCREEN_WIDTH + "] as an integer. Falling back to default.", e);
				}
				try {
					this.screenHeight = Integer
							.parseInt(p.getProperty(KEY_SCREEN_HEIGHT, Integer.toString(screenHeight)));
				} catch (NumberFormatException e) {
					LOG.error("Cannot parse [" + KEY_SCREEN_HEIGHT + "] as an integer. Falling back to default.", e);
				}
				
			} catch (IOException e) {
				LOG.error("Cannot load stored options from config-file \"" + CONFIG_FILE.getPath()
						+ "\". Falling back to defaults.", e);
			}
		}
	}
	
	/**
	 * @return the asciiFont
	 */
	public AsciiFont getFont() {
		
		if (asciiFont == null)
			asciiFont = new AsciiFont(fontFile, fontWidth, fontHeight);
		
		return asciiFont;
	}
	
	/**
	 * @return the screenWidth
	 */
	public int getScreenWidth() {
		
		return screenWidth;
	}
	
	/**
	 * @param screenWidth
	 *            the screenWidth to set
	 */
	public void setScreenWidth(int screenWidth) {
		
		this.screenWidth = screenWidth;
	}
	
	/**
	 * @return the screenHeight
	 */
	public int getScreenHeight() {
		
		return screenHeight;
	}
	
	/**
	 * @param screenHeight
	 *            the screenHeight to set
	 */
	public void setScreenHeight(int screenHeight) {
		
		this.screenHeight = screenHeight;
	}
	
	/**
	 * Save this Options instance to {@link #CONFIG_FILE}
	 */
	public void saveToFile() {
		
		try (FileWriter w = new FileWriter(CONFIG_FILE)) {
			
			LOG.info("Saving options to \"{}\" ...", CONFIG_FILE.getPath());
			
			final Properties p = new Properties();
			p.setProperty(KEY_FONT_FILENAME, fontFile.getPath());
			p.setProperty(KEY_FONT_WIDTH, fontWidth.toString());
			p.setProperty(KEY_FONT_HEIGHT, fontHeight.toString());
			p.setProperty(KEY_SCREEN_WIDTH, Integer.toString(screenWidth));
			p.setProperty(KEY_SCREEN_HEIGHT, Integer.toString(screenHeight));
			
			p.store(w, "RL1 Options (auto-generated)");
			
		} catch (IOException e) {
			LOG.error(
					"Cannot save Options to \"" + CONFIG_FILE.getPath() + "\". Current options will not be persisted.",
					e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.utils.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		
		if (!(CONFIG_FILE.exists() && CONFIG_FILE.isFile()))
			saveToFile();
	}
}
