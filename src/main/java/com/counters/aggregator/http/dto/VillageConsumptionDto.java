package com.counters.aggregator.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillageConsumptionDto {

    @JsonProperty("village_name")
    private String village;
    private Long consumption;
}
