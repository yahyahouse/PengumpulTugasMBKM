package com.fasilkom.pengumpulmbkm.model.tugas;


import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "tugas_akhir")
public class TugasAkhir implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "tugas_akhir_id")
    private Integer tugasAkhirId;

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

    @Column(name = "laporan_tugas_akhir")
    private String laporanTugasAkhir;

    @Column(name = "verifikasi")
    private Boolean verifikasi;
}
