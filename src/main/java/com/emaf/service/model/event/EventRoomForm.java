package com.emaf.service.model.event;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * EventRoomForm
 *
 * @author: VuongVT2
 * @since: 2022/12/07
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventRoomForm {
    @NotBlank
    private String eventId;
    private List<Long> roomIds;
}
