package com.umitdemirel.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderAggregatedEvent implements Event, Serializable {

    private final long serialVersionUID = -1097024204672836247L;

    private Long productId;
    private int totalOrderCount;
}
