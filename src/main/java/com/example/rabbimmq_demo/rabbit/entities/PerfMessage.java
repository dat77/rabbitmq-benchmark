package com.example.rabbimmq_demo.rabbit.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfMessage {
  private String id;
  private String type;
  private String payload;
}

