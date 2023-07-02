package com.fasilkom.pengumpulmbkm.model.response;

import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import java.util.Set;

@Data
public class ProfileResponse {
    private Integer userId;
    private String namaLengkap;
    private String email;
    private String npm;
    private Set<Roles> role;

    private Set<Prodi> prodi;

    private Set<Program> program;
    private String noHp;
    public ProfileResponse(Users users) {
        this.userId = users.getUserId();
        this.namaLengkap = users.getNamaLengkap();
        this.email = users.getEmail();
        this.npm = users.getNpm();
        this.noHp = users.getNoHp();
        this.role = users.getRoles();
        this.prodi = users.getProgramStudi();
        this.program = users.getProgramMBKM();

    }
}
