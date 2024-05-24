package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.model.FileSystemWatcher;
import dev.luzifer.model.SpicetifyCommands;
import dev.luzifer.ui.view.viewmodel.utils.ThemeManager;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

@Log
public class SpicetifyViewModel extends BaseViewModel {

  private final StringProperty currentThemeProperty = new SimpleStringProperty();
  private final BooleanProperty updateBeforeApplyProperty = new SimpleBooleanProperty(true);
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

  public void applyTheme() {
    int totalSteps = calculateTotalSteps();
    progressMaxProperty.set(totalSteps);
    updateSpicetifyIfNecessary();
    setCurrentTheme();
    executeCommandAndUpdateProgress(SpicetifyCommands.APPLY);
  }

  private int calculateTotalSteps() {
    int totalSteps = 3;
    return updateBeforeApplyProperty.get() ? ++totalSteps : totalSteps;
  }

  private void updateSpicetifyIfNecessary() {
    if (updateBeforeApplyProperty.get()) {
      executeCommandAndUpdateProgress(SpicetifyCommands.UPDATE);
      executeCommandAndUpdateProgress(SpicetifyCommands.BACKUP_APPLY);
    }
  }

  public void saveTheme() {
    ThemeManager.saveTheme(currentThemeProperty.get());
  }

  public BooleanProperty updateBeforeApplyProperty() {
    return updateBeforeApplyProperty;
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
