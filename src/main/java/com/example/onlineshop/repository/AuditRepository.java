package com.example.onlineshop.repository;

import com.example.onlineshop.entity.Audit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface AuditRepository extends CrudRepository<Audit, Long> {

    List<Audit> findAllByObjectIdAndLastModifiedDateIsGreaterThanEqualAndLastModifiedDateIsLessThanOrderByLastModifiedDate(
            String objectId, String startDate, String endDate);

    @Query(value = "SELECT * from audit_object where object_id = ?1" +
            "  and TO_TIMESTAMP(last_modified_date, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"')\n" +
            "      between TO_TIMESTAMP(?2, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"')\n" +
            "          and TO_TIMESTAMP(?3, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"')" +
            " order by last_modified_date desc", nativeQuery = true)
    List<Audit> getAudits(String objectId, String startDate, String endDate);

}
