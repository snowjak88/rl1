/**
 * 
 */
package org.snowjak.rl1.map;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.archetypes.Archetypes;
import org.snowjak.rl1.components.HasPosition;
import org.snowjak.rl1.components.IsDrawable;
import org.snowjak.rl1.drawing.DrawableLayer;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

/**
 * @author snowjak88
 *
 */
@Wire
public class Map {
	
	private static final Random RND = new Random(System.currentTimeMillis());
	
	private final int xSize, ySize, n;
	
	private final int[] pX, pY;
	
	private M<IsDrawable> isDrawable;
	private M<HasPosition> hasPosition;
	
	@SkipWire
	private EntitySubscription isDrawableSubscription = null;
	
	public Map(int xSize, int ySize, int n) {
		
		assert (xSize > 0);
		assert (ySize > 0);
		assert (n > 0);
		
		this.xSize = xSize;
		this.ySize = ySize;
		this.n = n;
		
		final int pointCount = (int) Math.sqrt((double) (xSize * ySize));
		assert (pointCount > n);
		
		pX = new int[pointCount];
		pY = new int[pointCount];
		
		for (int i = 0; i < pointCount; i++) {
			pX[i] = RND.nextInt(xSize);
			pY[i] = RND.nextInt(ySize);
		}
		
	}
	
	/**
	 * <ol>
	 * <li>If any exist, find all {@link IsDrawable} entities that are drawing on
	 * {@link DrawableLayer#TERRAIN} and refresh them</li>
	 * <li>For each map-position not so refreshed, create a new {@link IsDrawable}
	 * entity</li>
	 * </ol>
	 * This assumes that a {@link World} instance is available through the
	 * {@link Context}.
	 */
	public void createEntities() {
		
		final boolean[][] refreshed = new boolean[xSize][ySize];
		
		if (isDrawableSubscription == null)
			isDrawableSubscription = Context.get().odb.getAspectSubscriptionManager()
					.get(Aspect.all(IsDrawable.class, HasPosition.class));
		
		final int[] ids = Arrays.copyOf(isDrawableSubscription.getEntities().getData(),
				isDrawableSubscription.getEntities().size());
		for (int id : ids) {
			if (isDrawable.get(id).layer == DrawableLayer.TERRAIN) {
				
				final int x = hasPosition.get(id).x;
				final int y = hasPosition.get(id).y;
				refreshTile(id, x, y);
				refreshed[x][y] = true;
				
			}
		}
		
		for (int x = 0; x < pX.length; x++)
			for (int y = 0; y < pY.length; y++) {
				if (refreshed[x][y])
					continue;
				
				final World w = Context.get().odb;
				final int id = w.create(Archetypes.terrain(w));
				refreshTile(id, x, y);
				refreshed[x][y] = true;
			}
	}
	
	public void refreshTile(int entityID, int x, int y) {
		
		final IsDrawable d = getDrawable(x, y),
				e = (isDrawable.has(entityID)) ? isDrawable.get(entityID) : isDrawable.create(entityID);
		e.drawable = d.drawable;
		e.color = d.color;
		e.layer = DrawableLayer.TERRAIN;
		
		final HasPosition p = (hasPosition.has(entityID)) ? hasPosition.get(entityID) : hasPosition.create(entityID);
		p.x = x;
		p.y = y;
	}
	
	public IsDrawable getDrawable(int x, int y) {
		
		final IsDrawable d = new IsDrawable((RND.nextDouble() > 0.5) ? "." : ",", DrawableLayer.TERRAIN);
		d.color = lerp(new Color(85, 165, 48), new Color(76, 53, 0), getNoise(x, y));
		return d;
	}
	
	private Color lerp(Color c1, Color c2, double f) {
		
		return new Color((float) clamp(lerp(c1.getRed(), c2.getRed(), f), 0, 1),
				(float) clamp(lerp(c1.getGreen(), c2.getGreen(), f), 0, 1),
				(float) clamp(lerp(c1.getBlue(), c2.getBlue(), f), 0, 1));
	}
	
	private double clamp(double x, double min, double max) {
		
		return Math.max(Math.min(x, max), min);
	}
	
	private double lerp(double i1, double i2, double f) {
		
		return ((i2 - i1) * f) + i1;
	}
	
	public double getNoise(int x, int y) {
		
		int nth = 0, lastI = 0;
		final double[] distances = new double[pX.length];
		for (int i = 0; i < distances.length; i++)
			distances[i] = -1d;
		
		while (nth < n) {
			double bestDistance = Double.MAX_VALUE;
			int bestI = 0;
			
			for (int i = 0; i < pX.length; i++) {
				
				final double currDistance = Math.pow((double) pX[i] - (double) x, 2d)
						+ Math.pow((double) pY[i] - (double) y, 2d);
				
				if (currDistance < bestDistance && distances[i] < 0d) {
					bestDistance = currDistance;
					bestI = i;
				}
			}
			
			distances[bestI] = bestDistance;
			lastI = bestI;
			nth++;
			
		}
		
		return Math.sqrt(distances[lastI]);
	}
	
	/**
	 * @return the xSize
	 */
	public int getxSize() {
		
		return xSize;
	}
	
	/**
	 * @return the ySize
	 */
	public int getySize() {
		
		return ySize;
	}
	
}
