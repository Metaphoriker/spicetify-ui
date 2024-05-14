package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
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

  public SpicetifyView(SpicetifyViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);
    bindProperties();
    addListeners();
    setIcon();
    setThemeBoxItems();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }

  private void bindProperties() {
    themeBox.valueProperty().bindBidirectional(getViewModel().currentThemeProperty());
    updateCheckBox.selectedProperty().bindBidirectional(getViewModel().updateBeforeApplyProperty());
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
