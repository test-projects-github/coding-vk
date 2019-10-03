package com.counters.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounterRecord {

    @Id
    @GeneratedValue(generator = "counter_record_id_generator")
    @SequenceGenerator(name = "counter_record_id_generator",
            sequenceName = "counter_record_id_seq",
            allocationSize = 1)
    private Integer id;
    private String village;
    private Integer amount;
    private Integer delta;
    private Instant created;
    //TODO create indexes
}
