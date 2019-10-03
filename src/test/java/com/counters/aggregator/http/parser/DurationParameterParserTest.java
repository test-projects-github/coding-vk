package com.counters.aggregator.http.parser;

import com.counters.aggregator.http.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DurationParameterParserTest {

    private DurationParameterParser parser = new DurationParameterParser();

    @ParameterizedTest
    @MethodSource("validValues")
    public void shouldParseDurationAndReturnValue(String duration, Integer expectedDuration) {
        //when
        Integer actualDuration = parser.parse(duration);

        //then
        assertThat(actualDuration).isEqualTo(expectedDuration);
    }

    private static Stream<Arguments> validValues() {
        return Stream.of(Arguments.of("1h", 1),
                Arguments.of("99h", 99),
                Arguments.of("35h", 35));
    }

    @ParameterizedTest
    @MethodSource("invalidValues")
    public void shouldParseDurationAndThrowException(String duration, String message) {
        //expected
        assertThatThrownBy(() -> parser.parse(duration))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> invalidValues() {
        return Stream.of(Arguments.of("h", "Duration 'h' is invalid. It must integer number"),
                Arguments.of(null, "Duration 'null' is invalid. It must integer number"),
                Arguments.of("", "Duration '' is invalid. It must integer number"),
                Arguments.of("0h", "Duration should be more than 1h and less than 100h"),
                Arguments.of("101h", "Duration should be more than 1h and less than 100h")
        );
    }
}
