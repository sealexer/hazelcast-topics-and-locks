package com.alex.test.hazelcast.util;


public class ThreadUtil {

  protected static final int HEAVY_OPERATION_DURATION_MS = 1000;

  public static String currentThreadName() {
    return Thread.currentThread().getName();
  }

  public static void someHeavyOperation() {
    try {
      Thread.sleep(HEAVY_OPERATION_DURATION_MS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
