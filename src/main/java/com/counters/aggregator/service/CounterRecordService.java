package com.counters.aggregator.service;

import com.counters.aggregator.http.client.CounterServiceApi;
import com.counters.aggregator.http.dto.CounterRecordDto;
import com.counters.aggregator.http.dto.ReportDto;
import com.counters.aggregator.repository.CounterRecordRepository;
import com.counters.aggregator.http.validator.CounterRecordDtoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static java.lang.Integer.parseInt;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;

@Slf4j
@Service
public class CounterRecordService {

    @Autowired
    private CounterRecordRepository recordRepository;
    @Autowired
    private CounterServiceApi counterServiceApi;
    @Autowired
    private CounterRecordDtoValidator counterRecordDtoValidator;

    @Transactional
    public void saveRecord(CounterRecordDto recordDto) {
        try {
            if (counterRecordDtoValidator.validate(recordDto)) {
                String villageName = counterServiceApi.getVillage(recordDto.getCounterId()).getVillageName();
                Integer amount = parseInt(recordDto.getAmount());
                recordRepository.insertNewRecord(
                        villageName,
                        amount,
                        Instant.now()
                );
            }
        } catch (Exception e) {
            log.error("Error while saving counter record", e);
        }
    }

    @Transactional
    public ReportDto generateReport(Integer reportForHours) {
        Instant reportFromDate = now().minus(reportForHours, HOURS);
        return new ReportDto(recordRepository.generateReport(reportFromDate));
    }
}
