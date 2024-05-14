package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.Main;
import dev.luzifer.ui.view.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Log4j2
public class SpicetifyViewModel implements ViewModel {

  private static final String SPICETIFY_UPDATE_COMMAND = "spicetify update";
  private static final String SPICETIFY_APPLY_COMMAND = "spicetify apply";
  private static final String SPICETIFY_THEME_COMMAND = "spicetify config current_theme ";

  private final StringProperty currentThemeProperty = new SimpleStringProperty();
  private final DoubleProperty applyProgressProperty = new SimpleDoubleProperty(0);
  private final IntegerProperty applyProgressMaxProperty = new SimpleIntegerProperty(0);
  private final BooleanProperty updateBeforeApplyProperty = new SimpleBooleanProperty(true);

  private int progressCount = 0;

  public ObservableList<String> getThemes() {
    List<Path> themes = Main.getThemes();
    return FXCollections.observableList(
        themes.stream().map(Path::getFileName).map(Path::toString).toList());
  }

  public void applyTheme() {
    determineMaxProgress();
    if (updateBeforeApplyProperty.get()) {
      updateSpicetify();
    }
    setCurrentTheme();
    applySpicetify();
    resetProgress();
  }

  public BooleanProperty updateBeforeApplyProperty() {
    return updateBeforeApplyProperty;
  }

  public DoubleProperty applyProgressProperty() {
    return applyProgressProperty;
  }

  public StringProperty currentThemeProperty() {
    return currentThemeProperty;
  }

  private void setCurrentTheme() {
    String command = SPICETIFY_THEME_COMMAND + currentThemeProperty.get();
    executeCommand(command);
  }

  private void updateSpicetify() {
    executeCommand(SPICETIFY_UPDATE_COMMAND);
  }

  private void applySpicetify() {
    executeCommand(SPICETIFY_APPLY_COMMAND);
  }

  private void increaseProgress() {
    var maxValue = applyProgressMaxProperty.get();
    applyProgressProperty.set((double) ++progressCount / maxValue);
  }

  private void resetProgress() {
    applyProgressProperty.set(0.0);
    progressCount = 0;
  }

  private void determineMaxProgress() {
    if (updateBeforeApplyProperty.get()) {
      applyProgressMaxProperty.set(3);
    } else {
      applyProgressMaxProperty.set(2);
    }
  }

  private void executeCommand(String command) {
    try {
      Process process = createProcess(command);
      startNewThread(process);
    } catch (Exception e) {
      log.error("Error while executing command: {}", command, e);
    }
  }

  private Process createProcess(String command) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
    return processBuilder.start();
  }

  private void startNewThread(Process process) {
    new Thread(
            () -> {
              try {
                process.waitFor();
                increaseProgress();
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
            })
        .start();
  }
}
