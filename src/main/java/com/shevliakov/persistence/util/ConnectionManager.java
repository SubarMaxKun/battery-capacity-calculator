package com.shevliakov.persistence.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConnectionManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);
  private static final String URL_KEY = "db.url";
  private static final String USERNAME_KEY = "db.username";
  private static final String PASSWORD_KEY = "db.password";
  private static final String POOL_SIZE_KEY = "db.pool.size";
  private static final Integer DEFAULT_POOL_SIZE = 10;
  private static BlockingQueue<Connection> pool;
  private static List<Connection> sourceConnections;

  static {
    loadDriver();
    initConnectionPool();
  }

  public static Connection get() {
    try {
      LOGGER.info("connection received from pool[%d]".formatted(pool.size()));
      return pool.take();
    } catch (InterruptedException e) {
      LOGGER.error("failed to take connection from pool. %s".formatted(e));
      throw new RuntimeException(e);
    }
  }

  public static void closePool() {
    try {
      for (Connection sourceConnection : sourceConnections) {
        sourceConnection.close();
      }
      LOGGER.info("all connections successfully closed");
    } catch (SQLException e) {
      LOGGER.error("failed to close all connections from pool. %s".formatted(e));
      throw new RuntimeException(e);
    }
  }

  private static void loadDriver() {
    try {
      String driverName = "com.mysql.cj.jdbc.Driver";
      Class.forName(driverName);
      LOGGER.info("driver [%s] loaded".formatted(driverName));
    } catch (ClassNotFoundException e) {
      LOGGER.error("driver failed to load. %s".formatted(e.getMessage()));
      throw new RuntimeException(e.getMessage());
    }
  }

  private static Connection open() {
    try {
      return DriverManager.getConnection(
          PersistenceConfig.get(URL_KEY),
          PersistenceConfig.get(USERNAME_KEY),
          PersistenceConfig.get(PASSWORD_KEY));
    } catch (SQLException e) {
      LOGGER.error("failed to open connection. %s".formatted(e.getMessage()));
      throw new RuntimeException(e.getMessage());
    }
  }

  private static void initConnectionPool() {
    String poolSize = PersistenceConfig.get(POOL_SIZE_KEY);
    int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
    LOGGER.info("connection pool size = %s".formatted(size));
    pool = new ArrayBlockingQueue<>(size);
    sourceConnections = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Connection connection = open();
      Connection proxyConnection =
          (Connection)
              Proxy.newProxyInstance(
                  ConnectionManager.class.getClassLoader(),
                  new Class[] {Connection.class},
                  ((proxy, method, args) ->
                      method.getName().equals("close")
                          ? pool.add((Connection) proxy)
                          : method.invoke(connection, args)));
      pool.add(proxyConnection);
      sourceConnections.add(connection);
      LOGGER.info("connection №%d opened".formatted(i + 1));
    }
  }

  private ConnectionManager() {}
}
