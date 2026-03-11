package com.example.rabbimmq_demo.rabbit.producers;

import com.example.rabbimmq_demo.rabbit.config.RabbitConfig;
import com.example.rabbimmq_demo.rabbit.entities.PerfMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerfProducer {

  private final RabbitTemplate rabbitTemplate;

  public String send(int count, int size, String exchangeType) {
    String exchange = switch (exchangeType) {
      case "fanout" -> RabbitConfig.FANOUT_EXCHANGE;
      case "topic"  -> RabbitConfig.TOPIC_EXCHANGE;
      case "direct" -> RabbitConfig.DIRECT_EXCHANGE;
      default       -> throw new IllegalArgumentException("Unknown exchangeType: " + exchangeType);
    };
    long start = System.nanoTime();
    for (int i = 0; i < count; i++) {
      PerfMessage msg =
          new PerfMessage(UUID.randomUUID().toString(),"TEST",PayloadFactory.payloadOfSize(size));
      rabbitTemplate.convertAndSend(exchange, RabbitConfig.DIRECT_ROUTING_KEY, msg);
    }
    long end = System.nanoTime();
    return "Sent %d messages in %d ms".formatted(count, (end - start) / 1_000_000);
  }


}
