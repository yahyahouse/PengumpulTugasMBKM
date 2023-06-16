package com.fasilkom.pengumpulmbkm.model;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "dosen")
public class Dosen implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "dosen_id")
    private Integer dosenId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users userId;
}
