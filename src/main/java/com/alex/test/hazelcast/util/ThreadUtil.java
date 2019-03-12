package com.alex.test.hazelcast.util;


public class ThreadUtil {

  protected static final int HEAVY_OPERATION_DURATION_MS = 1000;

  public static String currentThreadName() {
    return Thread.currentThread().getName();
  }

  public static void someHeavyOperation() {
    sleep(HEAVY_OPERATION_DURATION_MS);
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
