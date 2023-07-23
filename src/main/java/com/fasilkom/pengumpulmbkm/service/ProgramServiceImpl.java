package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgramServiceImpl implements ProgramService{
    @Autowired
    private ProgramRepository programRepository;
    @Override
    public Program findByProgramid(Integer prgramId) {
        return programRepository.findByProgramId(prgramId);
    }
}
