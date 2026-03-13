package com.example.rabbimmq_demo.rabbit.consumers;

import static com.example.rabbimmq_demo.rabbit.config.RetryConfig.DLQ_ROUTING_KEY;
import static com.example.rabbimmq_demo.rabbit.config.RetryConfig.DLX_EXCHANGE;
import static com.example.rabbimmq_demo.rabbit.config.RetryConfig.MAX_RETRIES;
import static com.example.rabbimmq_demo.rabbit.config.RetryConfig.RETRY_DIRECT_ROUTING_KEY;

import com.example.rabbimmq_demo.rabbit.config.RabbitConfig;
import com.example.rabbimmq_demo.rabbit.entities.PerfMessage;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
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
      // channel.basicNack(tag, false, false); // send to DLX if configured
      processRetries(message, channel, tag);
    }
  }

  private void processRetries(Message message, Channel channel, long tag) throws Exception {
    Integer retryCount = message.getMessageProperties().getHeader("x-retry-count");
    if (retryCount == null) retryCount = 0;

    // Convert Spring MessageProperties → AMQP.BasicProperties
    DefaultMessagePropertiesConverter converter = new DefaultMessagePropertiesConverter();
    BasicProperties basicProps = converter.fromMessageProperties(message.getMessageProperties(), "UTF-8");

    if (retryCount >= MAX_RETRIES) {
      // send to DLQ
      message.getMessageProperties().setHeader("x-retry-count", retryCount);
      channel.basicPublish(DLX_EXCHANGE, DLQ_ROUTING_KEY, basicProps, message.getBody());
      channel.basicAck(tag, false);
      return;
    }

    // send to retry queue
    message.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
    basicProps = converter.fromMessageProperties(message.getMessageProperties(), "UTF-8");
    channel.basicPublish(DLX_EXCHANGE, RETRY_DIRECT_ROUTING_KEY, basicProps, message.getBody());
    channel.basicAck(tag, false);
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
