package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.SpicetifyInstallerViewModel;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SpicetifyInstallerView extends View<SpicetifyInstallerViewModel> {

  @FXML private Button installButton;
  @FXML private CheckBox marketplaceCheckBox;
  @FXML private Label notInstalledLabel;
  @FXML private ProgressBar progressBar;
  @FXML private Rectangle spicetifyLogoShape;
  @FXML private VBox rootPane;
  @FXML private Button closeButton;

  private double xOffset = 0;
  private double yOffset = 0;

  public SpicetifyInstallerView(SpicetifyInstallerViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);

    bindProperties();
    setupHeaderText();
    setupCloseButton();
    setLogoImage();
    setInteractiveTexts();
    makeSoftwareDraggable();
  }

  private void bindProperties() {
    marketplaceCheckBox.selectedProperty().bindBidirectional(getViewModel().marketplaceProperty());
    installButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
    progressBar.progressProperty().bind(getViewModel().progressProperty());
  }

  private ImageView downTrimmedImageView(ImageView imageView) {
    imageView.setFitHeight(20);
    imageView.setFitWidth(20);
    return imageView;
  }

  private void setupCloseButton() {
    closeButton.setGraphic(
        downTrimmedImageView(
            new ImageView(new Image(getClass().getResourceAsStream("/close.png")))));
    closeButton.getStyleClass().add("icon-button");
    closeButton.getStyleClass().add("close-button");
  }

  private void setupHeaderText() {
    notInstalledLabel.getStyleClass().add("header-text");
    notInstalledLabel.setText(
        "Spicetify is not installed. Do you want to install it?"); // TODO: i18n
  }

  private void setInteractiveTexts() {
    installButton.setText("Install"); // TODO: i18n
    marketplaceCheckBox.setText("Install with marketplace support"); // TODO: i18n
  }

  @FXML
  void onInstall(ActionEvent event) {
    getViewModel().install();
  }

  @FXML
  void onClose(ActionEvent event) {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  private void setLogoImage() {
    getResourceAsSaveStream("/spicetify-logo.png")
        .ifPresent(
            inputStream -> {
              Image image = new Image(inputStream);
              spicetifyLogoShape.setFill(new ImagePattern(image));
            });
  }

  private void makeSoftwareDraggable() {
    rootPane.setOnMousePressed(
        event -> {
          Stage stage = (Stage) rootPane.getScene().getWindow();
          xOffset = stage.getX() - event.getScreenX();
          yOffset = stage.getY() - event.getScreenY();
        });

    rootPane.setOnMouseDragged(
        event -> {
          Stage stage = (Stage) rootPane.getScene().getWindow();
          stage.setX(event.getScreenX() + xOffset);
          stage.setY(event.getScreenY() + yOffset);
        });
  }

  private Optional<InputStream> getResourceAsSaveStream(String path) {
    InputStream inputStream = Main.class.getResourceAsStream(path);
    return Optional.ofNullable(inputStream);
  }
}
