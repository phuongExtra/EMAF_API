package com.emaf.service.model.event;

import lombok.*;

import javax.validation.constraints.Positive;

/**
 * EventEquipmentCreate
 *
 * @author: VuongVT2
 * @since: 2022/10/22
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventEquipmentForm {
    @Positive
    private int quantity;
    private String equipmentId;
    private String borrowTime;
    private String returnTime;
}
