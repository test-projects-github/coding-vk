package com.counters.aggregator.http.service;

import com.counters.aggregator.http.dto.CounterRecordDto;
import com.counters.aggregator.http.dto.ReportDto;
import com.counters.aggregator.service.CounterRecordService;
import com.counters.aggregator.http.exception.BadRequestException;
import com.counters.aggregator.http.parser.DurationParameterParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.ResponseEntity.badRequest;

@Slf4j
@RestController
public class CounterController {

    @Autowired
    private CounterRecordService counterRecordService;
    @Autowired
    private DurationParameterParser durationParameterParser;

    //TODO configure rejection policy, queue size, number of threads
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    @PostMapping("/counter_callback")
    @ResponseStatus(ACCEPTED)
    public void saveCounterRecord(@RequestBody CounterRecordDto recordDto) {
        //TODO maybe it is better to create separate wrapper class with executor service functionality
        executor.submit(() -> counterRecordService.saveRecord(recordDto));
    }

    @GetMapping("/consumption_report")
    public ReportDto getReport(@RequestParam("duration") String duration) {
        return counterRecordService.generateReport(durationParameterParser.parse(duration));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequest(BadRequestException e) {
        return badRequest().body(e.getMessage());
    }

    @PreDestroy
    public void destroy() {
        try {
            executor.awaitTermination(1, SECONDS);
        } catch (InterruptedException e) {
            log.warn("Not all tasks were executed before stopping service");
        }
    }
}
