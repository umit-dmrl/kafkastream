package com.umitdemirel.kafkastream.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class StreamService {


    private final StreamTopologyProcessor streamTopologyProcessor;

    @PostConstruct
    public void process() {
        streamTopologyProcessor.orderAggregatedEventKStream
                .peek((k, v) -> System.out.println("test key : " + k + "test value" + v));
    }
}
