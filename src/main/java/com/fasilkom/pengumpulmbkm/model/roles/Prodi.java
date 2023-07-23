package com.fasilkom.pengumpulmbkm.model.roles;

import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "program_studi")
public class Prodi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prodi_id")
    private Integer prodiId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name",length =20)
    private EProdi name;
}
