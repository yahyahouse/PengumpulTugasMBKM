package com.fasilkom.pengumpulmbkm.model.Roles;

import com.fasilkom.pengumpulmbkm.model.Enum.EProdi;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "rolesProdi")
public class Prodi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleProdiId;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private EProdi name;
}
