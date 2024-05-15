package dev.luzifer;

import dev.luzifer.ui.SpicetifyClient;
import javafx.application.Application;
import lombok.extern.java.Log;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class Main {

  private static final Path APPDATA_FOLDER = Path.of(System.getenv("APPDATA"));
  private static final Path SPICETIFY_PATH = APPDATA_FOLDER.resolve("spicetify");
  private static final Path SPICETIFY_THEMES_FOLDER = SPICETIFY_PATH.resolve("Themes");

  public static void main(String[] args) {
    if (!SPICETIFY_THEMES_FOLDER.toFile().exists()) sendDialogAndClose();

    log.info("Starting Spicetify client");
    Application.launch(SpicetifyClient.class, args);
  }

  private static void sendDialogAndClose() {
    log.severe("Spicetify themes folder not found in " + SPICETIFY_THEMES_FOLDER);
    JOptionPane.showMessageDialog(
        null,
        "Spicetify themes folder not found in " + SPICETIFY_THEMES_FOLDER,
        "Error",
        JOptionPane.ERROR_MESSAGE);
    System.exit(1);
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  public static boolean doesSpicetifyExist() {
    return SPICETIFY_PATH.toFile().exists();
  }

  public static List<Path> getThemes() {
    File[] files = SPICETIFY_THEMES_FOLDER.toFile().listFiles();
    if (files == null) {
      return List.of();
    }

    return convertFromFileList(files);
  }

  private static List<Path> convertFromFileList(File[] files) {
    return Arrays.stream(files)
        .filter(File::isDirectory)
        .map(File::toPath)
        .collect(Collectors.toList());
  }
}
