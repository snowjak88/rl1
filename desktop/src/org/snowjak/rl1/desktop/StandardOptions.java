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
	
	@Option(names = "--mapGenerator-feature-size", paramLabel = "FEATURE-SIZE", description = "The mapGenerator's largest features will be this many cells wide/long.")
	private int mapLargestFeature = 128;
	
	@Option(names = "--mapGenerator-feature-persistence", paramLabel = "FRACTION", description = "The mapGenerator's high-frequency components will be blurred by this fraction, where 0 is most-smooth and 1 is least-smooth.")
	private float mapFeaturePersistence = 0.6f;
	
	@Option(names = "--mapGenerator-lowest-altitude", paramLabel = "ALTITUDE", description = "The mapGenerator's lowest altitude will equal this value")
	private int mapLowestAltitude = 0;
	
	@Option(names = "--mapGenerator-highest-altitude", paramLabel = "ALTITUDE", description = "The mapGenerator's highest altitude will equal this value")
	private int mapHighestAltitude = 300;
	
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
	
}
