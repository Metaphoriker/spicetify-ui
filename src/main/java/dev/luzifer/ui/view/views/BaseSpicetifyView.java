package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.BaseViewModel;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Since apparently all of our views are based on the same things, we can create a base class for
 * them.
 */
public abstract class BaseSpicetifyView<T extends BaseViewModel> extends View<T> {

  @FXML private VBox rootPane;
  @FXML private ProgressBar progressBar;
  @FXML private ProgressIndicator progressIndicator;

  private double xOffset = 0;
  private double yOffset = 0;

  protected BaseSpicetifyView(T viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    makeSoftwareDraggable();
    setupStyling();
    bindProperties();
  }

  protected ImageView downTrimmedImageView(ImageView imageView) {
    imageView.setFitHeight(20);
    imageView.setFitWidth(20);
    return imageView;
  }

  private void setupStyling() {
    progressIndicator.getStyleClass().add("loading-spinner");
  }

  private void bindProperties() {
    progressBar.progressProperty().bind(getViewModel().progressProperty());
    progressIndicator.visibleProperty().bind(getViewModel().progressProperty().greaterThan(0));
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

  protected Optional<InputStream> getResourceAsSaveStream(String path) {
    InputStream inputStream = Main.class.getResourceAsStream(path);
    return Optional.ofNullable(inputStream);
  }
}
