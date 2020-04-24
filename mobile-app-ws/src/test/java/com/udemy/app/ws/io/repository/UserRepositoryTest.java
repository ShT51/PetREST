package com.udemy.app.ws.io.repository;

import com.udemy.app.ws.io.entity.RoleEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static com.udemy.app.ws.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Disabled
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static boolean recordCreated = false;

    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        if (!recordCreated) {
            roleEntity = roleRepository.save(getRoleEntity());
            createUserEntity();
        }
    }

    @Test
    void testFindAllUsersWithConfirmedEmails() {
        Pageable pageableRequest = PageRequest.of(0, 1);
        Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmails(pageableRequest);
        assertNotNull(page);

        List<UserEntity> userEntities = page.getContent();
        assertNotNull(userEntities);
        assertEquals(1, userEntities.size());
    }

    @Test
    void testFindUserByFirstName() {
        String firstName = "Freddy";
        List<UserEntity> users = userRepository.findUserByFirstName(firstName);
        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    void testFindUserByLastName() {
        String lastName = "Krueger";
        List<UserEntity> users = userRepository.findUserByLastName(lastName);
        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertEquals(lastName, user.getLastName());
    }

    @Test
    void testFindUserByKeyword() {
        String keyword = "rueg";
        List<UserEntity> users = userRepository.findUserByKeyword(keyword);
        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertTrue(user.getLastName().contains(keyword) || user.getFirstName().contains(keyword));
    }

    @Test
    void testFindUserFirstAndLastNamesByKeyword() {
        String keyword = "rueg";
        List<Object[]> users = userRepository.findUserFirstAndLastNamesByKeyword(keyword);
        assertNotNull(users);
        assertEquals(2, users.size());

        Object[] user = users.get(0);
        String userFirstName = String.valueOf(user[0]);
        String userLastName = String.valueOf(user[1]);

        assertNotNull(userFirstName);
        assertNotNull(userLastName);
    }

    @Test
    void testUpdateEmailVerificationStatus() {
        boolean emailVerificationStatus = true;
        userRepository.updateEmailVerificationStatus(emailVerificationStatus, PUBLIC_USER_ID);

        UserEntity storedDetails = userRepository.findByUserId(PUBLIC_USER_ID);
        boolean storedEmailVerificationStatus = storedDetails.getEmailVerificationStatus();
        assertEquals(emailVerificationStatus, storedEmailVerificationStatus);
    }

    @Test
    void testFindUserEntityByUserId() {
        UserEntity userEntity = userRepository.findUserEntityByUserId(PUBLIC_USER_ID);
        assertNotNull(userEntity);
        assertEquals(PUBLIC_USER_ID, userEntity.getUserId());
    }

    @Test
    void testGetUserEntityFullNameById() {
        List<Object[]> row = userRepository.getUserEntityFullNameById(PUBLIC_USER_ID);
        assertNotNull(row);
        assertEquals(1, row.size());

        Object[] userDetails = row.get(0);

        String userFirstName = String.valueOf(userDetails[0]);
        String userLastName = String.valueOf(userDetails[1]);

        assertNotNull(userFirstName);
        assertNotNull(userLastName);
    }

    @Test
    void testUpdateUserEntityEmailVerificationStatus() {
        boolean newEmailVerificationStatus = true;
        userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, PUBLIC_USER_ID);

        UserEntity storedDetails = userRepository.findByUserId(PUBLIC_USER_ID);
        boolean storedEmailVerificationStatus = storedDetails.getEmailVerificationStatus();
        assertEquals(newEmailVerificationStatus, storedEmailVerificationStatus);
    }

     void createUserEntity() {
        UserEntity userEntity = getUserEntity();
        userEntity.setRoles(Arrays.asList(roleEntity));
        userRepository.save(userEntity);

        UserEntity userEntity2 = getUserEntity();
        userEntity2.setUserId("$publicUserId2");
        userEntity2.setRoles(Arrays.asList(roleEntity));
        userRepository.save(userEntity2);

        recordCreated = true;
    }
}