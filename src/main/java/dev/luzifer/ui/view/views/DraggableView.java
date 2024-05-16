package dev.luzifer.ui.view.views;

import dev.luzifer.Main;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.ViewModel;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DraggableView<T extends ViewModel> extends View<T> {

  @FXML private VBox rootPane;

  private double xOffset = 0;
  private double yOffset = 0;

  protected DraggableView(T viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    makeSoftwareDraggable();
  }

  protected ImageView downTrimmedImageView(ImageView imageView) {
    imageView.setFitHeight(20);
    imageView.setFitWidth(20);
    return imageView;
  }

  protected void makeSoftwareDraggable() {
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
