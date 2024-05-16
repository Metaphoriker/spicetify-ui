package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.Main;
import dev.luzifer.model.SpicetifyCommands;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpicetifyInstallerViewModel extends BaseViewModel {

  private final BooleanProperty marketplaceProperty = new SimpleBooleanProperty(false);

  @NonNull private final Runnable afterInstallCallback;

  public void install() {
    progressMaxProperty.set(3);
    increaseProgress();

    String command;
    if (Main.isWindows()) {
      command =
          marketplaceProperty.get()
              ? SpicetifyCommands.WINDOWS_INSTALL_MARKETPLACE
              : SpicetifyCommands.WINDOWS_INSTALL;
    } else {
      command =
          marketplaceProperty.get()
              ? SpicetifyCommands.OTHER_INSTALL_MARKETPLACE
              : SpicetifyCommands.OTHER_INSTALL;
    }

    commandExecutor.executeCommand(command, this::afterInstall);
  }

  private void afterInstall() {
    commandExecutor.executeCommand(SpicetifyCommands.BACKUP_APPLY, this::increaseProgress);
    afterInstallCallback.run();
  }

  public BooleanProperty marketplaceProperty() {
    return marketplaceProperty;
  }
}
