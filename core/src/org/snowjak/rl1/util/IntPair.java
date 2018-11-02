/**
 * 
 */
package org.snowjak.rl1.util;

/**
 * Specialization of {@link Pair} to {@code int} primitives.
 * 
 * @author snowjak88
 *
 */
public class IntPair {
	
	private int first = 0, second = 0;
	
	/**
	 * @param first
	 * @param second
	 */
	public IntPair(int first, int second) {
		
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @return the first
	 */
	public int getFirst() {
		
		return first;
	}
	
	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(int first) {
		
		this.first = first;
	}
	
	/**
	 * @return the second
	 */
	public int getSecond() {
		
		return second;
	}
	
	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(int second) {
		
		this.second = second;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntPair other = (IntPair) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		return true;
	}
	
}
