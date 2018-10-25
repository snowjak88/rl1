/**
 * 
 */
package org.snowjak.rl1.desktop;

import java.io.File;

import org.snowjak.rl1.App;
import org.snowjak.rl1.config.Config;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true, subcommands = { ExportMapCommand.class })
public class MainAppCommand implements Runnable {
	
	@Mixin
	private MainAppCommand.Options options = new MainAppCommand.Options();
	
	@Mixin
	private StandardOptions standardOptions = new StandardOptions();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		final Config config = new Config();
		
		config.setFontFile(options.fontFile);
		config.setFontWidth(options.fontWidth);
		config.setFontHeight(options.fontHeight);
		
		config.setScreenWidth(options.screenWidth);
		config.setScreenHeight(options.screenHeight);
		
		config.setSeed(standardOptions.getSeed());
		config.setParallelism(standardOptions.getParallelism());
		config.setMapLargestFeature(standardOptions.getMapLargestFeature());
		config.setMapFeaturePersistence(standardOptions.getMapFeaturePersistence());
		config.setMapLowestAltitude(standardOptions.getMapLowestAltitude());
		config.setMapHighestAltitude(standardOptions.getMapHighestAltitude());
		
		new LwjglApplication(new App(config), new LwjglApplicationConfiguration());
	}
	
	public static class Options {
		
		@Option(names = "--font-file", paramLabel = "FONT-FILENAME", description = "Font-filename.")
		private File fontFile = new File("fonts/cp437_12x12.png");
		
		@Option(names = "--font-width", paramLabel = "FONT-WIDTH", description = "Font character-width. Must be appropriate to given font-filename.")
		private Integer fontWidth = 12;
		
		@Option(names = "--font-height", paramLabel = "FONT-HEIGHT", description = "Font character-height. Must be appropriate to given font-filename.")
		private Integer fontHeight = 12;
		
		@Option(names = "--screen-width", paramLabel = "WIDTH-IN-CHARS", description = "Screen width (#/chars).")
		private int screenWidth = 80;
		
		@Option(names = "--screen-height", paramLabel = "HEIGHT-IN-CHARS", description = "Screen height (#/chars).")
		private int screenHeight = 25;
		
	}
}