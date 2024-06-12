package dev.luzifer.ui.view.views;

import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class SpicetifyView extends BaseSpicetifyView<SpicetifyViewModel> {

  private static final String ICON_PATH = "/spicetify-logo.png";

  @FXML private Circle iconShape;
  @FXML private ChoiceBox<String> themeBox;
  @FXML private CheckBox updateCheckBox;
  @FXML private Button applyButton;
  @FXML private HBox islandHBox;
  @FXML private Button uninstallButton;

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
    setIslandStyling();
    setupUninstallButton();
  }

  @FXML
  void onApply(ActionEvent event) {
    getViewModel().applyTheme();
  }
  
  @FXML
  void onUninstall(ActionEvent event) {
    getViewModel().uninstall();
  }

  private void addTooltipToUpdateCheckBox() {
    updateCheckBox.setTooltip(new Tooltip("Update Spicetify before applying the theme (Recommended)")); // TODO: later from messages.properties
  }
  
  private void setIslandStyling() {
    islandHBox.getStyleClass().add("island-hbox");
  }
  
  private void setupUninstallButton() {
    uninstallButton.getStyleClass().add("icon-button");
    uninstallButton.setGraphic(downTrimmedImageView(new ImageView(new Image("/uninstall.png"))));
    uninstallButton.setTooltip(new Tooltip("Uninstall Spicetify - you can reinstall it with this UI again")); // TODO: later from messages.properties
  }

  private void bindProperties() {
    themeBox.valueProperty().bindBidirectional(getViewModel().currentThemeProperty());
    themeBox.itemsProperty().bind(getViewModel().themesProperty());
    updateCheckBox.selectedProperty().bindBidirectional(getViewModel().updateBeforeApplyProperty());
    applyButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
  }

  private void addListeners() {
    themeBox.valueProperty().addListener((_, _, _) -> getViewModel().saveTheme());
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
