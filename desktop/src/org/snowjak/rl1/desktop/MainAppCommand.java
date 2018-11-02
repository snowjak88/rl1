/**
 * 
 */
package org.snowjak.rl1.desktop;

import java.io.File;

import org.snowjak.rl1.App;
import org.snowjak.rl1.AppConfig;
import org.snowjak.rl1.map.MapConfig;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true, subcommands = { ExportMapCommand.class }, showDefaultValues = true)
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
		
		final AppConfig appConfig = new AppConfig();
		final MapConfig mapConfig = new MapConfig();
		
		appConfig.setFontFile(options.fontFile);
		appConfig.setFontWidth(options.fontWidth);
		appConfig.setFontHeight(options.fontHeight);
		appConfig.setWindowWidth(options.windowWidth);
		appConfig.setWindowHeight(options.windowHeight);
		appConfig.setScreenWidth(options.screenWidth);
		appConfig.setScreenHeight(options.screenHeight);
		
		appConfig.setSeed(standardOptions.getSeed());
		appConfig.setParallelism(standardOptions.getParallelism());
		
		appConfig.setMapConfig(mapConfig);
		mapConfig.setLargestFeature(standardOptions.getMapLargestFeature());
		mapConfig.setPersistence(standardOptions.getMapFeaturePersistence());
		mapConfig.setLowAltitude(standardOptions.getMapLowestAltitude());
		mapConfig.setHighAltitude(standardOptions.getMapHighestAltitude());
		mapConfig.setChunkSizeX(standardOptions.getMapChunkWidth());
		mapConfig.setChunkSizeY(standardOptions.getMapChunkHeight());
		
		final LwjglApplicationConfiguration lwjglConfig = new LwjglApplicationConfiguration();
		lwjglConfig.width = appConfig.getWindowWidth();
		lwjglConfig.height = appConfig.getWindowHeight();
		
		new LwjglApplication(new App(appConfig), lwjglConfig);
	}
	
	public static class Options {
		
		@Option(names = "--font-file", paramLabel = "FONT-FILENAME", description = "Font-filename.", defaultValue = "fonts/cp437_12x12.png")
		private File fontFile = new File("fonts/cp437_12x12.png");
		
		@Option(names = "--font-width", paramLabel = "FONT-WIDTH", description = "Font character-width. Must be appropriate to given font-filename.", defaultValue = "12")
		private Integer fontWidth = 12;
		
		@Option(names = "--font-height", paramLabel = "FONT-HEIGHT", description = "Font character-height. Must be appropriate to given font-filename.", defaultValue = "12")
		private Integer fontHeight = 12;
		
		@Option(names = "--window-width", paramLabel = "WIDTH-IN-PIXELS", description = "Window width (pixels).", defaultValue = "630")
		private int windowWidth = 630;
		
		@Option(names = "--window-height", paramLabel = "HEIGHT-IN-PIXELS", description = "Window height (pixels).", defaultValue = "420")
		private int windowHeight = 420;
		
		@Option(names = "--screen-width", paramLabel = "WIDTH-IN-CHARS", description = "Screen width (#/chars).", defaultValue = "80")
		private int screenWidth = 80;
		
		@Option(names = "--screen-height", paramLabel = "HEIGHT-IN-CHARS", description = "Screen height (#/chars).", defaultValue = "25")
		private int screenHeight = 25;
		
	}
}