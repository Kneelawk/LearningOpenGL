package org.kneelawk.learningopengllauncher;

import java.io.IOException;

import org.kneelawk.cpcontrol.CPControl3;
import org.kneelawk.cpcontrol.CPControl3.AndEntryFilter;
import org.kneelawk.cpcontrol.CPControl3.DirectoryEntryFilter;
import org.kneelawk.cpcontrol.CPControl3.NameContainsEntryFilter;

public class LearningOpenGLLauncher {

	public static void main(String[] args) {
		// use org.kneelawk.klines.KLines because scala main objects create
		// companion classes
		CPControl3 c = new CPControl3("org.kneelawk.learningopengl.LearningOpenGL");

		// Use extractingFromFile version because extractingFromClasspath
		// version searches entire classpath
		c.addExtractingFromFileLibrary(CPControl3.ME)
				// Don't forget to add the application jar
				.addLibrary("application", new DirectoryEntryFilter("app"), CPControl3.ALWAYS_DELETE)
				.addLibrary("scala-lang",
						new AndEntryFilter(new DirectoryEntryFilter("libs"), new NameContainsEntryFilter("scala")),
						CPControl3.ALWAYS_DELETE)
				.addLibrary("lwjgl",
						new AndEntryFilter(new DirectoryEntryFilter("libs"), new NameContainsEntryFilter("lwjgl")),
						CPControl3.ALWAYS_DELETE);

		try {
			c.launch(args);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
