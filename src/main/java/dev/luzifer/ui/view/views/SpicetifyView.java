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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class SpicetifyView extends View<SpicetifyViewModel> {

  private static final String ICON_PATH = "/icon.png";

  @FXML private Circle iconShape;
  @FXML private Circle iconShape1;
  @FXML private ChoiceBox<String> themeBox;
  @FXML private CheckBox updateCheckBox;
  @FXML private CheckBox marketplaceCheckBox;
  @FXML private ProgressBar applyProgressBar;
  @FXML private ProgressBar installProgressBar;
  @FXML private VBox notInstalledVBox;
  @FXML private VBox installedVBox;
  @FXML private Label notInstalledLabel;
  @FXML private ImageView loadingSpinnerImageView;
  @FXML private ImageView installLoadingSpinnerImageView;

  public SpicetifyView(SpicetifyViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);
    getViewModel().checkSpicetifyInstalled();
    getViewModel().setupFileWatcher(_ -> Platform.runLater(() -> getViewModel().reloadThemes()));
    bindProperties();
    addListeners();
    setIcon();
    setLoadingSpinner();
    setupThemeBox();
    setNotInstalledLabelText();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }

  @FXML
  void onInstall(ActionEvent event) {
    getViewModel().install();
  }

  private void setNotInstalledLabelText() {
    notInstalledLabel.setText("You seem to not have Spicetify installed"); // TODO: later from messages.properties
  }

  private void bindProperties() {
    themeBox.valueProperty().bindBidirectional(getViewModel().currentThemeProperty());
    themeBox.itemsProperty().bind(getViewModel().themesProperty());
    updateCheckBox.selectedProperty().bindBidirectional(getViewModel().updateBeforeApplyProperty());
    notInstalledVBox.visibleProperty().bind(getViewModel().notInstalledProperty());
    installedVBox.visibleProperty().bind(getViewModel().notInstalledProperty().not());
    marketplaceCheckBox.selectedProperty().bindBidirectional(getViewModel().marketplaceProperty());
    loadingSpinnerImageView.visibleProperty().bind(getViewModel().progressPropertty().greaterThan(0));
    installLoadingSpinnerImageView.visibleProperty().bind(getViewModel().progressPropertty().greaterThan(0));
  }

  private void addListeners() {
    getViewModel()
        .progressPropertty()
        .addListener(
            (_, _, progress) ->
                Platform.runLater(() -> setProgress(progress)));
    themeBox
        .valueProperty()
        .addListener(
            (_, _, _) -> getViewModel().saveTheme());
  }

  private void setProgress(Number progress) {
    applyProgressBar.setProgress(progress.doubleValue());
    installProgressBar.setProgress(progress.doubleValue());
    if (progress.doubleValue() >= 1.0) {
      getViewModel().resetProgress();
    }
  }

  private void setIcon() {
    ImagePattern icon = createIcon();
    iconShape.setFill(icon);
    iconShape1.setFill(icon);
  }

  private void setLoadingSpinner() {
    Image loadingSpinner = createLoadingSpinner();
    loadingSpinnerImageView.setImage(loadingSpinner);
    installLoadingSpinnerImageView.setImage(loadingSpinner);
  }

  private void setupThemeBox() {
    String lastTheme = getViewModel().getLastTheme();
    if (lastTheme != null) {
      themeBox.setValue(lastTheme);
      return;
    }

    setThemeToFirst();
  }

  private void setThemeToFirst() {
    if (!themeBox.getItems().isEmpty()) {
      themeBox.setValue(themeBox.getItems().getFirst());
    }
  }

  private Image createLoadingSpinner() {
    return new Image(Main.class.getResourceAsStream("/loading.gif"));
  }

  private ImagePattern createIcon() {
    return new ImagePattern(new Image(Main.class.getResourceAsStream(ICON_PATH)));
  }
}
