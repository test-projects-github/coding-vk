package com.counters.aggregator.http.parser;

import com.counters.aggregator.http.exception.BadRequestException;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class DurationParameterParser {

    private static final Integer MAX_REPORT_DURATION = 100;

    public Integer parse(String duration) {
        if (isNotBlank(duration) && duration.matches("\\d+h")) {
            int parsedDuration = Integer.parseInt(duration.substring(0, duration.length() - 1));
            if (parsedDuration == 0 || parsedDuration > MAX_REPORT_DURATION) {
                throw new BadRequestException("Duration should be more than 1h and less than " + MAX_REPORT_DURATION + "h");
            }
            return parsedDuration;
        }

        throw new BadRequestException("Duration '" + duration + "' is invalid. It must integer number");
    }
}
