package com.fasilkom.pengumpulmbkm.model.Tugas;


import com.fasilkom.pengumpulmbkm.model.User.Dosen;
import com.fasilkom.pengumpulmbkm.model.User.Users;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "tugas_akhir")
public class TugasAkhir implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "laporan_id")
    private Integer laporanId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne
    @JoinColumn(name = "dosen_id")
    private Dosen dosenId;

    @Column(name = "sertifikat")
    private String sertifikat;

    @Column(name = "lembar_pengesahan")
    private String lembarPengesahan;

    @Column(name = "nilai")
    private String nilai;

    @Column(name = "laporan")
    private String laporan;

    @Column(name = "verifikasi")
    private Integer verifikasi;
}
