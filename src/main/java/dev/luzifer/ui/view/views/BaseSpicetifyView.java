package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.BaseViewModel;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Since apparently all of our views are based on the same things, we can create a base class for
 * them.
 */
public abstract class BaseSpicetifyView<T extends BaseViewModel> extends View<T> {

  private static final String MINIMIZE_ICON_PATH = "/minimize.png";
  private static final String CLOSE_ICON_PATH = "/close.png";

  @FXML private VBox rootPane;
  @FXML private ProgressBar progressBar;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private Button closeButton;
  @FXML private Button minimizeButton;
  @FXML private HBox controlBar;

  private double xOffset = 0;
  private double yOffset = 0;

  protected BaseSpicetifyView(T viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    makeSoftwareDraggable();
    setupStyling();
    setButtonsGraphics();
    bindProperties();
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

  private void setButtonsGraphics() {
    closeButton.setGraphic(downTrimmedImageView(new ImageView(new Image(CLOSE_ICON_PATH))));
    minimizeButton.setGraphic(downTrimmedImageView(new ImageView(new Image(MINIMIZE_ICON_PATH))));
  }

  protected ImageView downTrimmedImageView(ImageView imageView) {
    imageView.setFitHeight(20);
    imageView.setFitWidth(20);
    return imageView;
  }

  private void setupStyling() {
    progressIndicator.getStyleClass().add("loading-spinner");
    closeButton.getStyleClass().add("icon-button");
    minimizeButton.getStyleClass().add("icon-button");
    closeButton.getStyleClass().add("close-button");
    controlBar.getStyleClass().add("control-bar");
  }

  private void bindProperties() {
    progressBar.progressProperty().bind(getViewModel().progressProperty());
    progressIndicator.visibleProperty().bind(getViewModel().progressProperty().greaterThan(0));
  }

  private void makeSoftwareDraggable() {
    controlBar.setOnMousePressed(
        event -> {
          Stage stage = (Stage) rootPane.getScene().getWindow();
          xOffset = stage.getX() - event.getScreenX();
          yOffset = stage.getY() - event.getScreenY();
        });

    controlBar.setOnMouseDragged(
        event -> {
          Stage stage = (Stage) rootPane.getScene().getWindow();
          stage.setX(event.getScreenX() + xOffset);
          stage.setY(event.getScreenY() + yOffset);
        });
  }

  protected Optional<InputStream> getResourceAsSaveStream(String path) {
    InputStream inputStream = Main.class.getResourceAsStream(path);
    return Optional.ofNullable(inputStream);
  }
}
