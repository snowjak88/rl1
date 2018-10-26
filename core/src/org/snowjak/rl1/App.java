/**
 * 
 */
package org.snowjak.rl1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.display.MainDisplay;
import org.snowjak.rl1.util.PriorityInputMultiplexer;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

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
	 * The {@link AppConfig} we're running with.
	 */
	public final AppConfig appConfig;
	
	/**
	 * The {@link MainDisplay} presented on-screen.
	 */
	public MainDisplay display;
	
	/**
	 * The central {@link ExecutorService}.
	 */
	public final ListeningExecutorService executor;
	
	public final PriorityInputMultiplexer input;
	
	public App(AppConfig appConfig) {
		
		super();
		
		appConfig.loadFromFile();
		Context.initialize(this);
		
		this.appConfig = appConfig;
		this.events = new EventSystem();
		this.input = new PriorityInputMultiplexer();
		
		//@formatter:off
		final WorldConfiguration worldConfig =
				new WorldConfigurationBuilder()
					.with(new ExtendedComponentMapperPlugin())
					.with(events)
				.build();
		//@formatter:on
		
		this.odb = new World(worldConfig);
		
		if (appConfig.getParallelism() <= 0) {
			LOG.info("Not using parallelism.");
			executor = MoreExecutors.newDirectExecutorService();
		} else {
			LOG.info("Using parallelism level {}.", appConfig.getParallelism());
			executor = MoreExecutors.listeningDecorator(MoreExecutors.getExitingExecutorService(
					(ThreadPoolExecutor) Executors.newFixedThreadPool(appConfig.getParallelism())));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {
		
		Gdx.input.setInputProcessor(input);
		
		display = new MainDisplay();
		
		setScreen(display);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Game#dispose()
	 */
	@Override
	public void dispose() {
		
		super.dispose();
		this.appConfig.dispose();
	}
	
}
