package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;

@Data
public class TugasAkhirResponse {

    private Integer tugasAkhirId;
    private Users userId;
    private Dosen dosenId;
    private byte[] sertifikat;
    private byte[] lembarPengesahan;
    private byte[] nilai;
    private byte[] laporan;
    private Boolean verifikasi;

    public TugasAkhirResponse(TugasAkhir tugasAkhir) {
        this.tugasAkhirId = tugasAkhir.getTugasAkhirId();
        this.userId = tugasAkhir.getUserId();
        this.dosenId = tugasAkhir.getDosenId();
        this.sertifikat = tugasAkhir.getSertifikat();
        this.lembarPengesahan = tugasAkhir.getLembarPengesahan();
        this.nilai = tugasAkhir.getNilai();
        this.laporan = tugasAkhir.getLaporanTugasAkhir();
        this.verifikasi = tugasAkhir.getVerifikasi();
    }
}
