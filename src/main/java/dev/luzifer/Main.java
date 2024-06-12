package dev.luzifer;

import dev.luzifer.model.SpicetifyService;
import dev.luzifer.ui.SpicetifyClient;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public class Main {

  private static final SpicetifyService SPICETIFY_SERVICE = new SpicetifyService();

  public static void main(String[] args) {
    log.info("Starting Spicetify client");

    SPICETIFY_SERVICE.initialize();

    Application.launch(SpicetifyClient.class, args);
  }

  public static boolean isWindows() {
    return SPICETIFY_SERVICE.isWindows();
  }

  public static Path getThemeFolder() {
    return SPICETIFY_SERVICE.getThemeFolder();
  }

  public static boolean isSpicetifyInstalled() {
    return SPICETIFY_SERVICE.isSpicetifyInstalled();
  }

  public static List<Path> getThemes() {
    return SPICETIFY_SERVICE.getThemes();
  }

  public static void setLastTheme(String theme) {
    SPICETIFY_SERVICE.setLastTheme(theme);
  }

  public static String getLastTheme() {
    return SPICETIFY_SERVICE.getLastTheme();
  }
}
