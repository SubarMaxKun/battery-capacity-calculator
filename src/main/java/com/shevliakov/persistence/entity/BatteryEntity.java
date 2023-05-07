package com.shevliakov.persistence.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatteryEntity {
  private int batteryId;
  private String brand;
  private String model;
  private int voltage;
  private String capacity;
}
