/**
 * 
 */
package org.snowjak.rl1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.config.Config;
import org.snowjak.rl1.drawing.ascii.AsciiScreen;
import org.snowjak.rl1.map.Map;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
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
	 * The {@link Config} we're running with.
	 */
	public final Config config;
	
	/**
	 * The central {@link ExecutorService}.
	 */
	public final ListeningExecutorService executor;
	
	public App(Config config) {
		
		super();
		
		Context.initialize(this);
		
		this.config = config;
		this.events = new EventSystem();
		
		//@formatter:off
		final WorldConfiguration worldConfig =
				new WorldConfigurationBuilder()
					.with(new ExtendedComponentMapperPlugin())
					.with(events)
				.build();
		//@formatter:on
		
		this.odb = new World(worldConfig);
		
		if (config.getParallelism() <= 0) {
			LOG.info("Not using parallelism.");
			executor = MoreExecutors.newDirectExecutorService();
		} else {
			LOG.info("Using parallelism level {}.", config.getParallelism());
			executor = MoreExecutors.listeningDecorator(MoreExecutors.getExitingExecutorService(
					(ThreadPoolExecutor) Executors.newFixedThreadPool(config.getParallelism())));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {
		
		final Map map = new Map(config.getSeed(), config.getMapLargestFeature(), config.getMapFeaturePersistence(),
				config.getMapLowestAltitude(), config.getMapHighestAltitude());
		
		final AsciiScreen ascii = new AsciiScreen(config.getScreenWidth(), config.getScreenHeight(),
				config.getFont());
		
		for (int x = 0; x < config.getScreenWidth(); x++)
			for (int y = 0; y < config.getScreenHeight(); y++) {
				
				final float height = (float) map.getHeightFrac(x, y);
				final Color color = new Color(height, height, height, 1);
				
				ascii.foreground(color);
				ascii.put((char) 219, x, y);
				ascii.foreground();
				
			}
		
		setScreen(ascii);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Game#dispose()
	 */
	@Override
	public void dispose() {
		
		super.dispose();
		this.config.dispose();
	}
	
}
