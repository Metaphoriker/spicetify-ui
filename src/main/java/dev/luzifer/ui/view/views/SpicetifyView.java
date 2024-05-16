package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SpicetifyView extends View<SpicetifyViewModel> {

  private static final String ICON_PATH = "/icon.png";
  private static final String LOADING_SPINNER_PATH = "/loading.gif";

  @FXML private StackPane rootPane;
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
  @FXML private Button applyButton;
  @FXML private Button installButton;

  private double xOffset = 0;
  private double yOffset = 0;

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
    addTooltipToUpdateCheckBox();
    setIcon();
    setLoadingSpinner();
    setupThemeBox();
    setNotInstalledLabelText();
    makeSoftwareDraggable();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }

  @FXML
  void onInstall(ActionEvent event) {
    getViewModel().install();
  }

  private void makeSoftwareDraggable() {
    rootPane.setOnMousePressed(event -> {
      Stage stage = (Stage) rootPane.getScene().getWindow();
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    });

    rootPane.setOnMouseDragged(event -> {
      Stage stage = (Stage) rootPane.getScene().getWindow();
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    });
  }

  private void addTooltipToUpdateCheckBox() {
    updateCheckBox.setTooltip(new Tooltip("Update Spicetify before applying the theme (Recommended)")); // TODO: later from messages.properties
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
    loadingSpinnerImageView.visibleProperty().bind(getViewModel().progressProperty().greaterThan(0));
    installLoadingSpinnerImageView.visibleProperty().bind(getViewModel().progressProperty().greaterThan(0));
    applyButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
    installButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
  }

  private void addListeners() {
    getViewModel()
        .progressProperty()
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
    getResourceAsSaveStream(LOADING_SPINNER_PATH)
            .ifPresent(this::setLoadingSpinnerImage);
    return loadingSpinnerImageView.getImage();
  }

  private ImagePattern createIcon() {
    getResourceAsSaveStream(ICON_PATH)
            .ifPresent(this::setIconImage);
    return (ImagePattern) iconShape.getFill();
  }

  private void setLoadingSpinnerImage(InputStream inputStream) {
    Image loadingSpinner = new Image(inputStream);
    loadingSpinnerImageView.setImage(loadingSpinner);
    installLoadingSpinnerImageView.setImage(loadingSpinner);
  }

  private void setIconImage(InputStream inputStream) {
    Image icon = new Image(inputStream);
    iconShape.setFill(new ImagePattern(icon));iconShape1.setFill(new ImagePattern(icon));
  }

  private Optional<InputStream> getResourceAsSaveStream(String path) {
    InputStream inputStream =Main.class.getResourceAsStream(path);
    return Optional.ofNullable(inputStream);
  }
}
