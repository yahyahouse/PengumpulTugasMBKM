package com.fasilkom.pengumpulmbkm.model.users;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "dosen")
public class Dosen implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dosen_id")
    private Integer dosenId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users userId;
}
