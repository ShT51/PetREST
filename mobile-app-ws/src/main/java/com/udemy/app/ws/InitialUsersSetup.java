package com.udemy.app.ws;

import com.udemy.app.ws.io.entity.AuthorityEntity;
import com.udemy.app.ws.io.entity.RoleEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import com.udemy.app.ws.io.repository.AuthorityRepository;
import com.udemy.app.ws.io.repository.RoleRepository;
import com.udemy.app.ws.io.repository.UserRepository;
import com.udemy.app.ws.shared.Roles;
import com.udemy.app.ws.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;

@Component
public class InitialUsersSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String ADMIN_EMAIL = "admin@admin.com";

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("Application ready event triggered...");

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        UserEntity storedAdmin = userRepository.findByEmail(ADMIN_EMAIL);
        if (storedAdmin != null || roleAdmin == null) {
            return;
        }
        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Adminov");
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setEmailVerificationStatus(true);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("Admin"));
        adminUser.setRoles(Arrays.asList(roleAdmin));


        userRepository.save(adminUser);
    }


    private AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }
}
