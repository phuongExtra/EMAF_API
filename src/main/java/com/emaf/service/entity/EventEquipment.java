package com.emaf.service.entity;

import com.emaf.service.enumeration.EventEquipmentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * EventEquiment
 *
 * @author: VuongVT2
 * @since: 2022/10/06
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_event_equipment")
public class EventEquipment implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventEquipmentStatus status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "borrow_time", length = 14)
    private String borrowTime;

    @Column(name = "return_time", length = 14)
    private String returnTime;


}
