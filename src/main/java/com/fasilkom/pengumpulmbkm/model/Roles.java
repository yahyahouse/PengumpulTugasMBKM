package com.fasilkom.pengumpulmbkm.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private ERole name;
}
