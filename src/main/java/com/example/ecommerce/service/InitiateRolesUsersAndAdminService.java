
package com.example.ecommerce.service;

import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.entity.EcommRole;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.ecommerce.util.EcommConstants.*;

/** This class initiates roles and users and admin in this ecommerce. */
@Service
public class InitiateRolesUsersAndAdminService {

    private RoleRepository roleRepository;

    private EncryptionService encryptionService;

    private EcommUserRepository ecommUserRepository;

    /**
     * InitiateRolesUsersAndAdminService Constructor.
     * @param roleRepository The RoleRepository object.
     * @param encryptionService The EncryptionService object.
     */
    public InitiateRolesUsersAndAdminService(RoleRepository roleRepository, EncryptionService encryptionService,
                                             EcommUserRepository ecommUserRepository) {
        this.roleRepository = roleRepository;
        this.encryptionService = encryptionService;
        this.ecommUserRepository = ecommUserRepository;
    }

    /**
     * This method initiates the roles of USER and ADMIN.
     * Also initiates two users: 'ecommUser1' 'ecommUser2' and one admin: admin1.
     */
    @Transactional
    public void initiateRolesUsersAndAdmin() {

        // Initiates role ADMIN and role USER if they aren't exist.

        if (roleRepository.findByRoleName(ROLE_ADMIN).isEmpty()) {
            EcommRole adminRole = new EcommRole();
            adminRole.setId(ROLE_ADMIN_ID);
            adminRole.setRoleName(ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByRoleName(ROLE_USER).isEmpty()) {
            EcommRole userRole = new EcommRole();
            userRole.setId(ROLE_USER_ID);
            userRole.setRoleName(ROLE_USER);
            roleRepository.save(userRole);
        }

        // Initiates the users ecommUser1, ecommUser2 and the admin1 if they aren't exist.

        Optional<EcommUser> opUser = ecommUserRepository.findByUsernameIgnoreCase("admin1");
        if (opUser.isEmpty()) {
            Optional<EcommRole> opRole = roleRepository.findByRoleName(ROLE_ADMIN);
            if (opRole.isPresent()) {
                EcommRole adminRole = opRole.get();
                EcommUser admin = new EcommUser();
                admin.getRoles().add(adminRole);
                admin.setUsername("admin1");
                admin.setPassword(encryptionService.encryptPassword("Admin1$123"));
                admin.setEmail("admin1@ecommerce.com");
                admin.setFirstName("admin1_first_name");
                admin.setLastName("admin1_last_name");
                admin.setVerifiedEmail(true);
                ecommUserRepository.save(admin);
            }
        }

        opUser = ecommUserRepository.findByUsernameIgnoreCase("ecommUser1");
        if (opUser.isEmpty()) {
            Optional<EcommRole> opRole = roleRepository.findByRoleName(ROLE_USER);
            if (opRole.isPresent()) {
                EcommRole userRole = opRole.get();
                EcommUser user1 = new EcommUser();
                user1.getRoles().add(userRole);
                user1.setUsername("ecommUser1");
                user1.setPassword(encryptionService.encryptPassword("Password1$123"));
                user1.setEmail("user1@ecommuser.com");
                user1.setFirstName("user1_first_name");
                user1.setLastName("user1_last_name");
                user1.setVerifiedEmail(true);
                ecommUserRepository.save(user1);
            }
        }

        opUser = ecommUserRepository.findByUsernameIgnoreCase("ecommUser2");
        if (opUser.isEmpty()) {
            Optional<EcommRole> opRole = roleRepository.findByRoleName(ROLE_USER);
            if (opRole.isPresent()) {
                EcommRole userRole = opRole.get();
                EcommUser user2 = new EcommUser();
                user2.getRoles().add(userRole);
                user2.setUsername("ecommUser2");
                user2.setPassword(encryptionService.encryptPassword("Password2$123"));
                user2.setEmail("user2@ecommuser.com");
                user2.setFirstName("user2_first_name");
                user2.setLastName("user2_last_name");
                user2.setVerifiedEmail(false);
                ecommUserRepository.save(user2);
            }
        }
    }
}
