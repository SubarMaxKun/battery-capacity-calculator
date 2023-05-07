package com.shevliakov.persistence.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookmarkEntity {
  private int idUsers;
  private int idBatteries;
}
