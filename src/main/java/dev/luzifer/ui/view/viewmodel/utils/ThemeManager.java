package dev.luzifer.ui.view.viewmodel.utils;

import dev.luzifer.Main;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ThemeManager {

  public static ObservableList<String> getThemes() {
    return FXCollections.observableList(getThemeNames());
  }

  public static void reloadThemes(ObjectProperty<ObservableList<String>> themesProperty) {
    themesProperty.set(FXCollections.observableList(getThemeNames()));
  }

  public static String getLastTheme() {
    return Main.getLastTheme();
  }

  public static void saveTheme(String theme) {
    Main.setLastTheme(theme);
  }

  private static List<String> getThemeNames() {
    List<Path> themes = Main.getThemes();
    return themes.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
  }
}
