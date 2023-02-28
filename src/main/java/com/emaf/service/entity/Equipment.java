package com.emaf.service.entity;

import com.emaf.service.enumeration.EEquipmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Equipment
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
@Table(name = "em_equipment")
public class Equipment implements Serializable {
    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "equipment_name")
    private String equipmentName;

    @Column(name = "image", length = 2048)
    private String image;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100, nullable = false)
    private EEquipmentStatus status;

    @Column(name = "created_by", length = 10)
    private String createdBy;

    @Column(name = "updated_by", length = 10)
    private String updatedBy;

    @Column(name = "quantity")
    private int quantity;

    @Transient
    private int quantityAvailable;

//    @Column(name = "created_at", length = 14, nullable = false)
//    private String createdAt;
//
//    @Column(name = "updated_at", length = 14, nullable = false)
//    private String updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Collection<EventEquipment> eventEquipments;


}
