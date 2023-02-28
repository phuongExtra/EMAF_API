package com.emaf.service.entity;

/**
 * Major
 *
 * @author: VuongVT2
 * @since: 2022/09/14
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "em_major")
public class Major implements Serializable {
    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "major")
    private Collection<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "major")
    private Collection<EventMajor> eventMajors;
}
