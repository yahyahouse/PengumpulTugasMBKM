package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class LaporanResponse {

    private Integer laporanId;
    private Integer userId;
    private Integer dosenId;
    private Integer programId;
    private String laporan;
    private Boolean verifikasi;
    private String catatan;
    private Timestamp waktuPengumpulan;
    private Timestamp waktuUpdate;

    public LaporanResponse(Laporan laporan) {
        this.laporanId = laporan.getLaporanId();
        this.userId = laporan.getUserId().getUserId();
        this.dosenId = laporan.getDosenId().getDosenId();
        this.programId = laporan.getProgramId().getProgramId();
        this.laporan = laporan.getLaporan();
        this.verifikasi = laporan.getVerifikasi();
        this.catatan = laporan.getCatatan();
        this.waktuPengumpulan = laporan.getWaktuPengumpulan();
        this.waktuUpdate = laporan.getWaktuUpdate();
    }
}
