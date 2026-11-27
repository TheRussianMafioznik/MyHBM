package com.hbm.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import com.hbm.interfaces.Untested;
// import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;

public class HTTPHandler {

	public static String capsule = "ERROR         ";
	public static boolean newVersion = false;
	public static boolean optifine = false;
	public static String versionNumber = "";
	public static String changes = "";

	public static void loadStats() {
		try {
			// loadVersion();
			loadOF();
			loadSoyuz();
		} catch (IOException e) {
			MainRegistry.logger.warn("Version checker failed!");
		}
	}

	@Untested
	private static void loadOF() {
		// Drillgon200: Yeah this is retarded but apparently optifine isn't added to the list of forge mods
		File mods = new File(MainRegistry.proxy.getDataDir(), "mods");
		Path dir = mods.toPath();
		if (!Files.isDirectory(dir)) {
			return;
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, Files::isDirectory)) {
			for (Path path : stream) {
				if (path.getFileName().toString().toLowerCase(Locale.ROOT).contains("optifine")) {
					optifine = true;
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (optifine) {
			MainRegistry.logger.warn("Optifine present. This may cause compatibility with NTM's shaders!\n If experiencing issues, turn the enableShaders2 option in the config to false in order to disable them.");
		}
	}

	private static BufferedReader openUrlReader(String url) throws IOException {
		URLConnection connection = URI.create(url).toURL().openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
	}

	/* Проверка на ласт версию, раскомментрровать вызов выше и импорт
	private static void loadVersion() throws IOException {
		MainRegistry.logger.info("Searching for new versions...");

		try (BufferedReader in = openUrlReader("https://github.com/TheRussianMafioznik/MyHBM/blob/main/src/main/java/com/hbm/lib/RefStrings.java")) {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("String VERSION")) {
					int begin = line.indexOf('"');
					int end = line.lastIndexOf('"');
					if (begin != -1 && end > begin) {
						String sub = line.substring(begin + 1, end);
						newVersion = !RefStrings.VERSION.equals(sub);
						versionNumber = sub;
						MainRegistry.logger.info("Found version " + sub);
					}
				}
				if (line.contains("String CHANGELOG")) {
					int begin = line.indexOf('"');
					int end = line.lastIndexOf('"');
					if (begin != -1 && end > begin) {
						String sub = line.substring(begin + 1, end);
						changes = sub.replace("Â", "");
						MainRegistry.logger.info("Found changelog " + sub);
					}
					break;
				}
			}
		}

		MainRegistry.logger.info("Version checker ended.");
	}

	 */
	private static void loadSoyuz() throws IOException {
		try (BufferedReader in = openUrlReader("https://raw.githubusercontent.com/TheRussianMafioznik/MyHBM/refs/heads/main/src/main/java/com/hbm/lib/RefStrings.java")) {
			String line = in.readLine();
			if (line != null) {
				capsule = line;
			}
		}
	}
}
