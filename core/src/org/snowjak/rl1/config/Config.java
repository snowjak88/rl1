/**
 * 
 */
package org.snowjak.rl1.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.drawing.ascii.AsciiFont;
import org.snowjak.rl1.util.Converters;

import com.badlogic.gdx.utils.Disposable;

import picocli.CommandLine.Command;

/**
 * @author snowjak88
 *
 */
@Command
public class Config implements Disposable {
	
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	
	/**
	 * Default config-file name
	 */
	private static final File CONFIG_FILE = new File("./rl1.properties");
	
	private static final String KEY_PARALLELISM = "parallelism";
	
	private static final String KEY_FONT_FILENAME = "font.filename", KEY_FONT_WIDTH = "font.width",
			KEY_FONT_HEIGHT = "font.height";
	
	private static final String KEY_SCREEN_WIDTH = "screen.width", KEY_SCREEN_HEIGHT = "screen.height";
	
	private static final String KEY_SEED = "seed";
	
	private static final String KEY_MAP_LARGEST_FEATURE = "map.features.largest";
	private static final String KEY_MAP_PERSISTENCE = "map.features.persistence";
	private static final String KEY_MAP_LOWEST_ALTITUDE = "map.features.lowest",
			KEY_MAP_HIGHEST_ALTITUDE = "map.features.highest";
	
	private int parallelism = Runtime.getRuntime().availableProcessors();
	
	private File fontFile = new File("fonts/cp437_12x12.png");
	private Integer fontWidth = 12;
	private Integer fontHeight = 12;
	
	private transient AsciiFont asciiFont = null;
	
	private int screenWidth = 80;
	private int screenHeight = 25;
	
	private String seed = "";
	
	private int mapLargestFeature = 1000;
	private float mapFeaturePersistence = 0.3f;
	private int mapLowestAltitude = 0;
	private int mapHighestAltitude = 300;
	
	/**
	 * Construct a new {@link Config} instance. If {@link #CONFIG_FILE} exists and
	 * can be loaded, this will load the contents of {@link #CONFIG_FILE} into this
	 * {@link Config} instance.
	 */
	public Config() {
		
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
				
				getConfig(p, KEY_SCREEN_WIDTH, this::getScreenWidth, this::setScreenWidth, Integer.class);
				getConfig(p, KEY_SCREEN_HEIGHT, this::getScreenHeight, this::setScreenHeight, Integer.class);
				
				getConfig(p, KEY_SEED, this::getSeed, this::setSeed, String.class);
				
				getConfig(p, KEY_MAP_LARGEST_FEATURE, this::getMapLargestFeature, this::setMapLargestFeature,
						Integer.class);
				getConfig(p, KEY_MAP_PERSISTENCE, this::getMapFeaturePersistence, this::setMapFeaturePersistence,
						Float.class);
				getConfig(p, KEY_MAP_LOWEST_ALTITUDE, this::getMapLowestAltitude, this::setMapLowestAltitude,
						Integer.class);
				getConfig(p, KEY_MAP_HIGHEST_ALTITUDE, this::getMapHighestAltitude, this::setMapHighestAltitude,
						Integer.class);
				
			} catch (IOException e) {
				LOG.error("Cannot load stored options from config-file \"" + CONFIG_FILE.getPath()
						+ "\". Falling back to defaults.", e);
			}
		}
	}
	
	private <T> void getConfig(Properties p, String propertyKey, Supplier<T> getter, Consumer<T> setter,
			Class<T> type) {
		
		try {
			setter.accept(Converters.convert(p.getProperty(propertyKey, Converters.convert(getter.get(), String.class)),
					type));
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
	 * @return the mapLargestFeature
	 */
	public int getMapLargestFeature() {
		
		return mapLargestFeature;
	}
	
	/**
	 * @param mapLargestFeature
	 *            the mapLargestFeature to set
	 */
	public void setMapLargestFeature(int mapLargestFeature) {
		
		this.mapLargestFeature = mapLargestFeature;
	}
	
	/**
	 * @return the persistence
	 */
	public float getMapFeaturePersistence() {
		
		return mapFeaturePersistence;
	}
	
	/**
	 * @param persistence
	 *            the persistence to set
	 */
	public void setMapFeaturePersistence(float persistence) {
		
		this.mapFeaturePersistence = persistence;
	}
	
	/**
	 * @return the mapLowestAltitude
	 */
	public int getMapLowestAltitude() {
		
		return mapLowestAltitude;
	}
	
	/**
	 * @param mapLowestAltitude
	 *            the mapLowestAltitude to set
	 */
	public void setMapLowestAltitude(int mapLowestAltitude) {
		
		this.mapLowestAltitude = mapLowestAltitude;
	}
	
	/**
	 * @return the mapHighestAltitude
	 */
	public int getMapHighestAltitude() {
		
		return mapHighestAltitude;
	}
	
	/**
	 * @param mapHighestAltitude
	 *            the mapHighestAltitude to set
	 */
	public void setMapHighestAltitude(int mapHighestAltitude) {
		
		this.mapHighestAltitude = mapHighestAltitude;
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
			
			setConfig(p, KEY_SCREEN_WIDTH, this::getScreenWidth);
			setConfig(p, KEY_SCREEN_HEIGHT, this::getScreenHeight);
			
			setConfig(p, KEY_SEED, this::getSeed);
			
			setConfig(p, KEY_MAP_LARGEST_FEATURE, this::getMapLargestFeature);
			setConfig(p, KEY_MAP_PERSISTENCE, this::getMapFeaturePersistence);
			setConfig(p, KEY_MAP_LOWEST_ALTITUDE, this::getMapLowestAltitude);
			setConfig(p, KEY_MAP_HIGHEST_ALTITUDE, this::getMapHighestAltitude);
			
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
