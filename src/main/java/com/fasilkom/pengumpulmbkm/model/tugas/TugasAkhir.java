package com.fasilkom.pengumpulmbkm.model.tugas;


import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity(name = "tugas_akhir")
public class TugasAkhir implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tugas_akhir_id")
    private Integer tugasAkhirId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne
    @NotBlank(message = "Dosen is required")
    @JoinColumn(name = "dosen_id")
    private Dosen dosenId;

    @Lob
    @NotBlank(message = "Sertifikat is required")
    @Column(name = "sertifikat")
    private byte[] sertifikat;

    @Lob
    @NotBlank(message = "Lembar Pengesahan is required")
    @Column(name = "lembar_pengesahan")
    private byte[] lembarPengesahan;

    @Lob
    @NotBlank(message = "Nilai is required")
    @Column(name = "nilai")
    private byte[] nilai;

    @Lob
    @NotBlank(message = "Tugas Akhir is required")
    @Column(name = "laporan_tugas_akhir")
    private byte[] laporanTugasAkhir;

    @Column(name = "verifikasi")
    private Boolean verifikasi;

    @Column(name = "catatan")
    private String catatan;

    @Column(name = "waktu_pengumpulan")
    private Timestamp waktuPengumpulan;

    @Column(name = "waktu_update")
    private Timestamp waktuUpdate;
}
