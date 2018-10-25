package org.snowjak.rl1.desktop;

import picocli.CommandLine;
import picocli.CommandLine.RunLast;

public class DesktopLauncher {
	
	public static void main(String[] arg) {
		
		//@formatter:off
		new CommandLine(new MainAppCommand())
			.parseWithHandler(new RunLast(), arg);
		//@formatter:on
	}
}
