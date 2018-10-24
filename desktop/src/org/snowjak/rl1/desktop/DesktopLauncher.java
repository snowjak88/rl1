package org.snowjak.rl1.desktop;

import org.snowjak.rl1.App;
import org.snowjak.rl1.config.Options;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import picocli.CommandLine;

public class DesktopLauncher {
	
	public static void main(String[] arg) {
		
		final Options options = CommandLine.populateCommand(new Options(), arg);
		
		new LwjglApplication(new App(options), new LwjglApplicationConfiguration());
	}
}
