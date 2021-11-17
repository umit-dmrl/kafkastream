package com.umitdemirel.kafkastream.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value("${kafka.topic.productConversion}")
    private String productConversionTopic;

    @Value("${kafka.topic.orderEvent}")
    private String orderEvent;

    @Value("${kafka.topic.aggregatedOrderEvent}")
    private String aggregatedOrderEvent;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic productConversionTopic() {
        return new NewTopic(productConversionTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic orderEventTopic() {
        return new NewTopic(orderEvent, 1, (short) 1);
    }

    @Bean
    public NewTopic aggregatedOrderEventTopic() {
        return new NewTopic(aggregatedOrderEvent, 1, (short) 1);
    }
}
