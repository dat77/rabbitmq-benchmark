package com.example.rabbimmq_demo.rabbit.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public record PerfMessage (
  String id,
  String type,
  String payload
){}

