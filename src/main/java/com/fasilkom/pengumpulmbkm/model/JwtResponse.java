package com.fasilkom.pengumpulmbkm.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String userId;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, String userId, String username, String email, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
