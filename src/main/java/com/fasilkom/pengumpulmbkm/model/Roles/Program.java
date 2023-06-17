package com.fasilkom.pengumpulmbkm.model.Roles;

import com.fasilkom.pengumpulmbkm.model.Enum.EProgram;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "rolesProgram")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleProgramId;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private EProgram name;
}
