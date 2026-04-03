package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.LaporanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasilkom.pengumpulmbkm.util.CommonConstant.*;

@Service
@RequiredArgsConstructor
public class LaporanServiceImpl implements LaporanService {

    private final LaporanRepository laporanRepository;
    private final DosenService dosenService;
    private final ProgramService programService;

    @Override
    public Laporan findByLaporanId(Integer laporanId) {
        return laporanRepository.findByLaporanId(laporanId);
    }

    @Override
    public void saveLaporan(Laporan laporan) {
        laporanRepository.save(laporan);
    }

    @Override
    public List<Laporan> findLaporanByUserId(String userId) {
        return laporanRepository.getLaporanByUserId(userId);
    }

    @Override
    public List<Laporan> findLaporanByDosenId(String dosenId) {
        return laporanRepository.getLaporanByDosenId(dosenId);
    }

    @Override
    public List<Laporan> getAllLaporan() {
        return laporanRepository.getAllLaporan();
    }

    @Override
    public void updateLaporan(Laporan laporan) {
        laporanRepository.save(laporan);
    }

    @Override
    public Laporan uploadLaporan(String dosenId, String laporanMBKM, Integer programId, Users user) {
        Laporan laporan = new Laporan();
        Dosen dosen = dosenService.getDosenByDosenId(dosenId);
        if (dosen == null) {
            throw new IllegalArgumentException(DOSEN_NOT_FOUND);
        }
        Program program = programService.findByProgramid(programId);
        if (program == null) {
            throw new IllegalArgumentException(PROGRAM_MBKM_NOT_FOUND);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        laporan.setUserId(user);
        laporan.setDosenId(dosen);
        laporan.setProgramId(program);
        laporan.setLaporan(laporanMBKM);
        laporan.setVerifikasi(null);
        laporan.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
        return laporanRepository.save(laporan);
    }

    @Override
    public Laporan updateLaporanBusinessLogic(Integer laporanId, String laporan, Integer programId, Users user) {
        LocalDateTime currentTime = LocalDateTime.now();
        Laporan laporanSave = findByLaporanId(laporanId);
        if (laporanSave == null) {
            throw new IllegalArgumentException("Laporan Not Found");
        }
        Program program = programService.findByProgramid(programId);
        if (program == null) {
            throw new IllegalArgumentException(PROGRAM_MBKM_NOT_FOUND);
        }
        if (laporanSave.getUserId().getUserId().equals(user.getUserId())) {
            if (programId != null) {
                laporanSave.setProgramId(program);
            }
            laporanSave.setLaporan(laporan);
            laporanSave.setWaktuUpdate(Timestamp.valueOf(currentTime));
            return laporanRepository.save(laporanSave);
        } else {
            throw new org.springframework.security.access.AccessDeniedException(AKSES_DITOLAK);
        }
    }
}
