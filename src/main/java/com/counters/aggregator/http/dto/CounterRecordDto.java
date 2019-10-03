package com.counters.aggregator.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounterRecordDto {

    @JsonProperty("counter_id")
    private String counterId;
    private String amount;
}
