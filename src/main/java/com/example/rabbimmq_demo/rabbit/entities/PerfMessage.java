package com.example.rabbimmq_demo.rabbit.entities;

public record PerfMessage (
  String id,
  String type,
  String payload
){}

