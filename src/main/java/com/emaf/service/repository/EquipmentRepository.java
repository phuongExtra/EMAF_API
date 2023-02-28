package com.emaf.service.repository;

import com.emaf.service.entity.Equipment;
import com.emaf.service.model.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, String> {

    @Query(value = "select r.*\n" +
            "from em_equipment r\n" +
            "where r.id not in (select ee.equipment_id\n" +
            "                   from em_event_equipment ee\n" +
            "                            left join em_event e on e.id = ee.event_id\n" +
            "                   where not (:borrowTime >= ee.return_time or :returnTime <= ee.borrow_time)\n" +
            "                     and e.status in (:status) and r.quantity <= ee.quantity)", nativeQuery = true)
    List<Equipment> getAllEquipmentAvailable(@Param("borrowTime") String borrowTime,
                                             @Param("returnTime") String returnTime,
                                             @Param("status") List<String> status);
//    e.equipmentName
    @Query(" select e from Equipment e where unaccent(LOWER(e.equipmentName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ")
    Page<Equipment> getAllEquipment(@Param("search") String search, Pageable pageable);
}