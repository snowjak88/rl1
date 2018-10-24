/**
 * 
 */
package org.snowjak.rl1;

import org.snowjak.rl1.config.Options;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

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
	 */
	public static Context get() {
		
		if (_instance == null)
			_instance = new Context();
		
		return _instance;
	}
	
	/**
	 * The {@link App}/{@link Game}/{@link ApplicationListener} instance.
	 */
	public final App app;
	/**
	 * The {@link Options} instance we're using for this execution.
	 */
	public final Options opt;
	/**
	 * The Artemis-ODB {@link World}.
	 */
	public final World odb;
	
	/**
	 * The {@link EventSystem}, for synchronously dispatching {@link Event}s.
	 */
	public final EventSystem es;
	
	private Context() {
		
		this.app = (App) Gdx.app.getApplicationListener();
		this.opt = app.options;
		this.odb = app.odb;
		this.es = app.events;
	}
}
