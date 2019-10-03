package com.counters.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CountersAggregator {

    public static void main(String[] args) {
        SpringApplication.run(CountersAggregator.class);
    }
}
