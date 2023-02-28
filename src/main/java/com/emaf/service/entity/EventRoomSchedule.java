package com.emaf.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * EventRoomSchedule
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
@Table(name = "em_event_room_schedule")
public class EventRoomSchedule implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by", length = 10)
    private String createdBy;

    @Column(name = "updated_by", length = 10)
    private String updatedBy;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
