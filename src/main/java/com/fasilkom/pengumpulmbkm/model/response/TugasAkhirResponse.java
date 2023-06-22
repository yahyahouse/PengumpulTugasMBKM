package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Data
public class TugasAkhirResponse {

    private Integer tugasAkhirId;
    private Integer userId;
    private Integer dosenId;
    private byte[] sertifikat;
    private byte[] lembarPengesahan;
    private byte[] nilai;
    private byte[] laporan;
    private Boolean verifikasi;
    private Timestamp waktuUpload;

    public TugasAkhirResponse(TugasAkhir tugasAkhir) {
        this.tugasAkhirId = tugasAkhir.getTugasAkhirId();
        this.userId = tugasAkhir.getUserId().getUserId();
        this.dosenId = tugasAkhir.getDosenId().getDosenId();
        this.sertifikat = tugasAkhir.getSertifikat();
        this.lembarPengesahan = tugasAkhir.getLembarPengesahan();
        this.nilai = tugasAkhir.getNilai();
        this.laporan = tugasAkhir.getLaporanTugasAkhir();
        this.verifikasi = tugasAkhir.getVerifikasi();
        this.waktuUpload = tugasAkhir.getWaktuPengumpulan();
    }
}
