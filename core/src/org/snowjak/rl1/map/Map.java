/**
 * 
 */
package org.snowjak.rl1.map;

/**
 * A {@link Map} is a section of a {@link MapGenerator} that's been generated
 * out explicitly, and is capable of being interacted with.
 * 
 * @author snowjak88
 *
 */
public class Map {
	
	private final MapConfig config;
	private final MapGenerator generator;
	private final int xSize, ySize, xCenter, yCenter;
	private final float[][] heightmap;
	
	/**
	 * @param generator
	 */
	public Map(MapConfig config, MapGenerator generator, int xSize, int ySize, int xCenter, int yCenter) {
		
		this.config = config;
		this.generator = generator;
		this.xSize = xSize;
		this.ySize = ySize;
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		
		this.heightmap = new float[xSize][ySize];
		
		final int halfXSize = xSize / 2, halfYSize = ySize / 2;
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++) {
				heightmap[x][y] = (float) generator.getHeightFrac(x + xCenter - halfXSize, y + yCenter - halfYSize);
			}
	}
	
	/**
	 * Generate the neighboring {@link Map}, equal in size to this Map, in the
	 * direction ({@code xDir,yDir}), where only the <em>signs</em> of
	 * ({@code xDir,yDir}) matter. For ({@code 0,0}), this method returns this same
	 * Map instance.
	 * 
	 * @param xDir
	 * @param yDir
	 * @return
	 */
	public Map generateNextRegion(int xDir, int yDir) {
		
		if (xDir == 0 && yDir == 0)
			return this;
		
		xDir = (xDir < 0) ? -1 : (xDir > 0) ? +1 : 0;
		yDir = (yDir < 0) ? -1 : (yDir > 0) ? +1 : 0;
		
		return new Map(config, generator, xSize, ySize, xCenter + (xSize * xDir), yCenter + (ySize * yDir));
		
	}
	
}
