package dev.luzifer;

import dev.luzifer.ui.SpicetifyClient;
import javafx.application.Application;
import lombok.extern.java.Log;

import java.nio.file.Path;
import java.util.List;

@Log
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
}