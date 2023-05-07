package com.shevliakov.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);

  private static final Properties PROPERTIES = new Properties();

  static {
    loadProperties();
  }

  private PersistenceConfig() {}

  public static String get(String key) {
    return PROPERTIES.getProperty(key);
  }

  private static void loadProperties() {
    try (InputStream applicationProperties =
        PersistenceConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
      PROPERTIES.load(applicationProperties);
    } catch (IOException e) {
      LOGGER.error("failed to read properties. %s".formatted(e.getMessage()));
      throw new RuntimeException(e);
    }
  }
}
