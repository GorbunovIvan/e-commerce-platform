package org.example.repository;

import org.example.model.StatusTrackerRecord;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StatusTrackerRecordRepository extends MongoRepository<StatusTrackerRecord, String> {

    List<StatusTrackerRecord> findAllByOrderId(@Param("orderId") String orderId);
    List<StatusTrackerRecord> findAllByOrderIdIn(@Param("orderIds") List<String> orderIds);
    Optional<StatusTrackerRecord> findFirstByOrderIdOrderByTimeDesc(@Param("orderId") String orderId);

    @Aggregation(pipeline = {
            "{ '$sort': { 'time': -1 } }",
            "{ '$group': { '_id': '$orderId', 'latestRecord': { '$first': '$$ROOT' } } }",
            "{ '$replaceRoot': { 'newRoot': '$latestRecord' } }"
    })
    List<StatusTrackerRecord> findAllCurrentStatuses();
}
