package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.Main;
import dev.luzifer.model.FileSystemWatcher;
import dev.luzifer.ui.view.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Log
public class SpicetifyViewModel implements ViewModel {

  private static final String SPICETIFY_UPDATE_COMMAND = "spicetify update";
  private static final String SPICETIFY_APPLY_COMMAND = "spicetify apply";
  private static final String SPICETIFY_BACKUP_APPLY_COMMAND = "spicetify backup apply";
  private static final String SPICETIFY_THEME_COMMAND = "spicetify config current_theme ";
  private static final String SPICETIFY_INSTALL_WINDOWS_COMMAND =
      "iwr -useb https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.ps1 | iex";
  private static final String SPICETIFY_INSTALL_WINDOWS_MARKETPLACE_COMMAND =
      "iwr -useb https://raw.githubusercontent.com/spicetify/spicetify-marketplace/main/resources/install.ps1 | iex";
  private static final String SPICETIFY_INSTALL_OTHER_COMMAND =
      "curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.sh | sh";
  private static final String SPICETIFY_INSTALL_OTHER_MARKETPLACE_COMMAND =
      "curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-marketplace/main/resources/install.sh | sh";

  private final StringProperty currentThemeProperty = new SimpleStringProperty();
  private final DoubleProperty progressProperty = new SimpleDoubleProperty(0);
  private final IntegerProperty progressMaxProperty = new SimpleIntegerProperty(0);
  private final BooleanProperty updateBeforeApplyProperty = new SimpleBooleanProperty(true);
  private final BooleanProperty notInstalledProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty marketplaceProperty = new SimpleBooleanProperty(false);
  private final ObjectProperty<ObservableList<String>> themesProperty =
      new SimpleObjectProperty<>(getThemes());

  private final CommandExecutor commandExecutor = new CommandExecutor();

  private int progressCount = 0;

  private ObservableList<String> getThemes() {
    List<Path> themes = Main.getThemes();
    return FXCollections.observableList(
        themes.stream().map(Path::getFileName).map(Path::toString).toList());
  }

  public void reloadThemes() {
    themesProperty.set(FXCollections.observableList(getThemes()));
  }

  public void setupFileWatcher(Consumer<String> callback) {
    FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(callback);
    CompletableFuture.runAsync(fileSystemWatcher, Executors.newSingleThreadExecutor());
  }

  public String getLastTheme() {
    return Main.getLastTheme();
  }

  public void applyTheme() {
    determineMaxProgress();
    if (updateBeforeApplyProperty.get()) {
      updateSpicetify();
    }
    setCurrentTheme();
    applySpicetify();
  }

  public void saveTheme() {
    Main.setLastTheme(currentThemeProperty.get());
  }

  public void resetProgress() {
    progressCount = 0;
    progressProperty.set(0);
  }

  public void checkSpicetifyInstalled() {
    notInstalledProperty.set(!Main.isSpicetifyInstalled());
  }

  public BooleanProperty updateBeforeApplyProperty() {
    return updateBeforeApplyProperty;
  }

  public BooleanProperty notInstalledProperty() {
    return notInstalledProperty;
  }

  public DoubleProperty progressProperty() {
    return progressProperty;
  }

  public StringProperty currentThemeProperty() {
    return currentThemeProperty;
  }

  public BooleanProperty marketplaceProperty() {
    return marketplaceProperty;
  }

  public ObjectProperty<ObservableList<String>> themesProperty() {
    return themesProperty;
  }

  private void setCurrentTheme() {
    String command = SPICETIFY_THEME_COMMAND + currentThemeProperty.get();
    commandExecutor.executeCommand(command, this::increaseProgress);
  }

  private void updateSpicetify() {
    commandExecutor.executeCommand(SPICETIFY_UPDATE_COMMAND, this::increaseProgress);
  }

  private void applySpicetify() {
    commandExecutor.executeCommand(SPICETIFY_APPLY_COMMAND, this::increaseProgress);
  }

  public void install() {
    progressMaxProperty.set(3);
    increaseProgress();

    String command;
    if (Main.isWindows()) {
      command =
          marketplaceProperty.get()
              ? SPICETIFY_INSTALL_WINDOWS_MARKETPLACE_COMMAND
              : SPICETIFY_INSTALL_WINDOWS_COMMAND;
    } else {
      command =
          marketplaceProperty.get()
              ? SPICETIFY_INSTALL_OTHER_MARKETPLACE_COMMAND
              : SPICETIFY_INSTALL_OTHER_COMMAND;
    }

    commandExecutor.executeCommand(command, this::afterInstall);
  }

  private void afterInstall() {
    commandExecutor.executeCommand(SPICETIFY_BACKUP_APPLY_COMMAND, this::increaseProgress);
    checkSpicetifyInstalled();
  }

  private void increaseProgress() {
    var maxValue = progressMaxProperty.get();
    progressProperty.set((double) ++progressCount / maxValue);
  }

  private void determineMaxProgress() {
    progressMaxProperty.set(updateBeforeApplyProperty.get() ? 3 : 2);
  }
}
