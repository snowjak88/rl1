/**
 * 
 */
package org.snowjak.rl1.map;

/**
 * A {@link MapChunk} is a section of a {@link MapGenerator} that's been
 * generated out explicitly, and is capable of being interacted with.
 * 
 * @author snowjak88
 *
 */
public class MapChunk {
	
	private final MapConfig config;
	private final MapGenerator generator;
	private final int xSize, ySize, xCenter, yCenter;
	private final float[][] heightmap;
	
	/**
	 * @param generator
	 */
	public MapChunk(MapConfig config, MapGenerator generator, int xCenter, int yCenter) {
		
		this.config = config;
		this.generator = generator;
		this.xSize = config.getChunkSizeX();
		this.ySize = config.getChunkSizeY();
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
	 * Returns this Map's height at the given point (expressed as an absolute
	 * altitude, between {@link MapConfig#getLowAltitude()} and
	 * {@link MapConfig#getHighAltitude()}, or {@code 0} if the given point is not
	 * within this map-chunk.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public float getHeight(int x, int y) {
		
		if (!isInChunk(x, y))
			return 0;
		
		return getHeightFrac(x, y) * (config.getHighAltitude() - config.getLowAltitude()) + config.getLowAltitude();
	}
	
	/**
	 * Returns this Map's height at the given point (expressed as a fraction in
	 * {@code [0,1]}), or {@code 0} if the given point is not within this map-chunk.
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @see #getHeight(int, int)
	 * @see #isInChunk(int, int)
	 * @see #generateNextRegion(int, int)
	 */
	public float getHeightFrac(int x, int y) {
		
		if (!isInChunk(x, y))
			return 0;
		
		return heightmap[getChunkX(x)][getChunkY(y)];
	}
	
	/**
	 * Determines if the given map-point is within this MapChunk.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInChunk(int x, int y) {
		
		final int chunkX = getChunkX(x), chunkY = getChunkY(y);
		
		return (chunkX >= 0 && chunkX < xSize && chunkY >= 0 && chunkY < ySize);
	}
	
	/**
	 * Convert the given map (i.e., world) X-coordinate into a chunk-relative
	 * coordinate, which spans the interval {@code [0, xSize)}.
	 * 
	 * @param x
	 * @return
	 */
	public int getChunkX(int x) {
		
		return x - xCenter + (xSize / 2);
	}
	
	/**
	 * Convert the given map (i.e., world) Y-coordinate into a chunk-relative
	 * coordinate, which spans the interval {@code [0, ySize)}.
	 * 
	 * @param y
	 * @return
	 */
	public int getChunkY(int y) {
		
		return y - yCenter + (ySize / 2);
	}
	
	/**
	 * Convert the given chunk-relative coordinate (spanning the interval
	 * {@code [0, xSize)}) to a map (i.e., world) coordinate.
	 * 
	 * @param chunkX
	 * @return
	 */
	public int getMapX(int chunkX) {
		
		return chunkX - (xSize / 2) + xCenter;
	}
	
	/**
	 * Convert the given chunk-relative coordinate (spanning the interval
	 * {@code [0, ySize)}) to a map (i.e., world) coordinate.
	 * 
	 * @param chunkX
	 * @return
	 */
	public int getMapY(int chunkY) {
		
		return chunkY - (ySize / 2) + yCenter;
	}
	
	public int getSizeX() {
		
		return xSize;
	}
	
	public int getSizeY() {
		
		return ySize;
	}
	
	/**
	 * Generate the neighboring {@link MapChunk}, equal in size to this MapChunk, in
	 * the direction ({@code xDir,yDir}), where only the <em>signs</em> of
	 * ({@code xDir,yDir}) matter. For ({@code 0,0}), this method returns this same
	 * MapChunk instance.
	 * 
	 * @param xDir
	 * @param yDir
	 * @return
	 */
	public MapChunk generateNextRegion(int xDir, int yDir) {
		
		if (xDir == 0 && yDir == 0)
			return this;
		
		xDir = (xDir < 0) ? -1 : (xDir > 0) ? +1 : 0;
		yDir = (yDir < 0) ? -1 : (yDir > 0) ? +1 : 0;
		
		return new MapChunk(config, generator, xCenter + (xSize * xDir), yCenter + (ySize * yDir));
		
	}
}