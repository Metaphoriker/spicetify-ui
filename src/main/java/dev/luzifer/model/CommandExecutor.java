package dev.luzifer.model;

import dev.luzifer.Main;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
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
    log.info("Executing command: " + command);
    try {
      Process process = createProcess(command);
      CompletableFuture<Void> processFuture =
          CompletableFuture.runAsync(
              () -> handleProcessExecution(process, processConsumer), EXECUTOR);

      processFuture.thenRun(callback).exceptionally(this::handleExecutionException);
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute command: " + command, e);
    }
  }

  private void handleProcessExecution(Process process, Consumer<Process> processConsumer) {
    try {
      logOutput(process);
      process.waitFor();
      processConsumer.accept(process);
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException("Error during process execution", e);
    }
  }

  private Void handleExecutionException(Throwable e) {
    log.error("Error while executing command", e);
    return null;
  }

  private Process createProcess(String command) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(prepareCommand(command));
    processBuilder.redirectErrorStream(true);
    return processBuilder.start();
  }

  private void logOutput(Process process) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.info(line);
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
