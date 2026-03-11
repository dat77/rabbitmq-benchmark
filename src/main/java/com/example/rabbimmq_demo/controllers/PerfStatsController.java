package com.example.rabbimmq_demo.controllers;

import com.example.rabbimmq_demo.rabbit.consumers.PerfListener;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perf")
@RequiredArgsConstructor
public class PerfStatsController {

  private final PerfListener listener;

  @GetMapping("/stats")
  public String stats() {
    return listener.stats();
  }

  @PostMapping("/reset")
  public void reset() {
    listener.reset();
  }

}
