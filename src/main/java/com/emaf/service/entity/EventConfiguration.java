package com.emaf.service.entity;

import com.emaf.service.enumeration.EEventConfigurationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * EventConfiguration
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_event_configuration")
public class EventConfiguration implements Serializable {
    @Id
    @Column(name = "id", length = 10, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_limit_period" )
    private Long requestLimitPeriod;

    @Column(name = "checkin_open_time")
    private Long checkinOpenTime;

    @Column(name = "checkout_open_time")
    private Long checkoutOpenTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EEventConfigurationStatus status;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

}