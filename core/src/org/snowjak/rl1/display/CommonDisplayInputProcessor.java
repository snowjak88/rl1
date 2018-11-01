/**
 * 
 */
package org.snowjak.rl1.display;

import org.snowjak.rl1.util.listener.Listenable;

/**
 * Provides input-handling to AbstractDisplay to handle common events.
 * 
 * @author snowjak88
 *
 */
public class CommonDisplayInputProcessor extends DisplayInputProcessor
		implements Listenable<DisplayInputProcessor.DisplayEvent> {
	
	public static final int COMMON_DISPLAY_INPUT_PRIORITY = 64;
	
	/**
	 * @param display
	 * @param priority
	 */
	public CommonDisplayInputProcessor() {
		
		super(COMMON_DISPLAY_INPUT_PRIORITY);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputAdapter#scrolled(int)
	 */
	@Override
	public boolean scrolled(int amount) {
		
		final float multiplier = (float) Math.pow(2, amount);
		
		fireEvent(new UpdateScaleEvent(multiplier));
		
		return true;
	}
	
	public class UpdateScaleEvent implements DisplayEvent {
		
		private final float scaleMultiplier;
		
		/**
		 * @param scale
		 */
		public UpdateScaleEvent(float scaleMultiplier) {
			
			this.scaleMultiplier = scaleMultiplier;
		}
		
		/**
		 * @return the scale
		 */
		public float getScaleMultiplier() {
			
			return scaleMultiplier;
		}
		
	}
}