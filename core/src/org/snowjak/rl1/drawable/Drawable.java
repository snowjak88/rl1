/**
 * 
 */
package org.snowjak.rl1.drawable;

import com.badlogic.gdx.graphics.Color;

/**
 * @author snowjak88
 *
 */
public class Drawable {
	
	/**
	 * The set of possible representations associated with this Drawable
	 */
	public char[][][] representation = null;
	public Color color = null;
	
	/**
	 * Get the 1st representation of this drawable. Equivalent to
	 * {@link #getRepresentation(int) getRepresentation(0)}.
	 * 
	 * @return
	 */
	public char[][] getRepresentation() {
		
		return getRepresentation(0);
	}
	
	/**
	 * Get the {@code i}th representation of this drawable.
	 * 
	 * @param i
	 * @return <code>null</code> if this drawable's representation has not been
	 *         defined
	 */
	public char[][] getRepresentation(int i) {
		
		return representation[i];
	}
	
	public boolean hasColor() {
		
		return (color != null);
	}
	
	/**
	 * Get the number of possible representations of this drawable.
	 * 
	 * @return
	 */
	public int getCountRepresentations() {
		
		if (representation == null)
			return 0;
		
		return representation.length;
	}
	
	public int getWidth() {
		
		if (getCountRepresentations() < 1)
			return 0;
		
		return representation[0].length;
	}
	
	public int getHeight() {
		
		if (getWidth() < 1)
			return 0;
		
		return representation[0][0].length;
	}
}
