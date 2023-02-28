package com.emaf.service.entity;

import com.emaf.service.enumeration.EEventParticipationStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * EventParticipation
 *
 * @author: VuongVT2
 * @since: 2022/09/14
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_event_participation")
public class EventParticipation implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checkin_time", length = 14)
    private String checkinTime;

    @Column(name = "checkout_time", length = 14)
    private String checkoutTime;

    @Transient
    private String eventName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25, nullable = false)
    private EEventParticipationStatus status;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Transient
    private String eventId;


}
