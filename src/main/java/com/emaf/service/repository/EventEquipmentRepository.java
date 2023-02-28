package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventEquipment;
import com.emaf.service.enumeration.EEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventEquipmentRepository extends JpaRepository<EventEquipment, Long> {

//    @Query("SELECT ee.equipment from EventEquipment ee where ee.equipment.id = :equipmentId " +
//            "and NOT (:startTime > ee.event.endTime OR :endTime < ee.event.startTime)")
//    List<EventEquipment> getAllByEventEquipmentByIdAndTime(String equipmentId, String startTime, String endTime, int quantity);


    @Query("select sum(ee.quantity) from EventEquipment ee where ee.equipment.id = :equipmentId " +
            " and not (:borrowTime > ee.returnTime OR :returnTime < ee.borrowTime) and ee.event.status in (:status)")
    Optional<Integer> getSumQuantityEquipmentInEvent(@Param("equipmentId") String equipmentId,
                                                     @Param("borrowTime") String borrowTime,
                                                     @Param("returnTime") String returnTime,
                                                     @Param("status") List<EEventStatus> status);

    void deleteAllByEvent(Event event);

}