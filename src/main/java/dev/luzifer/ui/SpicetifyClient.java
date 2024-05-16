package dev.luzifer.ui;

import dev.luzifer.Main;
import dev.luzifer.ui.view.ViewController;
import dev.luzifer.ui.view.viewmodel.SpicetifyInstallerViewModel;
import dev.luzifer.ui.view.viewmodel.SpicetifyViewModel;
import dev.luzifer.ui.view.views.SpicetifyInstallerView;
import dev.luzifer.ui.view.views.SpicetifyView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class SpicetifyClient extends Application {

  @Override
  public void start(Stage stage) {
    ViewController viewController = new ViewController();
    SpicetifyView spicetifyView = new SpicetifyView(new SpicetifyViewModel());

    if (Main.isSpicetifyInstalled()) viewController.showView(spicetifyView);
    else
      viewController.showView(
          new SpicetifyInstallerView(
              new SpicetifyInstallerViewModel(
                  () -> {
                    Platform.runLater(
                        () -> {
                          viewController.closeAllViews();
                          viewController.showView(spicetifyView);
                        });
                  })));
  }
}
