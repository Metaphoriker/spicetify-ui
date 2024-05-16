package dev.luzifer.model;

import dev.luzifer.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CommandExecutor {

  private static final Executor EXECUTOR =
      Executors.newSingleThreadExecutor(
          runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
          });

  public void executeCommand(String command) {
    executeCommand(command, () -> {});
  }

  public void executeCommand(String command, Runnable callback) {
    executeCommand(command, callback, _ -> {});
  }

  public void executeCommand(String command, Runnable callback, Consumer<Process> processConsumer) {
    try {
      Process process = createProcess(command);
      CompletableFuture.runAsync(
              () -> {
                try {
                  process.waitFor();
                  processConsumer.accept(process);
                  logOutput(process);
                } catch (InterruptedException | IOException e) {
                  throw new RuntimeException(e);
                }
              },
              EXECUTOR)
          .thenRun(callback);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Process createProcess(String command) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(prepareCommand(command));
    return processBuilder.start();
  }

  private void logOutput(Process process) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    }
  }

  private List<String> prepareCommand(String command) {
    if (Main.isWindows()) {
      return List.of("powershell.exe", "-Command", command);
    } else {
      return List.of(command.split(" "));
    }
  }
}
