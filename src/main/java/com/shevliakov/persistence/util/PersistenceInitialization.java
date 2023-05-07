package com.shevliakov.persistence.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PersistenceInitialization {

  private PersistenceInitialization() {}

  public static void run() {
    try (Connection connection = ConnectionManager.get();
        Statement statementDDL = connection.createStatement();
        Statement statementDML = connection.createStatement()) {
      statementDDL.execute(getSQL(Path.of("db", "migrations", "ddl.sql").toString()));
      // temporarily left commented because of exception
      // statementDML.execute(getSQL(Path.of("db", "migrations", "dml.sql").toString()));
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static String getSQL(final String resourceName) {
    return new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    ConnectionManager.class.getClassLoader().getResourceAsStream(resourceName))))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
