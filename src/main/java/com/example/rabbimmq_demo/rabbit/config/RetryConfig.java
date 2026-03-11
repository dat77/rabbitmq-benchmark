package com.example.rabbimmq_demo.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfig {

  public static final String DLX_EXCHANGE = "perf.dlx";
  public static final String RETRY_QUEUE = "perf.retry";
  public static final String DLQ_QUEUE = "perf.dlq";

  public static final String RETRY_DIRECT_ROUTING_KEY = "retry.direct";


  public static final String DLQ_ROUTING_KEY = "dlq";

  @Bean
  public DirectExchange dlxExchange() {
    return new DirectExchange(DLX_EXCHANGE);
  }

  // DIRECT

  @Bean
  public Queue retryDirectQueue() {
    return QueueBuilder.durable(RETRY_QUEUE)
        .withArgument("x-message-ttl", 5000) // 5 sec delay
        .withArgument("x-dead-letter-exchange", RabbitConfig.DIRECT_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", RabbitConfig.DIRECT_ROUTING_KEY)
        .build();
  }

  @Bean
  public Binding retryDirectBinding(Queue retryDirectQueue, DirectExchange dlxExchange) {
    return BindingBuilder.bind(retryDirectQueue).to(dlxExchange).with(RETRY_DIRECT_ROUTING_KEY);
  }

  // FANOUT


  // TOPIC


  // DLQ

  @Bean
  public Queue dlqQueue() {
    return QueueBuilder.durable(DLQ_QUEUE).build();
  }

  @Bean
  public Binding dlqBinding(Queue dlqQueue, DirectExchange dlxExchange) {
    return BindingBuilder.bind(dlqQueue).to(dlxExchange).with(DLQ_ROUTING_KEY);
  }


}
