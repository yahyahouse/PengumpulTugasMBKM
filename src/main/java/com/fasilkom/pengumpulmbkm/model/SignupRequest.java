package com.fasilkom.pengumpulmbkm.model;


import lombok.Data;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 10, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    private Set<String> prodi;

    private Set<String> program;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String namaLengkap;

    @NotBlank
    @Size(min = 10, max = 50)
    private String npm;

}
