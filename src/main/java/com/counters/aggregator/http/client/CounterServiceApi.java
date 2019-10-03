package com.counters.aggregator.http.client;

import com.counters.aggregator.http.dto.VillageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "counter-service-client", url = "${feign.url}")
public interface CounterServiceApi {

    @GetMapping(value = "/counter", consumes = "application/json")
    VillageDto getVillage(@RequestParam("counterId") String counterId);
}
