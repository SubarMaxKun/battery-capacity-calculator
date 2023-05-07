package com.shevliakov.persistence.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculationHistoryEntity {
  private int historyId;
  private int idUsers;
  private String batteryCapacity;
  private String consumedPower;
  private String workingTime;
  private int batteryVoltage;
  private float inverterEfficiency;
}
