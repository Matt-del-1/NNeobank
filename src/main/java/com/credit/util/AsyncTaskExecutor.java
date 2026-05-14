package com.credit.util;

import com.credit.model.AsyncTaskStatus;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncTaskExecutor {

  public static final int TASK_EXECUTION_TIME_MILLIS = 10000;
  private final AsyncTaskStorage asyncTaskStorage;
  private final AtomicCounter atomicCounter;

  /**
   * Длительная асинхронная бизнес-операция: "пересчёт займов".
   * Имитирует тяжёлую работу через Thread.sleep.
   * Параллельно инкрементирует потокобезопасный счётчик.
   */
  @Async("asyncExecutorPool")
  public CompletableFuture<Void> executeTask(String taskId) {
    log.info("Async task {} started in thread {}", taskId, Thread.currentThread().getName());
    try {
      asyncTaskStorage.updateStatus(taskId, AsyncTaskStatus.IN_PROGRESS);

      Thread.sleep(TASK_EXECUTION_TIME_MILLIS);

      // потокобезопасный инкремент: показывает работу AtomicLong
      long processed = atomicCounter.incrementAndGet();

      asyncTaskStorage.updateResult(taskId,
          "Processed loans batch. Total processed batches: " + processed);
      asyncTaskStorage.updateStatus(taskId, AsyncTaskStatus.DONE);

      log.info("Async task {} completed. Total counter = {}", taskId, processed);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      asyncTaskStorage.updateStatus(taskId, AsyncTaskStatus.FAILED);
      log.error("Async task {} was interrupted", taskId, exception);
    } catch (RuntimeException exception) {
      asyncTaskStorage.updateStatus(taskId, AsyncTaskStatus.FAILED);
      log.error("Async task {} failed", taskId, exception);
    }
    return CompletableFuture.completedFuture(null);
  }
}