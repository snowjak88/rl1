/**
 * 
 */
package org.snowjak.rl1.map;

public class MapConfig {
	
	private int lowAltitude, highAltitude;
	private int largestFeature;
	private float persistence;
	
	/**
	 * @return the lowAltitude
	 */
	public int getLowAltitude() {
		
		return lowAltitude;
	}
	
	/**
	 * @param lowAltitude
	 *            the lowAltitude to set
	 */
	public void setLowAltitude(int lowAltitude) {
		
		this.lowAltitude = lowAltitude;
	}
	
	/**
	 * @return the highAltitude
	 */
	public int getHighAltitude() {
		
		return highAltitude;
	}
	
	/**
	 * @param highAltitude
	 *            the highAltitude to set
	 */
	public void setHighAltitude(int highAltitude) {
		
		this.highAltitude = highAltitude;
	}
	
	/**
	 * @return the largestFeature
	 */
	public int getLargestFeature() {
		
		return largestFeature;
	}
	
	/**
	 * @param largestFeature
	 *            the largestFeature to set
	 */
	public void setLargestFeature(int largestFeature) {
		
		this.largestFeature = largestFeature;
	}
	
	/**
	 * @return the persistence
	 */
	public float getPersistence() {
		
		return persistence;
	}
	
	/**
	 * @param persistence
	 *            the persistence to set
	 */
	public void setPersistence(float persistence) {
		
		this.persistence = persistence;
	}
	
}