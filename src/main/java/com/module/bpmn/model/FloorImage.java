package com.module.bpmn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bpmn_floor")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FloorImage {
    @Id
    @SequenceGenerator(name = "floorimage_sequence", sequenceName = "floorimage_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "floorimage_sequence")
    private Integer id;

    @Column(length = 512, nullable = false)
    private String fileName;
    private String type;

    @Column(name = "upload_date")
    private Date uploadDate;

    private long size;
    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public FloorImage(String fileName, String type, byte[] data, User user, Date date, long size){
        this.fileName = fileName;
        this.type = type;
        this.data = data;
        this.user = user;
        this.uploadDate = date;
        this.size = size;
    }
}
