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

    @Lob
    @Column(name = "sertifikat")
    private byte[] sertifikat;

    @Lob
    @Column(name = "lembar_pengesahan")
    private byte[] lembarPengesahan;

    @Lob
    @Column(name = "nilai")
    private byte[] nilai;

    @Lob
    @Column(name = "laporan_tugas_akhir")
    private byte[] laporanTugasAkhir;

    @Column(name = "verifikasi")
    private Boolean verifikasi;
}
