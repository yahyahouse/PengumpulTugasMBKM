package com.fasilkom.pengumpulmbkm.model.roles;

import javax.persistence.*;

import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
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
