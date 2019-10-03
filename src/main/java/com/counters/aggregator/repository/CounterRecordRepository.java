package com.counters.aggregator.repository;

import com.counters.aggregator.http.dto.VillageConsumptionDto;
import com.counters.aggregator.model.CounterRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CounterRecordRepository extends CrudRepository<CounterRecord, Integer> {

    @Modifying
    @Query(value = "insert into counter_record (id, village, amount, created, delta) " +
            "values ( " +
            "    counter_record_id_seq.nextval,  " +
            "    :village,  " +
            "    :amount,  " +
            "    :created,  " +
            "    :amount - COALESCE((select cr.amount  " +
            "                        from counter_record cr  " +
            "                        where cr.created = (select max(crmax.created) " +
            "                                            from counter_record crmax " +
            "                                            where crmax.village = :village) " +
            "                        ), 0) " +
            "        ) ",
            nativeQuery = true)
    void insertNewRecord(@Param("village") String village,
                         @Param("amount") Integer amount,
                         @Param("created") Instant created);

    @Query("select new com.counters.aggregator.http.dto.VillageConsumptionDto(cr.village, sum(cr.delta)) " +
            "from CounterRecord cr " +
            "where cr.created >= :startFromDateTime " +
            "group by cr.village")
    List<VillageConsumptionDto> generateReport(@Param("startFromDateTime") Instant startFromDateTime);
}
