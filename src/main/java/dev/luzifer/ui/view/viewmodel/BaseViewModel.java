package dev.luzifer.ui.view.viewmodel;

import dev.luzifer.ui.view.ViewModel;
import dev.luzifer.ui.view.viewmodel.utils.CommandExecutor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class BaseViewModel implements ViewModel {

  protected final DoubleProperty progressProperty = new SimpleDoubleProperty(0);
  protected final IntegerProperty progressMaxProperty = new SimpleIntegerProperty(0);

  protected final CommandExecutor commandExecutor = new CommandExecutor();

  private int progressCount = 0;

  public DoubleProperty progressProperty() {
    return progressProperty;
  }

  protected void increaseProgress() {
    var maxValue = progressMaxProperty.get();
    progressProperty.set((double) ++progressCount / maxValue);
    resetProgressIfNecessary();
  }

  public void resetProgress() {
    progressCount = 0;
    progressProperty.set(0);
  }

  private void resetProgressIfNecessary() {
    if (progressCount == progressMaxProperty.get()) {
      resetProgress();
    }
  }
}
