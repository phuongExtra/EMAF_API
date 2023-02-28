package com.emaf.service.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Notification
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
@Table(name = "em_notification")
public class Notification implements Serializable {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "content")
    private String content;

    @Column(name = "target_url")
    private String targetUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private String type;
}
