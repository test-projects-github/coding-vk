package com.counters.aggregator.http.validator;

import com.counters.aggregator.http.dto.CounterRecordDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CounterRecordDtoValidatorTest {

    private CounterRecordDtoValidator validator = new CounterRecordDtoValidator();

    @ParameterizedTest
    @MethodSource("values")
    public void shouldValidateCounterRecordDto(CounterRecordDto record, boolean isValid) {
        //when
        boolean validationResult = validator.validate(record);

        //then
        assertThat(validationResult).isEqualTo(isValid);
    }

    private static Stream<Arguments> values() {
        return Stream.of(Arguments.of(new CounterRecordDto(null, "10"), false),
                Arguments.of(new CounterRecordDto("", "10"), false),
                Arguments.of(new CounterRecordDto("10", null), false),
                Arguments.of(new CounterRecordDto("10", ""), false),
                Arguments.of(new CounterRecordDto("10", "asdf"), false),
                Arguments.of(new CounterRecordDto("10", "10"), true)
        );
    }
}