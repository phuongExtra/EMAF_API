package com.emaf.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Document
 *
 * @author: VuongVT2
 * @since: 2022/11/06
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_document")
public class Document implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "target_url")
    private String targetUrl;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
