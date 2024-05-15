package dev.luzifer;

import dev.luzifer.ui.SpicetifyClient;
import javafx.application.Application;
import lombok.extern.java.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class Main {

  private static final Path APPDATA_FOLDER = Path.of(System.getenv("APPDATA"));
  private static final Path SPICETIFY_PATH = APPDATA_FOLDER.resolve("spicetify");
  private static final Path SPICETIFY_THEMES_FOLDER = SPICETIFY_PATH.resolve("Themes");
  private static final Path SPICETIFY_UI_FOLDER = APPDATA_FOLDER.resolve("spicetify-ui");
  private static final Path SPICETIFY_UI_CONFIG_FILE = SPICETIFY_UI_FOLDER.resolve("config.ini");

  public static void main(String[] args) {
    log.info("Starting Spicetify client");

    setupFolder();

    Application.launch(SpicetifyClient.class, args);
  }

  public static Path getThemeFolder() {
    return SPICETIFY_THEMES_FOLDER;
  }

  public static boolean isSpicetifyInstalled() {
    return SPICETIFY_PATH.toFile().exists();
  }

  public static List<Path> getThemes() {
    File[] files = SPICETIFY_THEMES_FOLDER.toFile().listFiles();
    if (files == null) {
      return List.of();
    }

    return convertFromFileList(files);
  }

  public static void setLastTheme(String theme) {
    writeConfig("last_theme", theme);
  }

  private static String readConfig(String key) {
    try {
      String content = Files.readString(SPICETIFY_UI_CONFIG_FILE);
      String[] lines = content.split("\n");
      for (String line : lines) {
        String[] parts = line.split("=");
        if (parts[0].equals(key)) {
          return parts[1];
        }
      }
    } catch (Exception e) {
      log.severe("Could not read from config file");
    }
    return null;
  }

  private static void writeConfig(String key, String value) {
    try {
      String content = key + "=" + value;
      Files.writeString(SPICETIFY_UI_CONFIG_FILE, content);
    } catch (Exception e) {
      log.severe("Could not write to config file");
    }
  }

  private static void setupFolder() {
    SPICETIFY_UI_FOLDER.toFile().mkdirs();
    ensureConfigFileExists();
  }

  private static void ensureConfigFileExists() {
    if (!SPICETIFY_UI_CONFIG_FILE.toFile().exists()) {
      try {
        SPICETIFY_UI_CONFIG_FILE.toFile().createNewFile();
      } catch (Exception e) {
        log.severe("Could not create config file");
      }
    }
  }

  private static List<Path> convertFromFileList(File[] files) {
    return Arrays.stream(files)
        .filter(File::isDirectory)
        .map(File::toPath)
        .collect(Collectors.toList());
  }
}
