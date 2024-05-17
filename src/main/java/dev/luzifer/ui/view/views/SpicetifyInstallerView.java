package dev.luzifer.ui.view.views;

import dev.luzifer.ui.view.viewmodel.SpicetifyInstallerViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class SpicetifyInstallerView extends BaseSpicetifyView<SpicetifyInstallerViewModel> {

  @FXML private Button installButton;
  @FXML private CheckBox marketplaceCheckBox;
  @FXML private Label notInstalledLabel;
  @FXML private Rectangle spicetifyLogoShape;

  public SpicetifyInstallerView(SpicetifyInstallerViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);

    bindProperties();
    setupHeaderText();
    setLogoImage();
    setInteractiveTexts();
  }

  private void bindProperties() {
    marketplaceCheckBox.selectedProperty().bindBidirectional(getViewModel().marketplaceProperty());
    installButton.disableProperty().bind(getViewModel().progressProperty().greaterThan(0));
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

  private void setLogoImage() {
    getResourceAsSaveStream("/spicetify-banner.png")
        .ifPresent(
            inputStream -> {
              Image image = new Image(inputStream);
              spicetifyLogoShape.setFill(new ImagePattern(image));
            });
  }
}
