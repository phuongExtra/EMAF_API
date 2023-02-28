package com.emaf.service.entity;

import com.emaf.service.enumeration.EOrganizationCommitteeStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * EventUser
 *
 * @author: VuongVT2
 * @since: 2022/10/06
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "em_organization_committee")
public class OrganizationCommittee implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EOrganizationCommitteeStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private String eventName;

    @Transient
    private String organizer;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Transient
    private String eventId;

}
