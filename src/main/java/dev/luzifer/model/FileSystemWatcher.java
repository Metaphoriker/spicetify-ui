package dev.luzifer.model;

import dev.luzifer.Main;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class FileSystemWatcher implements Runnable {

  @NonNull private final Consumer<String> callback;

  @Override
  public void run() {
    try (WatchService watcher = FileSystems.getDefault().newWatchService()) {

      Path themesDirectory = Main.getThemeFolder();
      themesDirectory.register(
          watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

      while (true) {
        WatchKey key = watcher.take();
        for (WatchEvent<?> event : key.pollEvents()) {

          WatchEvent.Kind<?> kind = event.kind();
          if (kind == StandardWatchEventKinds.OVERFLOW) continue;

          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path filename = ev.context();

          callback.accept(filename.toString());
        }
        key.reset();
      }

    } catch (IOException | InterruptedException e) {
      log.error("Error while watching themes folder", e);
    }
  }
}
