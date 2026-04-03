package com.fasilkom.pengumpulmbkm.model.users;


import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "dosen")
public class Dosen implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dosen_id")
    private String dosenId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users userId;
}
