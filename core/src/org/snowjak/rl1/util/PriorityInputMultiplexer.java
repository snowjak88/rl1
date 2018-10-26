/**
 * 
 */
package org.snowjak.rl1.util;

import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.InputProcessor;

/**
 * @author snowjak88
 *
 */
public class PriorityInputMultiplexer implements InputProcessor {
	
	private SortedSet<PriorityInputProcessorEntry> processors = new TreeSet<>();
	
	public void addProcessor(int priority, InputProcessor inputProcessor) {
		
		processors.add(new PriorityInputProcessorEntry(priority, inputProcessor));
	}
	
	public void removeProcessor(InputProcessor inputProcessor) {
		
		processors.removeIf(e -> e.getInputProcessor() == inputProcessor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyDown(int)
	 */
	@Override
	public boolean keyDown(int keycode) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().keyDown(keycode);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyUp(int)
	 */
	@Override
	public boolean keyUp(int keycode) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().keyUp(keycode);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
	 */
	@Override
	public boolean keyTyped(char character) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().keyTyped(character);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().touchDown(screenX, screenY, pointer, button);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().touchUp(screenX, screenY, pointer, button);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().touchDragged(screenX, screenY, pointer);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#mouseMoved(int, int)
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().mouseMoved(screenX, screenY);
			if (result)
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#scrolled(int)
	 */
	@Override
	public boolean scrolled(int amount) {
		
		for (PriorityInputProcessorEntry e : processors) {
			final boolean result = e.getInputProcessor().scrolled(amount);
			if (result)
				return true;
		}
		return false;
	}
	
	public static class PriorityInputProcessorEntry implements Comparable<PriorityInputProcessorEntry> {
		
		private final int priority;
		private final InputProcessor inputProcessor;
		
		/**
		 * @param priority
		 * @param inputProcessor
		 */
		public PriorityInputProcessorEntry(int priority, InputProcessor inputProcessor) {
			
			this.priority = priority;
			this.inputProcessor = inputProcessor;
		}
		
		/**
		 * @return the priority
		 */
		public int getPriority() {
			
			return priority;
		}
		
		/**
		 * @return the inputProcessor
		 */
		public InputProcessor getInputProcessor() {
			
			return inputProcessor;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(PriorityInputProcessorEntry o) {
			
			return Integer.compare(priority, o.priority);
		}
		
	}
}
