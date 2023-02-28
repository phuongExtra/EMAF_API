package com.emaf.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.emaf.service.enumeration.EUserStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * User
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_user")
public class User implements Serializable {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 150)
    private String lastName;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "avatar", length = 2084)
    private String avatar;

    @Column(name = "phone_number", length = 12)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EUserStatus status;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<Notification> notifications;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<OrganizationCommittee> organizationCommittees;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<EventFavorite> eventFavorites;

}

