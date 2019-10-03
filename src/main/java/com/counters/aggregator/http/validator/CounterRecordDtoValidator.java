package com.counters.aggregator.http.validator;

import com.counters.aggregator.http.dto.CounterRecordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Slf4j
@Component
public class CounterRecordDtoValidator {

    public boolean validate(CounterRecordDto recordDto) {
        List<String> errors = new ArrayList<>();

        if (isBlank(recordDto.getCounterId())) {
            errors.add(blankFieldError("counterId"));
        }

        //TODO is is not clear if it is should be integer, greater than zero
        if (isBlank(recordDto.getAmount())) {
            errors.add(blankFieldError("amount"));
        }

        if (!isNumeric(recordDto.getAmount())) {
            errors.add(format("Field 'amount' with value '%s' must be integer", recordDto.getAmount()));
        }

        if (!errors.isEmpty()) {
            String message = errors.stream()
                    .collect(joining(". ",
                            format("Counter record %s will be ignored. ", recordDto),
                            "."));
            log.warn(message);
            return false;
        }

        return true;
    }

    private String blankFieldError(String fieldName) {
        return format("Field '%s' is blank", fieldName);
    }
}
