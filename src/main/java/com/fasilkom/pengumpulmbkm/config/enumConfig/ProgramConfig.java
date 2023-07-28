package com.fasilkom.pengumpulmbkm.config.enumConfig;


import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.repository.ProgramRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProgramConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ProgramConfig.class);
    ProgramConfig(ProgramRepository programRepository) {
        LOG.info("Test Program MBKM run");
        for (EProgram o: EProgram.values()) {
            try {
                Program program = programRepository.findByName(o)
                        .orElseThrow(() -> new RuntimeException("Roles not found"));
                LOG.info("Program MBKM {} has been found!", program.getName());
            } catch(RuntimeException rte) {
                LOG.info("Program MBKM {} is not found, inserting to DB . . .", o.name());
                Program program = new Program();
                program.setName(o);
                programRepository.save(program);
            }
        }
    }
}