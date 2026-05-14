package com.credit.util;

public class NonAtomicCounter {

  private long counter;

  public void increment() {
    counter++;
  }

  public long get() {
    return counter;
  }

  public void reset() {
    counter = 0;
  }
}