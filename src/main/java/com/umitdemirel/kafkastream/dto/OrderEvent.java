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
public class OrderEvent implements Event, Serializable {

    private final long serialVersionUID = -4772483359095773641L;

    private Long productId;
    private int orderCount;
}
