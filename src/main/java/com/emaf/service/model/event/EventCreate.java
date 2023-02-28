package com.emaf.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * EventCreate
 *
 * @author: VuongVT2
 * @since: 2022/10/11
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventCreate {
    private String eventId;

    @NotBlank
    private String eventName;

    @NotBlank
    private String shortDescription;

    private String description;

    @NotBlank
    @Length(max = 25)
    private String type;

    @NotBlank
    @Length(max = 14)
    private String startTime;

    @NotBlank
    @Length(max = 14)
    private String endTime;

    @NotBlank
    private String location;
    private String note;
    private String feedbackLink;

    @Length(max = 14)
    private String registerStartTime;
    @Length(max = 14)
    private String registerEndTime;
//    private boolean checkoutRequired;
    private boolean approvalRequired;
    private List<Long> roomIds;
    private String speakers;
    private String masterOfCeremonies;
    private String checkinRequired;
//    private Integer numberOfParticipants;
    private Integer minNumOfParticipant;
    private Integer maxNumOfParticipant;
    private Integer participantDeviation;
    private List<EventUserForm> eventUsers;
    private List<EventEquipmentForm> eventEquipments;
    private List<EventTimelineCreate> eventTimelines;
}
