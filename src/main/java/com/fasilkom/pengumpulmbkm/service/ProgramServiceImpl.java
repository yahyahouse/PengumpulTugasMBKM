package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService{

    private final ProgramRepository programRepository;
    @Override
    public Program findByProgramid(Integer prgramId) {
        return programRepository.findByProgramId(prgramId);
    }
}
