package com.emaf.service.model;

import com.emaf.service.enumeration.EEquipmentStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * EquipmentForm
 *
 * @author: VuongVT2
 * @since: 2022/11/25
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentForm {
    private String equipmentId;
    @NotBlank
    private String equipmentName;
    private String note;
    @Positive
    private int quantity;
    private String status;
}
