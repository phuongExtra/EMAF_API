package com.emaf.service.entity;

import com.emaf.service.enumeration.ECheckinRequired;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.enumeration.EEventType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Event
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
@Table(name = "em_event")
public class Event implements Serializable {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "banner", length = 2084)
    private String banner;

    @Column(name = "short_description", columnDefinition = "text")
    private String shortDescription;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 25, nullable = false)
    private EEventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25, nullable = false)
    private EEventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "checkin_required", length = 25)
    private ECheckinRequired checkinRequired;

    @Column(name = "start_time", length = 12, nullable = false)
    private String startTime;

    @Column(name = "end_time", length = 12, nullable = false)
    private String endTime;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    @Column(name = "created_by", length = 10)
    private String createdBy;

    @Column(name = "updated_by", length = 10)
    private String updatedBy;

    @Column(name = "handled_by", length = 10)
    private String handleBy;

    @Column(name = "note")
    private String note;

    @Column(name = "feedback_link", length = 2084)
    private String feedbackLink;

//    @Column(name = "number_of_participants")
    //    private Integer numberOfParticipants;

    @Column(name = "min_num_of_participant")
    private Integer minNumOfParticipant;

    @Column(name = "max_num_of_participant")
    private Integer maxNumOfParticipant;

    @Column(name = "participant_deviation")
    private Integer participantDeviation;

    @Column(name = "approval_required")
    private boolean approvalRequired;


//    @Column(name = "checkout_required", columnDefinition = "boolean default false")
//    private boolean checkoutRequired;

    @Column(name = "registration_start_time", length = 12)
    private String registrationStartTime;

    @Column(name = "registration_end_time", length = 12)
    private String registerEndTime;

    @Column(name = "staff_reject_reason", columnDefinition = "text")
    private String staffFeedback;

    @Column(name = "manager_reject_reason", columnDefinition = "text")
    private String managerFeedback;

    @Transient
    private String requestedBy;

    @Transient
    private Integer numberOfFavorites;

    @Transient
    private boolean favorited;

    @Transient
    private boolean participationLimit;

    @Transient
    private int numOfParticipationRegis;
    @Transient
    private EventParticipation eventParticipation;

    @Transient
    private String roleInEvent;

    @Column(name = "hash_checkin", length = 36, unique = true)
    private String hashCheckin;

    @Column(name = "hash_checkout", length = 36, unique = true)
    private String hashCheckout;

    @Column(name = "location")
    private String location;

    @Column(name = "speakers")
    private String speakers;

    @Column(name = "master_of_ceremonies")
    private String masterOfCeremonies;

    //    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private Collection<OrganizationCommittee> organizationCommittees;

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Collection<EventParticipation> eventParticipations;

    //    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private Collection<EventEquipment> eventEquipments;

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Collection<Comment> comments;

    //    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private Collection<EventRoomSchedule> eventRoomSchedules;

    //    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private Collection<EventTimeline> eventTimelines;

    //    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private Collection<EventMajor> eventMajors;

//    @ManyToOne
//    @JoinColumn(name = "room_id")
//    private Room room;

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Collection<EventFavorite> eventFavorites;

    //    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Collection<Document> documents;

}
