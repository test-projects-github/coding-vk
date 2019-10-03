package com.counters.aggregator.http.service;

import com.counters.aggregator.http.dto.CounterRecordDto;
import com.counters.aggregator.http.dto.ReportDto;
import com.counters.aggregator.http.dto.VillageConsumptionDto;
import com.counters.aggregator.repository.CounterRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.time.Instant.now;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CounterControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CounterRecordRepository recordRepository;

    private final CounterRecordDto counterRecordDto = new CounterRecordDto("1", "100");
    private final String villageName1 = "village1";
    private final String villageName2 = "village2";
    private final String villageName3 = "village3";
    private final String villageName4 = "village4";

    @Test
    public void shouldSaveCounterRecord() throws Exception {
        //given
        createGetCounterNameByIdStub();

        //when
        mockMvc.perform(post("/counter_callback")
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(counterRecordDto)))
                .andExpect(status().isAccepted());

        //then
        await().atMost(10, SECONDS)
                .until(() -> recordRepository.count() == 1);
        assertThat(recordRepository.findAll())
                .extracting("village", "amount", "delta")
                .contains(tuple("name", 100, 100));
    }

    @Test
    public void shouldGenerateReport() throws Exception {
        //given
        insertReportRecords();

        //when
        MvcResult result = mockMvc.perform(get("/consumption_report")
                .param("duration", "24h")
                .content(toJson(counterRecordDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(fromJson(result.getResponse().getContentAsString(), ReportDto.class))
                .isEqualTo(report());
    }

    @Test
    public void shouldNotGenerateReportIfDurationParameterIsInvalid() throws Exception {
        //expected
        mockMvc.perform(get("/consumption_report")
                .param("duration", "h"))
                .andExpect(status().isBadRequest());
    }

    private void createGetCounterNameByIdStub() {
        stubFor(WireMock.get(urlEqualTo("/counter?counterId=1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"1\",\"village_name\":\"name\"}")
                ));
    }

    private void insertReportRecords() {
        recordRepository.deleteAll(); //TODO this require proper fix. Because controller contains thread pool their transaction rollback is not done. So, previous test left one record already in database
        recordRepository.insertNewRecord(villageName1, 80, now());
        recordRepository.insertNewRecord(villageName1, 100, now());
        recordRepository.insertNewRecord(villageName1, 250, now());

        recordRepository.insertNewRecord(villageName2, 80, now());

        recordRepository.insertNewRecord(villageName3, 0, now());
        recordRepository.insertNewRecord(villageName3, 0, now());
        recordRepository.insertNewRecord(villageName3, 10, now());
        recordRepository.insertNewRecord(villageName3, 250, now());
        recordRepository.insertNewRecord(villageName3, 500, now());
        recordRepository.insertNewRecord(villageName3, 8000, now());

        recordRepository.insertNewRecord(villageName4, 0, now());
    }

    private ReportDto report() {
        return new ReportDto(ImmutableList.of(new VillageConsumptionDto(villageName1, 250L),
                new VillageConsumptionDto(villageName2, 80L),
                new VillageConsumptionDto(villageName3, 8000L),
                new VillageConsumptionDto(villageName4, 0L)));
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private <T> T fromJson(String body, Class<T> className) throws Exception {
        return new ObjectMapper().readValue(body, className);
    }
}
