package com.module.bpmn.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "bpmn_user_settings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Settings {

    @Id
    @SequenceGenerator(name = "setting_sequence", sequenceName = "setting_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "setting_sequence")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    @Column(name = "key")
    private String settingsKey;
    @Column(name = "value")
    private String settingsValue;
}
