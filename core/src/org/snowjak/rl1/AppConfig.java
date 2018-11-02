/**
 * 
 */
package org.snowjak.rl1;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.display.screen.AsciiFont;
import org.snowjak.rl1.map.MapConfig;
import org.snowjak.rl1.util.Converters;

import com.badlogic.gdx.utils.Disposable;

import picocli.CommandLine.Command;

/**
 * @author snowjak88
 *
 */
@Command
public class AppConfig implements Disposable {
	
	private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
	
	/**
	 * Default config-file name
	 */
	private static final File CONFIG_FILE = new File("./rl1.properties");
	
	private static final String KEY_PARALLELISM = "parallelism";
	
	private static final String KEY_FONT_FILENAME = "font.filename", KEY_FONT_WIDTH = "font.width",
			KEY_FONT_HEIGHT = "font.height";
	
	private static final String KEY_WINDOW_WIDTH = "window.width", KEY_WINDOW_HEIGHT = "window.height";
	private static final String KEY_SCREEN_WIDTH = "screen.width", KEY_SCREEN_HEIGHT = "screen.height";
	
	private static final String KEY_SEED = "seed";
	
	private static final String KEY_MAP_LARGEST_FEATURE = "map.features.largest";
	private static final String KEY_MAP_PERSISTENCE = "map.features.persistence";
	private static final String KEY_MAP_LOWEST_ALTITUDE = "map.features.lowest",
			KEY_MAP_HIGHEST_ALTITUDE = "map.features.highest";
	private static final String KEY_MAP_CHUNK_WIDTH = "map.chunk.width", KEY_MAP_CHUNK_HEIGHT = "map.chunk.height";
	
	private int parallelism = Runtime.getRuntime().availableProcessors();
	
	private File fontFile = new File("fonts/cp437_12x12.png");
	private Integer fontWidth = 12;
	private Integer fontHeight = 12;
	
	private transient AsciiFont asciiFont = null;
	
	private int windowWidth = 630;
	private int windowHeight = 420;
	private int screenWidth = 60;
	private int screenHeight = 40;
	
	private String seed = "";
	
	private MapConfig mapConfig = new MapConfig();
	
	/**
	 * Construct a new {@link AppConfig} instance. If {@link #CONFIG_FILE} exists
	 * and can be loaded, this will load the contents of {@link #CONFIG_FILE} into
	 * this {@link AppConfig} instance.
	 */
	public AppConfig() {
		
	}
	
	private <T> void getConfig(Properties p, String propertyKey, Supplier<T> getter, Consumer<T> setter,
			Class<T> type) {
		
		if (p.containsKey(propertyKey))
			try {
				setter.accept(Converters.convert(p.getProperty(propertyKey), type));
			} catch (Exception e) {
				LOG.error("Cannot parse [" + propertyKey + "]. Falling back to default.", e);
			}
	}
	
	private <T> void setConfig(Properties p, String propertyKey, Supplier<T> getter) {
		
		p.setProperty(propertyKey, Converters.convert(getter.get(), String.class));
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
	 * @return the parallelism
	 */
	public int getParallelism() {
		
		return parallelism;
	}
	
	/**
	 * @param parallelism
	 *            the parallelism to set
	 */
	public void setParallelism(int parallelism) {
		
		this.parallelism = parallelism;
	}
	
	/**
	 * @return the windowWidth
	 */
	public int getWindowWidth() {
		
		return windowWidth;
	}
	
	/**
	 * @param windowWidth
	 *            the windowWidth to set
	 */
	public void setWindowWidth(int windowWidth) {
		
		this.windowWidth = windowWidth;
	}
	
	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		
		return windowHeight;
	}
	
	/**
	 * @param windowHeight
	 *            the windowHeight to set
	 */
	public void setWindowHeight(int windowHeight) {
		
		this.windowHeight = windowHeight;
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
	 * @return the fontFile
	 */
	public File getFontFile() {
		
		return fontFile;
	}
	
	/**
	 * @param fontFile
	 *            the fontFile to set
	 */
	public void setFontFile(File fontFile) {
		
		this.asciiFont = null;
		this.fontFile = fontFile;
	}
	
	/**
	 * @return the fontWidth
	 */
	public Integer getFontWidth() {
		
		return fontWidth;
	}
	
	/**
	 * @param fontWidth
	 *            the fontWidth to set
	 */
	public void setFontWidth(Integer fontWidth) {
		
		this.asciiFont = null;
		this.fontWidth = fontWidth;
	}
	
	/**
	 * @return the fontHeight
	 */
	public Integer getFontHeight() {
		
		return fontHeight;
	}
	
	/**
	 * @param fontHeight
	 *            the fontHeight to set
	 */
	public void setFontHeight(Integer fontHeight) {
		
		this.asciiFont = null;
		this.fontHeight = fontHeight;
	}
	
	/**
	 * @return the seed
	 */
	public String getSeed() {
		
		return seed;
	}
	
	/**
	 * @param seed
	 *            the seed to set
	 */
	public void setSeed(String seed) {
		
		this.seed = seed;
	}
	
	/**
	 * @retur)n the mapConfig
	 */
	public MapConfig getMapConfig() {
		
		return mapConfig;
	}
	
	/**
	 * @param mapConfig
	 *            the mapConfig to set
	 */
	public void setMapConfig(MapConfig mapConfig) {
		
		this.mapConfig = mapConfig;
	}
	
	public void loadFromFile() {
		
		if (CONFIG_FILE.exists() && CONFIG_FILE.isFile()) {
			try {
				
				final Properties p = new Properties();
				p.load(new FileReader(CONFIG_FILE));
				
				getConfig(p, KEY_PARALLELISM, this::getParallelism, this::setParallelism, Integer.class);
				
				if (p.containsKey(KEY_FONT_FILENAME) && p.containsKey(KEY_FONT_WIDTH) && p.containsKey(KEY_FONT_HEIGHT)
						&& !(p.getProperty(KEY_FONT_FILENAME).trim().isEmpty())
						&& !(p.getProperty(KEY_FONT_WIDTH).trim().isEmpty())
						&& !(p.getProperty(KEY_FONT_HEIGHT).trim().isEmpty())) {
					
					getConfig(p, KEY_FONT_FILENAME, this::getFontFile, this::setFontFile, File.class);
					getConfig(p, KEY_FONT_WIDTH, this::getFontWidth, this::setFontWidth, Integer.class);
					getConfig(p, KEY_FONT_HEIGHT, this::getFontHeight, this::setFontHeight, Integer.class);
					
				}
				
				getConfig(p, KEY_WINDOW_WIDTH, this::getWindowWidth, this::setWindowWidth, Integer.class);
				getConfig(p, KEY_WINDOW_HEIGHT, this::getWindowHeight, this::setWindowHeight, Integer.class);
				
				getConfig(p, KEY_SCREEN_WIDTH, this::getScreenWidth, this::setScreenWidth, Integer.class);
				getConfig(p, KEY_SCREEN_HEIGHT, this::getScreenHeight, this::setScreenHeight, Integer.class);
				
				getConfig(p, KEY_SEED, this::getSeed, this::setSeed, String.class);
				
				getConfig(p, KEY_MAP_LARGEST_FEATURE, () -> this.getMapConfig().getLargestFeature(),
						(i) -> this.getMapConfig().setLargestFeature(i), Integer.class);
				getConfig(p, KEY_MAP_PERSISTENCE, () -> this.getMapConfig().getPersistence(),
						(f) -> this.getMapConfig().setPersistence(f), Float.class);
				getConfig(p, KEY_MAP_LOWEST_ALTITUDE, () -> this.getMapConfig().getLowAltitude(),
						(i) -> this.getMapConfig().setLowAltitude(i), Integer.class);
				getConfig(p, KEY_MAP_HIGHEST_ALTITUDE, () -> this.getMapConfig().getHighAltitude(),
						(i) -> this.getMapConfig().setHighAltitude(i), Integer.class);
				
				getConfig(p, KEY_MAP_CHUNK_WIDTH, () -> this.getMapConfig().getChunkSizeX(),
						(i) -> this.getMapConfig().setChunkSizeX(i), Integer.class);
				getConfig(p, KEY_MAP_CHUNK_HEIGHT, () -> this.getMapConfig().getChunkSizeY(),
						(i) -> this.getMapConfig().setChunkSizeY(i), Integer.class);
				
			} catch (IOException e) {
				LOG.error("Cannot load stored options from config-file \"" + CONFIG_FILE.getPath()
						+ "\". Falling back to defaults.", e);
			}
		}
	}
	
	/**
	 * Save this Options instance to {@link #CONFIG_FILE}
	 */
	public void saveToFile() {
		
		try (FileWriter w = new FileWriter(CONFIG_FILE)) {
			
			LOG.info("Saving options to \"{}\" ...", CONFIG_FILE.getPath());
			
			final Properties p = new Properties();
			
			setConfig(p, KEY_PARALLELISM, this::getParallelism);
			
			setConfig(p, KEY_FONT_FILENAME, this::getFontFile);
			setConfig(p, KEY_FONT_WIDTH, this::getFontWidth);
			setConfig(p, KEY_FONT_HEIGHT, this::getFontHeight);
			
			setConfig(p, KEY_WINDOW_WIDTH, this::getWindowWidth);
			setConfig(p, KEY_WINDOW_HEIGHT, this::getWindowHeight);
			
			setConfig(p, KEY_SCREEN_WIDTH, this::getScreenWidth);
			setConfig(p, KEY_SCREEN_HEIGHT, this::getScreenHeight);
			
			setConfig(p, KEY_SEED, this::getSeed);
			
			setConfig(p, KEY_MAP_LARGEST_FEATURE, () -> this.getMapConfig().getLargestFeature());
			setConfig(p, KEY_MAP_PERSISTENCE, () -> this.getMapConfig().getPersistence());
			setConfig(p, KEY_MAP_LOWEST_ALTITUDE, () -> this.getMapConfig().getLowAltitude());
			setConfig(p, KEY_MAP_HIGHEST_ALTITUDE, () -> this.getMapConfig().getHighAltitude());
			
			setConfig(p, KEY_MAP_CHUNK_WIDTH, () -> this.getMapConfig().getChunkSizeX());
			setConfig(p, KEY_MAP_CHUNK_HEIGHT, () -> this.getMapConfig().getChunkSizeY());
			
			p.store(w, "RL1 Options (auto-saved)");
			
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
