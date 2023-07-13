package com.fasilkom.pengumpulmbkm.model.roles;

import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "program_mbkm")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleProgramId;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private EProgram name;
}
