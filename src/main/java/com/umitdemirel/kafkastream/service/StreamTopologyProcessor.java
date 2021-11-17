package com.umitdemirel.kafkastream.service;

import com.umitdemirel.kafkastream.dto.OrderAggregatedEvent;
import com.umitdemirel.kafkastream.dto.OrderEvent;
import com.umitdemirel.kafkastream.dto.ProductConversionEvent;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StreamTopologyProcessor {

    private final String PRODUCT_CONVERSION_TOPIC = "product-conversion";
    private final String ORDER_EVENT_TOPIC = "order-event";
    private final String AGGREGATED_ORDER_EVENT_TOPIC = "aggregated-order-event";

    private final Serde<ProductConversionEvent> productConversionEventSerde;
    private final Serde<OrderEvent> orderEventSerde;
    private final Serde<OrderAggregatedEvent> orderAggregatedEventSerde;

    @Getter
    GlobalKTable<String, ProductConversionEvent> productConversionEventGlobalKTable;

    @Getter
    KStream<String, OrderEvent> orderEventKStream;

    @Getter
    KStream<String, OrderAggregatedEvent> orderAggregatedEventKStream;

    public StreamTopologyProcessor() {
        this.productConversionEventSerde = new JsonSerde<>(ProductConversionEvent.class);
        this.orderAggregatedEventSerde = new JsonSerde<>(OrderAggregatedEvent.class);
        this.orderEventSerde = new JsonSerde<>(OrderEvent.class);
    }

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        this.orderEventKStream = streamsBuilder
                .stream(ORDER_EVENT_TOPIC, Consumed.with(Serdes.String(), orderEventSerde));
        this.productConversionEventGlobalKTable = streamsBuilder
                .globalTable(PRODUCT_CONVERSION_TOPIC, Consumed.with(Serdes.String(), productConversionEventSerde));
        this.orderAggregatedEventKStream = streamsBuilder
                .stream(AGGREGATED_ORDER_EVENT_TOPIC, Consumed.with(Serdes.String(), orderAggregatedEventSerde));
        orderEventStream();
        productConversionCalculate();
    }

    public void orderEventStream() {
        orderEventKStream.selectKey((k, v) -> v.getProductId().toString()).groupByKey()
                .aggregate(() -> 0, (key, order, total) -> total + order.getOrderCount(),
                        Materialized.with(Serdes.String(), Serdes.Integer()))
                .toStream()
                .peek((k, v) -> System.out.println("Aggregated product : " + k + "Aggregated order count : " + v))
                .map((k, v) -> new KeyValue<>(k,
                        OrderAggregatedEvent.builder().productId(Long.parseLong(k)).totalOrderCount(v).build()))
                .to(AGGREGATED_ORDER_EVENT_TOPIC, Produced.with(Serdes.String(), orderAggregatedEventSerde));
    }

    public void productConversionCalculate() {
        orderAggregatedEventKStream
                .map((k, v) -> new KeyValue<>(v.getProductId().toString(), v))
                .leftJoin(productConversionEventGlobalKTable, (leftKey, leftValue) -> leftKey, (leftValue, rightValue) -> {
                    if (Objects.nonNull(leftValue)) {
                        System.out.println("left product id : " + leftValue.getProductId() + ", left total order count : " + leftValue.getTotalOrderCount());
                    }
                    if (Objects.nonNull(rightValue)) {
                        System.out.println("right product id : " + rightValue.getProductId() + ", right click count : " + rightValue.getClickCount());
                    }
                    return rightValue;
                })
                .peek((k, v) -> System.out.println("aggregated product key : " + k + ", click count " + v.getClickCount()));
    }
}
