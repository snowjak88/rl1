/**
 * 
 */
package org.snowjak.rl1.desktop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.rl1.App;
import org.snowjak.rl1.AppConfig;
import org.snowjak.rl1.map.MapGenerator;
import org.snowjak.rl1.map.MapConfig;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "export-mapGenerator", description = "Exports a section of the game-mapGenerator as an image.")
public class ExportMapCommand implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExportMapCommand.class);
	
	@Mixin
	private StandardOptions standardOptions = new StandardOptions();
	
	@Option(names = "--export-output", paramLabel = "PNG-FILE", description = "Filename to export resulting PNG image to.", defaultValue = "export.png")
	private File outputFile;
	
	@Option(names = "--export-width", paramLabel = "WIDTH", description = "Exported mapGenerator will be so many cells wide.", defaultValue = "400")
	private int mapWidth;
	
	@Option(names = "--export-height", paramLabel = "HEIGHT", description = "Exported mapGenerator will be so many cells high.", defaultValue = "400")
	private int mapHeight;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		final AppConfig appConfig = new AppConfig();
		final MapConfig mapConfig = new MapConfig();
		
		appConfig.setSeed(standardOptions.getSeed());
		appConfig.setParallelism(standardOptions.getParallelism());
		appConfig.setMapConfig(mapConfig);
		
		mapConfig.setLargestFeature(standardOptions.getMapLargestFeature());
		mapConfig.setPersistence(standardOptions.getMapFeaturePersistence());
		mapConfig.setLowAltitude(standardOptions.getMapLowestAltitude());
		mapConfig.setHighAltitude(standardOptions.getMapHighestAltitude());
		
		new App(appConfig);
		
		final MapGenerator mapGenerator = new MapGenerator(appConfig);
		
		final BufferedImage img = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_ARGB);
		
		final int halfWidth = mapWidth / 2;
		final int halfHeight = mapHeight / 2;
		
		for (int x = -(halfWidth); x < +(halfWidth); x++)
			for (int y = -(halfHeight); y < +(halfHeight); y++) {
				
				final double height = mapGenerator.getHeightFrac(x, y);
				
				final int r = (int) (height * 255d) & 255, g = (int) (height * 255d) & 255,
						b = (int) (height * 255d) & 255;
				
				final int rgb = (255 << 24) | (r << 16) | (g << 8) | b;
				img.setRGB(x + halfWidth, y + halfHeight, rgb);
				
			}
		
		try (final OutputStream os = new FileOutputStream(outputFile)) {
			ImageIO.write(img, "PNG", os);
		} catch (IOException e) {
			LOG.error("Cannot save exported image to \"" + outputFile.getPath() + "\".", e);
		}
	}
	
}