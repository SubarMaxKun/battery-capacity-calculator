package com.shevliakov.persistence.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEntity {
  private int id;
  private String username;
  private String password;
}
