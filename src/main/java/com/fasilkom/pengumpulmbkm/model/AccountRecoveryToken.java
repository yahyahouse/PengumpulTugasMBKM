package com.fasilkom.pengumpulmbkm.model;


import com.fasilkom.pengumpulmbkm.model.users.Users;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class AccountRecoveryToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private String email;

    private String token;

    private LocalDateTime expirationDate;

    public AccountRecoveryToken() {
    }
}
