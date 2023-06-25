package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import lombok.Data;

import java.util.Set;

@Data
public class DosenResponse {

    private Integer dosenId;
    private Integer userId;
    private String namaLengkap;
    private String email;
    private Set<Roles> role;

    private Set<Prodi> prodi;

    private Set<Program> program;
    private String noHp;
    public DosenResponse(Dosen dosen) {
        this.dosenId = dosen.getDosenId();
        this.userId = dosen.getUserId().getUserId();
        this.namaLengkap = dosen.getUserId().getNamaLengkap();
        this.email = dosen.getUserId().getEmail();
        this.role = dosen.getUserId().getRoles();
        this.prodi = dosen.getUserId().getProgramStudi();
        this.program = dosen.getUserId().getProgramMBKM();
        this.noHp = dosen.getUserId().getNoHp();
    }
}
