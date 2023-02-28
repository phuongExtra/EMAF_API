package com.emaf.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * EventTimeline
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
@Table(name = "em_event_timeline")
public class EventTimeline implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "note")
    private String note;

    @Column(name = "activity")
    private String activity;

    @Column(name = "start_time", length = 12, nullable = false)
    private String startTime;

    @Column(name = "end_time", length = 12, nullable = false)
    private String endTime;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;


}
