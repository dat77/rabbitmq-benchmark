package com.example.rabbimmq_demo.controllers;

import com.example.rabbimmq_demo.rabbit.producers.PerfProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perf")
@RequiredArgsConstructor
public class PerfController {

  private final PerfProducer perfProducer;

  @PostMapping("/send")
  public String send(
      @RequestParam(defaultValue = "1000") int count,
      @RequestParam(defaultValue = "1024") int size,
      @RequestParam(defaultValue = "direct") String exchangeType
  ) {
    return perfProducer.send(count, size, exchangeType);
  }


}
