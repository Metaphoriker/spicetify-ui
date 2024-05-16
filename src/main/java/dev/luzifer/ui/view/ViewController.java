package dev.luzifer.ui.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ViewController {

  private static final String CSS_CLASS_PATH = "/dev/luzifer/ui/view/views/Spicetify-UI-Theme.css";
  private static final String ICON_PATH = "/spicetify-logo.png";

  private final Map<String, View<? extends ViewModel>> viewMap = new HashMap<>();

  public void showView(View<? extends ViewModel> view) {
    String viewName = getViewName(view);
    Optional.ofNullable(viewMap.get(viewName))
        .ifPresentOrElse(View::requestFocus, () -> createAndShowView(view, viewName));
  }

  public void closeAllViews() {
    new ArrayList<>(viewMap.values()).forEach(View::close);
  }

  private String getViewName(View<? extends ViewModel> view) {
    return view.getClass()
        .getSimpleName()
        .substring(0, view.getClass().getSimpleName().length() - 4);
  }

  private void createAndShowView(View<? extends ViewModel> view, String viewName) {
    viewMap.put(viewName, view);
    Parent root = loadView(view.getClass(), _ -> view);
    Scene scene = createScene(root);
    setupAndShowView(view, viewName, scene);
  }

  private Scene createScene(Parent root) {
    Scene scene = new Scene(root);
    URL cssUrl = getClass().getResource(CSS_CLASS_PATH);
    if (cssUrl != null) {
      String css = cssUrl.toExternalForm();
      scene.getStylesheets().add(css);
    }
    return scene;
  }

  private void setupAndShowView(View<? extends ViewModel> view, String title, Scene scene) {
    view.setScene(scene);
    view.setTitle(title);
    view.setOnHiding(
        _ -> {
          view.onClose();
          viewMap.remove(title);
        });
    view.setResizable(false);
    view.initStyle(StageStyle.UNDECORATED);
    view.getIcons().add(new Image(getClass().getResourceAsStream(ICON_PATH)));
    view.show();
  }

  private <T> Parent loadView(Class<T> clazz, Callback<Class<?>, Object> controllerFactory) {
    FXMLLoader fxmlLoader = new FXMLLoader();
    URL fxmlLocation = clazz.getResource(clazz.getSimpleName() + ".fxml");
    fxmlLoader.setLocation(fxmlLocation);
    fxmlLoader.setControllerFactory(controllerFactory);
    try {
      return fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(
          MessageFormat.format("FXML could not get loaded for class: {0}", clazz), e);
    }
  }
}
