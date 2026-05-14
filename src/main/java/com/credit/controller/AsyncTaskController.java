package com.credit.controller;

import com.credit.exception.NotFoundException;
import com.credit.model.AsyncTask;
import com.credit.util.AsyncTaskExecutor;
import com.credit.util.AsyncTaskStorage;
import com.credit.util.AtomicCounter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans/async")
@RequiredArgsConstructor
public class AsyncTaskController {

  private final AsyncTaskExecutor asyncTaskExecutor;
  private final AsyncTaskStorage asyncTaskStorage;
  private final AtomicCounter atomicCounter;

  /**
   * Запускает асинхронную бизнес-операцию. Возвращает taskId сразу,
   * не дожидаясь завершения.
   */
  @PostMapping("/process")
  public ResponseEntity<Map<String, String>> startProcessing() {
    String taskId = asyncTaskStorage.createTask();
    asyncTaskExecutor.executeTask(taskId);
    return new ResponseEntity<>(
        Map.of("taskId", taskId, "message", "Task accepted for async processing"),
        HttpStatus.ACCEPTED);
  }

  /**
   * Проверка статуса асинхронной задачи по её ID.
   */
  @GetMapping("/status/{taskId}")
  public ResponseEntity<AsyncTask> getStatus(@PathVariable String taskId) {
    AsyncTask task = asyncTaskStorage.getTask(taskId)
        .orElseThrow(() -> new NotFoundException("Async task not found with ID: " + taskId));
    return ResponseEntity.ok(task);
  }

  /**
   * Текущее значение потокобезопасного счётчика (для демонстрации пункта 2).
   */
  @GetMapping("/counter")
  public ResponseEntity<Map<String, Long>> getCounter() {
    return ResponseEntity.ok(Map.of("counter", atomicCounter.get()));
  }
}