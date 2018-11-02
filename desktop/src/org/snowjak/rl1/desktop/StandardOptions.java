/**
 * 
 */
package org.snowjak.rl1.desktop;

import java.util.UUID;

import picocli.CommandLine.Option;

/**
 * @author snowjak88
 *
 */
public class StandardOptions {
	
	@Option(names = "--parallelism", paramLabel = "THREADS", description = "How many background workers are allowed.")
	private int parallelism = Runtime.getRuntime().availableProcessors();
	
	@Option(names = "--seed", paramLabel = "SEED-VALUE", description = "Seed with which to initialize the random-number generator.")
	private String seed = UUID.randomUUID().toString();
	
	@Option(names = "--map-feature-size", paramLabel = "FEATURE-SIZE", description = "The map's largest features will be this many cells wide/long.")
	private int mapLargestFeature = 128;
	
	@Option(names = "--map-feature-persistence", paramLabel = "FRACTION", description = "The map's high-frequency components will be blurred by this fraction, where 0 is most-smooth and 1 is least-smooth.")
	private float mapFeaturePersistence = 0.6f;
	
	@Option(names = "--map-lowest-altitude", paramLabel = "ALTITUDE", description = "The map's lowest altitude will equal this value")
	private int mapLowestAltitude = 0;
	
	@Option(names = "--map-highest-altitude", paramLabel = "ALTITUDE", description = "The map's highest altitude will equal this value")
	private int mapHighestAltitude = 300;
	
	@Option(names = "--map-chunk-width", paramLabel = "WIDTH-IN-TILES", description = "The map will generate chunks of so many tiles wide.", defaultValue = "128")
	private int mapChunkWidth = 128;
	
	@Option(names = "--map-chunk-height", paramLabel = "HEIGHT-IN-TILES", description = "The map will generate chunks of so many tiles high.", defaultValue = "128")
	private int mapChunkHeight = 128;
	
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
	 * @return the mapFeaturePersistence
	 */
	public float getMapFeaturePersistence() {
		
		return mapFeaturePersistence;
	}
	
	/**
	 * @param mapFeaturePersistence
	 *            the mapFeaturePersistence to set
	 */
	public void setMapFeaturePersistence(float mapFeaturePersistence) {
		
		this.mapFeaturePersistence = mapFeaturePersistence;
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
	 * @return the mapChunkWidth
	 */
	public int getMapChunkWidth() {
		
		return mapChunkWidth;
	}

	
	/**
	 * @param mapChunkWidth the mapChunkWidth to set
	 */
	public void setMapChunkWidth(int mapChunkWidth) {
		
		this.mapChunkWidth = mapChunkWidth;
	}

	
	/**
	 * @return the mapChunkHeight
	 */
	public int getMapChunkHeight() {
		
		return mapChunkHeight;
	}

	
	/**
	 * @param mapChunkHeight the mapChunkHeight to set
	 */
	public void setMapChunkHeight(int mapChunkHeight) {
		
		this.mapChunkHeight = mapChunkHeight;
	}
	
}
