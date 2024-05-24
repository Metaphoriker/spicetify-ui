package dev.luzifer.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.java.Log;

@Log
public class SpicetifyService {

  private static final Path APPDATA_FOLDER = Path.of(System.getenv("APPDATA"));
  private static final Path SPICETIFY_PATH = APPDATA_FOLDER.resolve("spicetify");
  private static final Path SPICETIFY_THEMES_FOLDER = SPICETIFY_PATH.resolve("Themes");
  private static final Path SPICETIFY_UI_FOLDER = APPDATA_FOLDER.resolve("spicetify-ui");
  private static final Path SPICETIFY_UI_CONFIG_FILE = SPICETIFY_UI_FOLDER.resolve("config.ini");

  public boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  public Path getThemeFolder() {
    return SPICETIFY_THEMES_FOLDER;
  }

  public Path getFirstFoundImageFile(String theme) {
    Path themePath = SPICETIFY_THEMES_FOLDER.resolve(theme);
    try (Stream<Path> paths = Files.walk(themePath)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".png") || path.toString().endsWith(".jpg"))
          .findFirst()
          .orElse(null);
    } catch (IOException e) {
      log.warning("Could not find any images in the theme folder of " + theme + " theme");
      return null;
    }
  }

  public boolean isSpicetifyInstalled() {
    return SPICETIFY_PATH.toFile().exists();
  }

  public List<Path> getThemes() {
    File[] files = SPICETIFY_THEMES_FOLDER.toFile().listFiles();
    if (files == null) {
      return List.of();
    }

    return convertFromFileList(files);
  }

  public void setLastTheme(String theme) {
    writeConfig("last_theme", theme);
  }

  public String getLastTheme() {
    return readConfig("last_theme");
  }

  private String readConfig(String key) {
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

  private void writeConfig(String key, String value) {
    try {
      String content = key + "=" + value;
      Files.writeString(SPICETIFY_UI_CONFIG_FILE, content);
    } catch (Exception e) {
      log.severe("Could not write to config file");
    }
  }

  public void initialize() {
    SPICETIFY_UI_FOLDER.toFile().mkdirs();
    ensureConfigFileExists();
  }

  private void ensureConfigFileExists() {
    if (!SPICETIFY_UI_CONFIG_FILE.toFile().exists()) {
      try {
        SPICETIFY_UI_CONFIG_FILE.toFile().createNewFile();
      } catch (Exception e) {
        log.severe("Could not create config file");
      }
    }
  }

  private List<Path> convertFromFileList(File[] files) {
    return Arrays.stream(files)
        .filter(File::isDirectory)
        .map(File::toPath)
        .collect(Collectors.toList());
  }
}
