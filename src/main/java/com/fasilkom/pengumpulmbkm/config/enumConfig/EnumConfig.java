package com.fasilkom.pengumpulmbkm.config.enumConfig;



import com.fasilkom.pengumpulmbkm.model.enumeration.*;
import com.fasilkom.pengumpulmbkm.model.roles.*;
import com.fasilkom.pengumpulmbkm.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;



@Configuration
public class EnumConfig {


    private static final Logger LOG = LoggerFactory.getLogger(EnumConfig.class);

    EnumConfig(RoleRepository roleRepository) {
        LOG.info("Test RolesConfig run");
        for(ERole c : ERole.values()) {
            try {
                Roles roles = roleRepository.findByName(c)
                        .orElseThrow(() -> new RuntimeException("Roles not found"));
                LOG.info("Role {} has been found!", roles.getName());
            } catch(RuntimeException rte) {
                LOG.info("Role {} is not found, inserting to DB . . .", c.name());
                Roles roles = new Roles();
                roles.setName(c);
                roleRepository.save(roles);
            }
        }
    }
}