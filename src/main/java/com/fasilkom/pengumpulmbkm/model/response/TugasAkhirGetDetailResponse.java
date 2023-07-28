package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TugasAkhirGetDetailResponse {

    private Integer tugasAkhirId;
    private Integer userId;
    private Integer dosenId;
    private Integer programId;
    private byte[] sertifikat;
    private byte[] lembarPengesahan;
    private byte[] nilai;
    private byte[] laporan;
    private Boolean verifikasi;
    private String catatan;
    private Timestamp waktuPengumpulan;
    private Timestamp waktuUpdate;

    public TugasAkhirGetDetailResponse(TugasAkhir tugasAkhir) {
        this.tugasAkhirId = tugasAkhir.getTugasAkhirId();
        this.userId = tugasAkhir.getUserId().getUserId();
        this.dosenId = tugasAkhir.getDosenId().getDosenId();
        this.programId = tugasAkhir.getProgramId().getProgramId();
        this.sertifikat = tugasAkhir.getSertifikat();
        this.lembarPengesahan = tugasAkhir.getLembarPengesahan();
        this.nilai = tugasAkhir.getNilai();
        this.laporan = tugasAkhir.getLaporanTugasAkhir();
        this.verifikasi = tugasAkhir.getVerifikasi();
        this.catatan = tugasAkhir.getCatatan();
        this.waktuPengumpulan = tugasAkhir.getWaktuPengumpulan();
        this.waktuUpdate = tugasAkhir.getWaktuUpdate();
    }
}
