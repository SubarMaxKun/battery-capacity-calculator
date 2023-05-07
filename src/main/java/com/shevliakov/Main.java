package com.shevliakov;

import com.shevliakov.persistence.util.ConnectionManager;
import com.shevliakov.persistence.util.PersistenceInitialization;

public class Main {

  // Base method, will be improved in future
  public static void main(String[] args) {
    try {
      PersistenceInitialization.run();
      /*} catch (Exception e) {
      // TODO:*/
    } finally {
      ConnectionManager.closePool();
    }
  }
}
