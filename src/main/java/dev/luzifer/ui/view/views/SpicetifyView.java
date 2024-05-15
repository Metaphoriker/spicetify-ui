package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class SpicetifyView extends View<SpicetifyViewModel> {

  private static final String ICON_PATH = "/icon.png";

  @FXML private Circle iconShape;
  @FXML private ChoiceBox<String> themeBox;
  @FXML private CheckBox updateCheckBox;
  @FXML private ProgressBar applyProgressBar;
  @FXML private VBox notInstalledVBox;
  @FXML private Label notInstalledLabel;

  public SpicetifyView(SpicetifyViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);
    getViewModel().checkSpicetifyInstalled();
    changeStyleIfNotInstalled();
    bindProperties();
    addListeners();
    setIcon();
    setThemeBoxItems();
    setNotInstalledLabelText();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }

  @FXML
  void onInstall(ActionEvent event) {}

  private void changeStyleIfNotInstalled() {
    if (getViewModel().notInstalledProperty().get()) {
      notInstalledVBox.getStyleClass().add("not-installed");
    } else {
      notInstalledVBox.getStyleClass().remove("not-installed");
    }
  }

  private void setNotInstalledLabelText() {
    notInstalledLabel.setText("You seem to not have Spicetify installed. Wanna install it now?"); // TODO: later from messages.properties
  }

  private void bindProperties() {
    themeBox.valueProperty().bindBidirectional(getViewModel().currentThemeProperty());
    updateCheckBox.selectedProperty().bindBidirectional(getViewModel().updateBeforeApplyProperty());
    notInstalledVBox.visibleProperty().bind(getViewModel().notInstalledProperty());
  }

  private void addListeners() {
    getViewModel()
        .applyProgressProperty()
        .addListener(
            (_, _, progress) ->
                Platform.runLater(() -> setProgress(progress)));
  }

  private void setProgress(Number progress) {
    applyProgressBar.setProgress(progress.doubleValue());
    if (progress.doubleValue() == 1) applyProgressBar.setProgress(0);
  }

  private void setIcon() {
    iconShape.setFill(getIcon());
  }

  private void setThemeBoxItems() {
    themeBox.setItems(getViewModel().getThemes());
    if (!themeBox.getItems().isEmpty())
      themeBox.setValue(themeBox.getItems().getFirst());
  }

  private ImagePattern getIcon() {
    return new ImagePattern(new Image(Main.class.getResourceAsStream(ICON_PATH)));
  }
}
