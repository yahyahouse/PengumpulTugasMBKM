package com.fasilkom.pengumpulmbkm.model.users;


import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "users")
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "nama_lengkap")
    private String namaLengkap;

    @Column(name = "NPM")
    private String npm;

    @Column(name = "email")
    private String email;

    @Column(name = "no_hp")
    private String noHp;

    @ManyToMany(fetch = FetchType.EAGER)
    @Column(name = "program_studi")
    private transient Set<Prodi> programStudi = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Column(name = "program_mbkm")
    private transient Set<Program> programMBKM = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private transient Set<Roles> roles = new HashSet<>();

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Users() {
    }

    public Users(SignupRequest request) {
    }
}
