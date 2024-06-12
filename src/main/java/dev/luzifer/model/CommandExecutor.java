package dev.luzifer.model;

import dev.luzifer.Main;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.*;
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

  private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

  public void executeCommand(String command) {
    executeCommand(command, () -> {});
  }

  public void executeCommand(String command, Runnable callback) {
    executeCommand(command, callback, process -> {});
  }

  public void executeCommand(String command, Runnable callback, Consumer<Process> processConsumer) {
    executeCommand(command, callback, processConsumer, 20, TimeUnit.SECONDS);
  }

  public void executeCommand(
      String command,
      Runnable callback,
      Consumer<Process> processConsumer,
      long timeout,
      TimeUnit unit) {
    log.info("Executing command: {}", command);
    try {
      Process process = createProcess(command);
      CompletableFuture<Void> processFuture =
          CompletableFuture.runAsync(
              () -> handleProcessExecution(process, processConsumer), EXECUTOR);

      ScheduledFuture<?> timeoutFuture = scheduleTimeout(process, timeout, unit);

      processFuture
          .thenRun(
              () -> {
                timeoutFuture.cancel(true);
                callback.run();
              })
          .exceptionally(this::handleExecutionException);

    } catch (IOException e) {
      throw new RuntimeException("Failed to execute command: " + command, e);
    }
  }

  private void handleProcessExecution(Process process, Consumer<Process> processConsumer) {
    try {
      logProcessOutput(process);
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

  private void logProcessOutput(Process process) throws IOException {
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

  private ScheduledFuture<?> scheduleTimeout(Process process, long timeout, TimeUnit unit) {
    return SCHEDULER.schedule(
        () -> {
          process.destroy();
          log.warn("Process terminated due to timeout.");
        },
        timeout,
        unit);
  }
}
