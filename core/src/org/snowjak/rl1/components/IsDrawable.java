/**
 * 
 */
package org.snowjak.rl1.components;

import org.snowjak.rl1.drawable.Drawable;
import org.snowjak.rl1.providers.DrawableProvider;

import com.artemis.Component;
import com.artemis.PooledComponent;

/**
 * References an {@link Drawable} instance by name.
 * 
 * @author snowjak88
 *
 */
public class IsDrawable extends Component {
	
	private static final DrawableProvider PROVIDER = new DrawableProvider();
	
	public String name = null;
	
	/**
	 * Get the referenced {@link Drawable} instance, or <code>null</code> if it
	 * cannot be found.
	 * 
	 * @return
	 */
	public Drawable getDrawable() {
		
		return PROVIDER.get(name).orElse(null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.PooledComponent#reset()
	 */
//	@Override
//	protected void reset() {
//		
//		name = null;
//	}
}
