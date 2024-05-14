package dev.luzifer;

import dev.luzifer.ui.SpicetifyClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class Main {

  private static final Path APPDATA_FOLDER = Path.of(System.getenv("APPDATA"));
  private static final Path SPICETIFY_THEMES_FOLDER =
      APPDATA_FOLDER.resolve("spicetify" + File.separator + "Themes");

  public static void main(String[] args) {
    if (!SPICETIFY_THEMES_FOLDER.toFile().exists()) sendDialogAndClose();

    log.info("Themes folder found in {}, starting application..", SPICETIFY_THEMES_FOLDER);
    Application.launch(SpicetifyClient.class, args);
  }

  private static void sendDialogAndClose() {
    log.error("Themes folder not found in {}", SPICETIFY_THEMES_FOLDER);
    Platform.runLater(
        () -> {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error Dialog");
          alert.setHeaderText(null);
          alert.setContentText("Themes folder not found in " + SPICETIFY_THEMES_FOLDER);
          alert.showAndWait();
        });
    System.exit(1);
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
