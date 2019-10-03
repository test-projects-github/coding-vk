package com.counters.aggregator.service;


import com.counters.aggregator.http.client.CounterServiceApi;
import com.counters.aggregator.http.dto.CounterRecordDto;
import com.counters.aggregator.http.dto.ReportDto;
import com.counters.aggregator.http.dto.VillageConsumptionDto;
import com.counters.aggregator.http.validator.CounterRecordDtoValidator;
import com.counters.aggregator.repository.CounterRecordRepository;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CounterRecordServiceTest {

    @Mock
    private CounterServiceApi counterServiceApi;
    @Mock
    private CounterRecordRepository recordRepository;
    @Mock
    private CounterRecordDtoValidator counterRecordDtoValidator;
    @InjectMocks
    private CounterRecordService recordService;

    private final String counterId = "10";
    private final List<VillageConsumptionDto> consumptionList = ImmutableList.of(new VillageConsumptionDto("name", 100L));
    private final ReportDto report = new ReportDto(consumptionList);
    private final CounterRecordDto invalidRecordDto = new CounterRecordDto("", "");
    private final CounterRecordDto validRecordDto = new CounterRecordDto(counterId, "10");

    @Test
    public void shouldNotSaveRecordWhenRecordDtoIsInvalid() {
        //given
        when(counterRecordDtoValidator.validate(invalidRecordDto))
                .thenReturn(false);

        //when
        recordService.saveRecord(invalidRecordDto);

        //then
        verifyNoInteractions(counterServiceApi);
        verifyNoInteractions(recordRepository);
    }

    @Test
    public void shouldNotSaveRecordServiceThrowsException() {
        //given
        when(counterRecordDtoValidator.validate(validRecordDto))
                .thenReturn(true);
        when(counterServiceApi.getVillage(counterId))
                .thenThrow(new RuntimeException());

        //when
        recordService.saveRecord(validRecordDto);

        //then
        verifyNoInteractions(recordRepository);
    }

    @Test
    public void shouldGenerateReport() {
        //given
        when(recordRepository.generateReport(any(Instant.class)))
                .thenReturn(consumptionList);

        //when
        ReportDto actualReport = recordService.generateReport(10);

        //then
        assertThat(actualReport).isEqualTo(report);
    }
}
