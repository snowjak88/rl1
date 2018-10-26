/**
 * 
 */
package org.snowjak.rl1.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.AppConfig;
import org.snowjak.rl1.util.SimplexNoise.SimplexOctave;

/**
 * @author snowjak88
 *
 */
public class Map {
	
	private static final Logger LOG = LoggerFactory.getLogger(Map.class);
	
	private final MapConfig mapConfig;
	private final SimplexOctave heightNoise;
	
	/**
	 * 
	 * @param seed
	 */
	public Map(AppConfig config) {
		
		this.mapConfig = config.getMapConfig();
		
		LOG.info("Map: seed=\"{}\", largest-feature={}, persistence={}", config.getSeed(),
				mapConfig.getLargestFeature(), mapConfig.getPersistence());
		
		this.heightNoise = new SimplexOctave(config.getSeed().hashCode(), (double) mapConfig.getLargestFeature(),
				mapConfig.getPersistence());
	}
	
	/**
	 * Return this Map's height (on the interval
	 * <code>[{@link #getLowAltitude()}, {@link #getHighAltitude()}]</code>
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double getHeight(double x, double y) {
		
		final double low = (double) mapConfig.getLowAltitude(), high = (double) mapConfig.getHighAltitude();
		return getHeightFrac(x, y) * (high - low) + low;
	}
	
	/**
	 * Return this Map's height (on the interval <code>[0, 1]</code>).
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double getHeightFrac(double x, double y) {
		
		return (heightNoise.noise(x, y) + 1d) / 2d;
	}
}
