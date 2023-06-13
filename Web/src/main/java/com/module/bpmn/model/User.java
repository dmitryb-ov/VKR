package com.module.bpmn.model;

import com.module.bpmn.utils.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "bpmn_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private Integer id;
    private String firstName;
    private String secondName;
    private String username;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user")
    private Set<Settings> settings;
    @OneToMany(mappedBy = "user")
    private Set<Schemes> schemes;

    @Enumerated(value = EnumType.STRING)
    private Role role;
}
