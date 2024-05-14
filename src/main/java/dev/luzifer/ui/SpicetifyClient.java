package dev.luzifer.ui;

import dev.luzifer.ui.view.ViewController;
import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import dev.luzifer.ui.view.views.SpicetifyView;
import javafx.application.Application;
import javafx.stage.Stage;

public class SpicetifyClient extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    ViewController viewController = new ViewController();
    viewController.showView(new SpicetifyView(new SpicetifyViewModel()));
  }
}
