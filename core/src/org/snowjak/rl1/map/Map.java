/**
 * 
 */
package org.snowjak.rl1.map;

import org.snowjak.rl1.util.SimplexNoise.SimplexOctave;

/**
 * @author snowjak88
 *
 */
public class Map {
	
	private final int lowAltitude, highAltitude;
	private final SimplexOctave heightNoise;
	
	/**
	 * 
	 * @param seed
	 */
	public Map(String seed, int largestFeature, float persistence, int lowAltitude, int highAltitude) {
		
		this.lowAltitude = lowAltitude;
		this.highAltitude = highAltitude;
		this.heightNoise = new SimplexOctave(seed.hashCode(), (double) largestFeature, persistence);
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
		
		final double low = (double) lowAltitude, high = (double) highAltitude;
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
	
	/**
	 * @return the lowAltitude
	 */
	public int getLowAltitude() {
		
		return lowAltitude;
	}
	
	/**
	 * @return the highAltitude
	 */
	public int getHighAltitude() {
		
		return highAltitude;
	}
	
}
