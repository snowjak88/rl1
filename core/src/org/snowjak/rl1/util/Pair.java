/**
 * 
 */
package org.snowjak.rl1.util;

/**
 * @author snowjak88
 *
 */
public class Pair<I, J> {
	
	private I first = null;
	private J second = null;
	
	/**
	 * @param first
	 * @param second
	 */
	public Pair(I first, J second) {
		
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @return the first
	 */
	public I getFirst() {
		
		return first;
	}
	
	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(I first) {
		
		this.first = first;
	}
	
	/**
	 * @return the second
	 */
	public J getSecond() {
		
		return second;
	}
	
	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(J second) {
		
		this.second = second;
	}
	
}
