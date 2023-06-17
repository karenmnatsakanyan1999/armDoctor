package com.armdoctor.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "hospital")
public class HospitalEntity {
    @Id
    @Column(name = "hospital_id")
    private Integer id;
    private String name;
    private String address;
    @ManyToMany(mappedBy = "hospitalEntities")
    private Set<DoctorEntity> doctorEntity;
}
