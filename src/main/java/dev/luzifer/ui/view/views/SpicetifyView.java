package dev.luzifer.ui.view.views;

import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SpicetifyView extends BaseSpicetifyView<SpicetifyViewModel> {

  private static final String ICON_PATH = "/spicetify-logo.png";

  @FXML private Button applyButton;
  @FXML private FlowPane themeFlowPane;
  @FXML private ImageView themePreviewImageView;
  @FXML private Label selectedThemeLabel;
  @FXML private TextField searchBarTextField;
  @FXML private Button openThemeFolderButton;
  @FXML private ImageView logoImageView;

  public SpicetifyView(SpicetifyViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);

    getViewModel().setupThemesFolderWatcher(_ -> Platform.runLater(() -> getViewModel().reloadThemes()));

    bindProperties();
    addListeners();
    setIcon();
    setupThemeFolderButton();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }

  @FXML
  void onOpenThemeFolder(ActionEvent event) {
    getViewModel().openThemeFolder();
  }

  @FXML
  void onOpenDiscoverTab(Event event) {

  }

  @FXML
  void onOpenInstalledTab(Event event) {

  }

  private void bindProperties() {
    applyButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
  }

  private void addListeners() {
  }

  private void setupThemeFolderButton() {
    openThemeFolderButton.getStyleClass().add("icon-button");
  }

  private void setIcon() {
    logoImageView.setImage(createIcon());
  }

  private Image createIcon() {
    return new Image(getClass().getResourceAsStream(ICON_PATH));
  }
}
