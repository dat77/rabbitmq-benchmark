package com.example.rabbimmq_demo.rabbit.consumers;

import com.example.rabbimmq_demo.rabbit.config.RabbitConfig;
import com.example.rabbimmq_demo.rabbit.entities.PerfMessage;
import com.rabbitmq.client.Channel;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerfListener {

  private final AtomicLong counter = new AtomicLong();
  private final AtomicLong startTime = new AtomicLong();

  @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
  public void handle(PerfMessage msg, Message message, Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
    try {
      long c = counter.incrementAndGet();
      if (c == 1) {
        startTime.compareAndSet(0, System.nanoTime());
      }
      channel.basicAck(tag, false);
      // log.info("Thread {} processed {}", Thread.currentThread().getName(), msg.id());
    } catch (Exception e) {
      channel.basicNack(tag, false, false); // send to DLX if configured
    }
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
