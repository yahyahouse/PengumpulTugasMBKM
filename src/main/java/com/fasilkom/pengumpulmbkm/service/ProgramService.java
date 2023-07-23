package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.roles.Program;
import org.springframework.stereotype.Service;

@Service
public interface ProgramService {
    Program findByProgramid(Integer prgramId);
}
