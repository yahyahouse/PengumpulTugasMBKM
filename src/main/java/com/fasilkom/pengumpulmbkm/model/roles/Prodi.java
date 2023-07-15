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
    private Integer prodiId;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private EProdi name;
}
