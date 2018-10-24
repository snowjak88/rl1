/**
 * 
 */
package org.snowjak.rl1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.config.Options;
import org.snowjak.rl1.screen.AsciiScreen;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;

import net.mostlyoriginal.api.event.common.Event;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.ExtendedComponentMapperPlugin;

/**
 * @author snowjak88
 *
 */
public class App extends Game {
	
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
	/**
	 * The Artemis-ODB {@link World}.
	 */
	public final World odb;
	/**
	 * The {@link EventSystem}, for synchronously dispatching {@link Event}s.
	 */
	public final EventSystem events;
	
	/**
	 * The {@link Options} we're running with.
	 */
	public final Options options;
	
	public App(Options options) {
		
		super();
		
		this.options = options;
		this.events = new EventSystem();
		
		//@formatter:off
		final WorldConfiguration worldConfig =
				new WorldConfigurationBuilder()
					.with(new ExtendedComponentMapperPlugin())
					.with(events)
				.build();
		//@formatter:on
		
		this.odb = new World(worldConfig);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {
		
		final AsciiScreen screen = new AsciiScreen(options.getScreenWidth(), options.getScreenHeight(),
				options.getFont());
		setScreen(screen);
		
		screen.color();
		screen.background(Color.BLACK);
		screen.write("This is the song we sing for ");
		screen.foreground(Color.RED);
		screen.write("Col");
		screen.foreground(Color.WHITE);
		screen.write("ora");
		screen.foreground(Color.BLUE);
		screen.write("do!");
		screen.color();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Game#dispose()
	 */
	@Override
	public void dispose() {
		
		super.dispose();
		
		this.options.dispose();
	}
	
}
