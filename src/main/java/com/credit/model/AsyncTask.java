package com.credit.model;

import lombok.Data;

@Data
public class AsyncTask {

  private final String id;
  private AsyncTaskStatus status;
  private String result;

  public AsyncTask(String id) {
    this.id = id;
    this.status = AsyncTaskStatus.PENDING;
  }
}