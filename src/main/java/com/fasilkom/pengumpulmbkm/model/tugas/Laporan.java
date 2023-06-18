package com.fasilkom.pengumpulmbkm.model.tugas;

import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "laporan")
public class Laporan implements Serializable {
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

    @Column(name = "laporan",columnDefinition = "TEXT")
    private String laporan;

    @Column(name = "verifikasi")
    private Boolean verifikasi;
}