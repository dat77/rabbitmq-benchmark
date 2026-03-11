package com.example.rabbimmq_demo.rabbit.consumers;

import com.example.rabbimmq_demo.rabbit.config.RabbitConfig;
import com.example.rabbimmq_demo.rabbit.entities.PerfMessage;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerfListener {

  private final AtomicLong counter = new AtomicLong();
  private final AtomicLong startTime = new AtomicLong();

  @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
  @SneakyThrows
  public void handle(PerfMessage message) {
    long c = counter.incrementAndGet();
    if (c == 1) {
      startTime.compareAndSet(0, System.nanoTime());
    }
    Thread.sleep(100L);
  }

  public void reset() {
    counter.set(0);
    startTime.set(0);
  }

  public String stats() {
    long c = counter.get();
    long start = startTime.get();
    if (c == 0 || start == 0) return "No messages yet";
    long durationMs = (System.nanoTime() - start) / 1_000_000;
    double throughput = c * 1000.0 / durationMs;
    return "Received %d messages in %d ms (%.2f msg/s)".formatted(c, durationMs, throughput);
  }
}
