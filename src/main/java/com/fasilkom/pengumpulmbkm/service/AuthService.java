package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.JwtResponse;
import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;

import java.util.Map;

public interface AuthService {
    JwtResponse authenticateUser(Map<String, Object> loginRequest);
    MessageResponse registerUser(SignupRequest signupRequest);
}
