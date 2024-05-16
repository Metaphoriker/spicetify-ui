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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private static final String CLOSE_ICON_PATH = "/close.png";
  private static final String THREE_DOTS_ICON_PATH = "/three-dots.png";

  @FXML private VBox rootPane;
  @FXML private Circle iconShape;
  @FXML private ChoiceBox<String> themeBox;
  @FXML private CheckBox updateCheckBox;
  @FXML private ProgressBar applyProgressBar;
  @FXML private ImageView loadingSpinnerImageView;
  @FXML private Button applyButton;
  @FXML private Button threeDotsButton;
  @FXML private Button closeButton;

  private double xOffset = 0;
  private double yOffset = 0;

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
    setLoadingSpinner();
    setupThemeBox();
    makeSoftwareDraggable();
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

  private void setupIconButtons() {
    threeDotsButton.setGraphic(downTrimmedImageView(new ImageView(new Image(THREE_DOTS_ICON_PATH))));
    closeButton.setGraphic(downTrimmedImageView(new ImageView(new Image(CLOSE_ICON_PATH))));
    setupIconButtonsStyleClasses();
  }

  private ImageView downTrimmedImageView(ImageView imageView) {
    imageView.setFitHeight(20);
    imageView.setFitWidth(20);
    return imageView;
  }

  private void setupIconButtonsStyleClasses() {
    threeDotsButton.getStyleClass().add("icon-button");
    closeButton.getStyleClass().add("icon-button");
    closeButton.getStyleClass().add("close-button");
  }

  private void addTooltipToUpdateCheckBox() {
    updateCheckBox.setTooltip(new Tooltip("Update Spicetify before applying the theme (Recommended)")); // TODO: later from messages.properties
  }

  private void bindProperties() {
    themeBox.valueProperty().bindBidirectional(getViewModel().currentThemeProperty());
    themeBox.itemsProperty().bind(getViewModel().themesProperty());
    updateCheckBox.selectedProperty().bindBidirectional(getViewModel().updateBeforeApplyProperty());
    loadingSpinnerImageView.visibleProperty().bind(getViewModel().progressProperty().greaterThan(0));
    applyButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
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

  private void setLoadingSpinner() {
    Image loadingSpinner = createLoadingSpinner();
    loadingSpinnerImageView.setImage(loadingSpinner);
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
  }

  private void setIconImage(InputStream inputStream) {
    Image icon = new Image(inputStream);
    iconShape.setFill(new ImagePattern(icon));
  }

  private Optional<InputStream> getResourceAsSaveStream(String path) {
    InputStream inputStream =Main.class.getResourceAsStream(path);
    return Optional.ofNullable(inputStream);
  }
}
