package org.kneelawk.learningopengllauncher;

import java.io.IOException;

import com.github.kneelawk.cpcontrol.AndEntryFilter;
import com.github.kneelawk.cpcontrol.CPControl4;
import com.github.kneelawk.cpcontrol.DirectoryEntryFilter;
import com.github.kneelawk.cpcontrol.NameContainsEntryFilter;

public class LearningOpenGLLauncher {

	public static void main(String[] args) {
		// use org.kneelawk.klines.KLines because scala main objects create
		// companion classes
		CPControl4 c = new CPControl4("org.kneelawk.learningopengl.LearningOpenGL");

		// Use extractingFromFile version because extractingFromClasspath
		// version searches entire classpath
		c.addExtractingFromFileLibrary(CPControl4.ME)
				// Don't forget to add the application jar
				.addLibrary("application", new DirectoryEntryFilter("app"), CPControl4.ALWAYS_DELETE)
				.addLibrary("scala-lang",
						new AndEntryFilter(new DirectoryEntryFilter("libs"), new NameContainsEntryFilter("scala")),
						CPControl4.ALWAYS_DELETE)
				.addLibrary("lwjgl",
						new AndEntryFilter(new DirectoryEntryFilter("libs"), new NameContainsEntryFilter("lwjgl")),
						CPControl4.ALWAYS_DELETE);

		try {
			c.launch(args);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
