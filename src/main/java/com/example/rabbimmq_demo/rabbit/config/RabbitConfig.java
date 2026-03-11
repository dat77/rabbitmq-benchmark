package com.example.rabbimmq_demo.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  public static final String DIRECT_EXCHANGE = "perf.direct";
  public static final String FANOUT_EXCHANGE = "perf.fanout";
  public static final String TOPIC_EXCHANGE  = "perf.topic";

  public static final String QUEUE_NAME = "perf.queue";

  public static final String DIRECT_ROUTING_KEY = "perf.key";
  public static final String TOPIC_ROUTING_KEY = "perf.#";

  @Bean
  public Queue perfQueue() {
    return QueueBuilder.durable(QUEUE_NAME)
        .withArgument("x-dead-letter-exchange", RetryConfig.DLX_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", RetryConfig.RETRY_DIRECT_ROUTING_KEY)
        .build();
  }

  // DIRECT

  @Bean
  public DirectExchange directExchange() {
    return new DirectExchange(DIRECT_EXCHANGE);
  }

  @Bean
  public Binding directBinding(Queue perfQueue, DirectExchange directExchange) {
    return BindingBuilder.bind(perfQueue).to(directExchange).with(DIRECT_ROUTING_KEY);
  }

  // FANOUT

  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(FANOUT_EXCHANGE);
  }

  @Bean
  public Binding fanoutBinding(Queue perfQueue, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(perfQueue).to(fanoutExchange);
  }

  // TOPIC

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(TOPIC_EXCHANGE);
  }

  @Bean
  public Binding topicBinding(Queue perfQueue, TopicExchange topicExchange) {
    return BindingBuilder.bind(perfQueue).to(topicExchange).with(TOPIC_ROUTING_KEY);
  }

}
