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
public class ProductConversionEvent implements Event, Serializable {

    private final long serialVersionUID = -5509788304931878587L;

    private Long productId;
    private int clickCount;
}
