package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.Main;
import dev.luzifer.model.FileSystemWatcher;
import dev.luzifer.model.SpicetifyCommands;
import dev.luzifer.ui.view.viewmodel.utils.ThemeManager;

import java.awt.*;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpicetifyViewModel extends BaseViewModel {

  private final StringProperty currentThemeProperty = new SimpleStringProperty();
  private final ObjectProperty<ObservableList<String>> themesProperty =
      new SimpleObjectProperty<>(ThemeManager.getThemes());

  public void reloadThemes() {
    ThemeManager.reloadThemes(themesProperty);
  }

  public void setupThemesFolderWatcher(Consumer<String> callback) {
    FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(callback);
    startNewDaemonThread(fileSystemWatcher);
  }

  private void startNewDaemonThread(FileSystemWatcher fileSystemWatcher) {
    Thread thread = new Thread(fileSystemWatcher);
    thread.setDaemon(true);
    thread.start();
  }

  public String getLastTheme() {
    return ThemeManager.getLastTheme();
  }

  public void openThemeFolder() {
    Desktop desktop = Desktop.getDesktop();
    try {
      desktop.open(Main.getThemeFolder().toFile());
    } catch (Exception e) {
      log.error("Error while opening the themes folder", e);
    }
  }

  public void applyTheme() {
    int totalSteps = 4;
    progressMaxProperty.set(totalSteps);
    updateSpicetify();
    setCurrentTheme();
    executeCommandAndUpdateProgress(SpicetifyCommands.APPLY);
  }

  public void uninstall() {
    progressMaxProperty.set(3);
    executeCommandAndUpdateProgress(SpicetifyCommands.SPICEITFY_RESTORE);

    if (Main.isWindows()) {
      executeCommandAndUpdateProgress(SpicetifyCommands.DELETE_APPDATA_SPICETIFY_WINDOWS);
      executeCommandAndUpdateProgress(SpicetifyCommands.DELETE_LOCALAPPDATA_SPICETIFY_WINDOWS);
    } else {
      executeCommandAndUpdateProgress(SpicetifyCommands.DELETE_APPDATA_SPICETIFY_OTHER);
      executeCommandAndUpdateProgress(SpicetifyCommands.DELETE_LOCALAPPDATA_SPICETIFY_OTHER);
    }
  }

  private void updateSpicetify() {
    executeCommandAndUpdateProgress(SpicetifyCommands.UPDATE);
    executeCommandAndUpdateProgress(SpicetifyCommands.BACKUP_APPLY);
  }

  public void saveTheme() {
    ThemeManager.saveTheme(currentThemeProperty.get());
  }

  public StringProperty currentThemeProperty() {
    return currentThemeProperty;
  }

  public ObjectProperty<ObservableList<String>> themesProperty() {
    return themesProperty;
  }

  private void setCurrentTheme() {
    String command = SpicetifyCommands.THEME + currentThemeProperty.get();
    executeCommandAndUpdateProgress(command);
  }

  private void executeCommandAndUpdateProgress(String command) {
    commandExecutor.executeCommand(command, this::increaseProgress);
  }
}
