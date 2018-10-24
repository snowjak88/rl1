/**
 * 
 */
package org.snowjak.rl1.components;

import java.awt.Color;

import org.snowjak.rl1.drawing.DrawableLayer;

import com.artemis.PooledComponent;

import asciiPanel.AsciiPanel;

/**
 * Denotes that an entity can be drawn.
 * 
 * @author snowjak88
 *
 */
public class IsDrawable extends PooledComponent {
	
	public String drawable = "";
	public Color color = AsciiPanel.white;
	
	public DrawableLayer layer = DrawableLayer.BACKGROUND;
	
	public IsDrawable() {
		
	}
	
	/**
	 * @param drawable
	 */
	public IsDrawable(String drawable) {
		
		this(drawable, null);
	}
	
	/**
	 * @param drawable
	 * @param layer
	 */
	public IsDrawable(String drawable, DrawableLayer layer) {
		
		this(drawable, AsciiPanel.white, layer);
	}
	
	/**
	 * @param drawable
	 * @param color
	 * @param layer
	 */
	public IsDrawable(String drawable, Color color, DrawableLayer layer) {
		
		this.drawable = drawable;
		this.color = color;
		this.layer = layer;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
	@Override
	protected void reset() {
		
		this.drawable = "";
		this.color = AsciiPanel.white;
		this.layer = DrawableLayer.BACKGROUND;
	}
	
}
