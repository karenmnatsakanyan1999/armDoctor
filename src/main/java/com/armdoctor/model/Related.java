package com.armdoctor.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "related")
@Data
public class Related implements Serializable {
@Id
private Integer user_id;
@Id
private Integer hospital_id;
}
