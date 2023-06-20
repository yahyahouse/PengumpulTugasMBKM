package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

@Data
public class LaporanResponse {

    private Integer laporanId;
    private Users userId;
    private Dosen dosenId;
    private String laporan;
    private Boolean verifikasi;

    public LaporanResponse(Laporan laporan) {
        this.userId = laporan.getUserId();
        this.dosenId = laporan.getDosenId();
        this.laporan = laporan.getLaporan();
        this.verifikasi = laporan.getVerifikasi();
    }
}
