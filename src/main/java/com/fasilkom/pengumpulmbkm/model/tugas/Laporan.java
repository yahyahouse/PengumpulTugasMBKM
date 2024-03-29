package com.fasilkom.pengumpulmbkm.model.tugas;

import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity(name = "laporan")
public class Laporan implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "laporan_id")
    private Integer laporanId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne
    @JoinColumn(name = "dosen_id")
    private Dosen dosenId;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program programId;

    @Column(name = "laporan")
    private String laporan;

    @Column(name = "verifikasi")
    private Boolean verifikasi;

    @Column(name = "catatan")
    private String catatan;

    @Column(name = "waktu_pengumpulan")
    private Timestamp waktuPengumpulan;

    @Column(name = "waktu_update")
    private Timestamp waktuUpdate;
}
