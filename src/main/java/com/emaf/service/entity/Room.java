package com.emaf.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Room
 *
 * @author: VuongVT2
 * @since: 2022/10/05
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_room")
public class Room {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "capacity")
    private Integer capacity;

    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private Collection<EventRoomSchedule> eventRoomSchedules;
}
