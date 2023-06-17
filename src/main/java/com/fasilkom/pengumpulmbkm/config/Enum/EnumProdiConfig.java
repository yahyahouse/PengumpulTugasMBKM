package com.fasilkom.pengumpulmbkm.config.Enum;


import com.fasilkom.pengumpulmbkm.model.Enum.*;
import com.fasilkom.pengumpulmbkm.model.Roles.*;
import com.fasilkom.pengumpulmbkm.repository.ProdiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnumProdiConfig {

    private static final Logger LOG = LoggerFactory.getLogger(EnumProdiConfig.class);

    EnumProdiConfig(ProdiRepository prodiRepository) {

        LOG.info("Test Role Prodi run");
        for (EProdi p: EProdi.values()) {
            try {
                Prodi rolesProdi = prodiRepository.findByName(p)
                        .orElseThrow(() -> new RuntimeException("Roles not found"));
                LOG.info("Role {} has been found!", rolesProdi.getName());
            } catch(RuntimeException rte) {
                LOG.info("Role {} is not found, inserting to DB . . .", p.name());
                Prodi rolesProdi = new Prodi();
                rolesProdi.setName(p);
                prodiRepository.save(rolesProdi);
            }
        }

    }

}