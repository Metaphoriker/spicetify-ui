package dev.luzifer.ui.view.views;

import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class SpicetifyView extends DraggableView<SpicetifyViewModel> {

  private static final String MINIMIZE_ICON_PATH = "/minimize.png";
  private static final String ICON_PATH = "/spicetify-logo.png";
  private static final String CLOSE_ICON_PATH = "/close.png";
  private static final String THREE_DOTS_ICON_PATH = "/three-dots.png";

  @FXML private Circle iconShape;
  @FXML private ChoiceBox<String> themeBox;
  @FXML private CheckBox updateCheckBox;
  @FXML private ProgressBar applyProgressBar;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private Button applyButton;
  @FXML private Button threeDotsButton;
  @FXML private Button closeButton;
  @FXML private Button minimizeButton;

  public SpicetifyView(SpicetifyViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);

    getViewModel().setupThemesFolderWatcher(_ -> Platform.runLater(() -> getViewModel().reloadThemes()));

    bindProperties();
    addListeners();
    addTooltipToUpdateCheckBox();
    setIcon();
    setupThemeBox();
    setupIconButtons();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }

  @FXML
  void onClose(ActionEvent event) {
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }

  @FXML
  void onMinimize(ActionEvent event) {
    Stage stage = (Stage) minimizeButton.getScene().getWindow();
    stage.setIconified(true);
  }

  private void setupIconButtons() {
    threeDotsButton.setGraphic(downTrimmedImageView(new ImageView(new Image(THREE_DOTS_ICON_PATH))));
    closeButton.setGraphic(downTrimmedImageView(new ImageView(new Image(CLOSE_ICON_PATH))));
    minimizeButton.setGraphic(downTrimmedImageView(new ImageView(new Image(MINIMIZE_ICON_PATH))));
    setupIconButtonsStyleClasses();
  }

  private void setupIconButtonsStyleClasses() {
    threeDotsButton.getStyleClass().add("icon-button");
    closeButton.getStyleClass().add("icon-button");
    minimizeButton.getStyleClass().add("icon-button");
    closeButton.getStyleClass().add("close-button");
    progressIndicator.getStyleClass().add("loading-spinner");
  }

  private void addTooltipToUpdateCheckBox() {
    updateCheckBox.setTooltip(new Tooltip("Update Spicetify before applying the theme (Recommended)")); // TODO: later from messages.properties
  }

  private void bindProperties() {
    themeBox.valueProperty().bindBidirectional(getViewModel().currentThemeProperty());
    themeBox.itemsProperty().bind(getViewModel().themesProperty());
    updateCheckBox.selectedProperty().bindBidirectional(getViewModel().updateBeforeApplyProperty());
    applyButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
    progressIndicator.visibleProperty().bind(getViewModel().progressProperty().greaterThan(0));
  }

  private void addListeners() {
    getViewModel().progressProperty().addListener((_, _, progress) -> Platform.runLater(() -> setProgress(progress)));
    themeBox.valueProperty().addListener((_, _, _) -> getViewModel().saveTheme());
  }

  private void setProgress(Number progress) {
    applyProgressBar.setProgress(progress.doubleValue());
  }

  private void setIcon() {
    ImagePattern icon = createIcon();
    iconShape.setFill(icon);
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

  private ImagePattern createIcon() {
    getResourceAsSaveStream(ICON_PATH)
            .ifPresent(this::setIconImage);
    return (ImagePattern) iconShape.getFill();
  }

  private void setIconImage(InputStream inputStream) {
    Image icon = new Image(inputStream);
    iconShape.setFill(new ImagePattern(icon));
  }
}
