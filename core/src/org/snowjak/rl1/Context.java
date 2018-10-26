/**
 * 
 */
package org.snowjak.rl1;

import org.snowjak.rl1.display.MainDisplay;
import org.snowjak.rl1.util.PriorityInputMultiplexer;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.google.common.util.concurrent.ListeningExecutorService;

import net.mostlyoriginal.api.event.common.Event;
import net.mostlyoriginal.api.event.common.EventSystem;

/**
 * Holds game-state in a singleton.
 * 
 * @author snowjak88
 *
 */
public class Context {
	
	private static Context _instance = null;
	
	/**
	 * Get the Context singleton.
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             if the application has not yet initialized the Context
	 */
	public static Context get() {
		
		if (_instance == null)
			throw new IllegalStateException(
					"Context is not initialized -- you must call Context.initialize(App) from App before using the Context!");
		
		return _instance;
	}
	
	static void initialize(App app) {
		
		if (_instance == null)
			_instance = new Context(app);
	}
	
	private final App app;
	
	/**
	 * The {@link App}/{@link Game}/{@link ApplicationListener} instance. May be
	 * <code>null</code> if LibGDX has not finished starting yet.
	 */
	public App getApp() {
		
		return app;
	}
	
	/**
	 * The {@link AppConfig} instance we're using for this execution.
	 */
	public AppConfig appConfig() {
		
		return app.appConfig;
	}
	
	/**
	 * The Artemis-ODB {@link World}.
	 */
	public World odb() {
		
		return app.odb;
	}
	
	/**
	 * The {@link EventSystem}, for synchronously dispatching {@link Event}s.
	 */
	public EventSystem es() {
		
		return app.events;
	}
	
	/**
	 * The {@link PriorityInputMultiplexer} on which components can register
	 * {@link InputProcessor}s.
	 * 
	 * @return
	 */
	public PriorityInputMultiplexer in() {
		
		return app.input;
	}
	
	/**
	 * The {@link ListeningExecutorService} provided by the application.
	 * 
	 * @return
	 */
	public ListeningExecutorService exe() {
		
		return app.executor;
	}
	
	/**
	 * The {@link MainDisplay} presented on-screen.
	 * 
	 * @return
	 */
	public MainDisplay display() {
		
		return app.display;
	}
	
	private Context(App app) {
		
		this.app = app;
	}
}
