package com.fasilkom.pengumpulmbkm.model.User;


import com.fasilkom.pengumpulmbkm.model.Roles;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "users")
public class Users implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Integer userId;

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

    @Column(name = "program_studi")
    private String programStudi;

    @Column(name = "program_mbkm")
    private String programMBKM;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Roles> roles = new HashSet<>();

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Users() {

    }
}
