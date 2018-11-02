/**
 * 
 */
package org.snowjak.rl1.map;

import java.util.HashMap;
import java.util.Map;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.components.IsDrawable;
import org.snowjak.rl1.util.IntPair;

import com.artemis.utils.IntBag;

/**
 * A {@link MapChunk} is a section of a {@link MapGenerator} that's been
 * generated out explicitly, and is capable of being interacted with.
 * 
 * @author snowjak88
 *
 */
public class MapChunk {
	
	private static final IntBag EMPTY_BAG = new IntBag(0);
	
	private final MapConfig config;
	private final MapGenerator generator;
	private final int xSize, ySize, xCenter, yCenter;
	private final float[][] heightmap;
	
	/**
	 * What entities are stored at each location?
	 */
	private final Map<IntPair, IntBag> entitiesByLocation = new HashMap<>();
	/**
	 * What location is associated with an entity?
	 */
	private final Map<Integer, IntPair> locationByEntity = new HashMap<>();
	
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
				
				if (heightmap[x][y] < 0.5) {
					
					final int entityID = Context.get().odb().create();
					final IsDrawable drawable = new IsDrawable();
					drawable.name = "water";
					
					Context.get().odb().edit(entityID).add(drawable);
					addEntityAt(entityID, getMapPoint(new IntPair(x, y)));
				}
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
	
	public void addEntityAt(int entityID, IntPair mapPoint) {
		
		if (!isInChunk(mapPoint))
			return;
		
		entitiesByLocation.computeIfAbsent(mapPoint, (p) -> new IntBag()).add(entityID);
		locationByEntity.put(entityID, mapPoint);
	}
	
	public void removeEntity(int entityID) {
		
		final IntPair removeFrom = locationByEntity.get(entityID);
		
		if (removeFrom == null)
			return;
		
		locationByEntity.remove(entityID);
		entitiesByLocation.get(removeFrom).removeValue(entityID);
	}
	
	public void moveEntityTo(int entityID, IntPair mapPoint) {
		
		removeEntity(entityID);
		addEntityAt(entityID, mapPoint);
	}
	
	public IntBag getEntitiesAt(IntPair mapPoint) {
		
		return entitiesByLocation.getOrDefault(mapPoint, EMPTY_BAG);
	}
	
	/**
	 * Determines if the given map-point is within this MapChunk.
	 * 
	 * @param mapPoint
	 * @return
	 */
	public boolean isInChunk(IntPair mapPoint) {
		
		return isInChunk(mapPoint.getFirst(), mapPoint.getSecond());
	}
	
	/**
	 * Determines if the given map-point is within this MapChunk.
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public boolean isInChunk(int mapX, int mapY) {
		
		final int chunkX = getChunkX(mapX), chunkY = getChunkY(mapY);
		
		return isInChunk_internal(chunkX, chunkY);
	}
	
	protected boolean isInChunk_internal(IntPair chunkPoint) {
		
		return isInChunk_internal(chunkPoint.getFirst(), chunkPoint.getSecond());
	}
	
	protected boolean isInChunk_internal(int chunkX, int chunkY) {
		
		return (chunkX >= 0 && chunkX < xSize && chunkY >= 0 && chunkY < ySize);
	}
	
	/**
	 * Convert the given map (i.e., world) point into a chunk-relative point.
	 * 
	 * @param mapPoint
	 * @return
	 */
	public IntPair getChunkPoint(IntPair mapPoint) {
		
		return new IntPair(getChunkX(mapPoint.getFirst()), getChunkY(mapPoint.getSecond()));
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
	 * Convert the chunk-relative point to a map (i.e., world) point.
	 * 
	 * @param chunkPoint
	 * @return
	 */
	public IntPair getMapPoint(IntPair chunkPoint) {
		
		return new IntPair(getMapX(chunkPoint.getFirst()), getMapY(chunkPoint.getSecond()));
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