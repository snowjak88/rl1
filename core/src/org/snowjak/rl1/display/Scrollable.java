/**
 * 
 */
package org.snowjak.rl1.display;

/**
 * Denotes that a display can scroll.
 * 
 * @author snowjak88
 *
 */
public interface Scrollable {
	
	public void scroll(int dx, int dy);
	
	public void scrollTo(int x, int y);
	
	public boolean isOnScreen(int x, int y);
	
	public int getScrollX();
	
	public int getScrollY();
}
