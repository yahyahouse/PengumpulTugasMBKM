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
    @Column(name = "program_id")
    private Integer programId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name",length =20)
    private EProgram name;
}
