package com.udemy.app.ws.service.impl;

import com.udemy.app.ws.TestHelper;
import com.udemy.app.ws.exceptions.UserServiceException;
import com.udemy.app.ws.io.entity.RoleEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import com.udemy.app.ws.io.repository.RoleRepository;
import com.udemy.app.ws.io.repository.UserRepository;
import com.udemy.app.ws.shared.AmazonSES;
import com.udemy.app.ws.shared.Utils;
import com.udemy.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.udemy.app.ws.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    RoleRepository roleRepository;

    @Mock
    AmazonSES amazonSES;

    private UserEntity userEntity;
    private UserDto userDto;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        roleEntity = TestHelper.getRoleEntity();
        userEntity = TestHelper.getUserEntity();
        userDto = TestHelper.getUserDto();
    }

    @Test
    void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser(USER_EMAIL);

        assertNotNull(userDto);
        assertEquals(FIRST_NAME, userDto.getFirstName());
        assertEquals(LAST_NAME, userDto.getLastName());
        assertEquals(ENCRYPTED_PASSWORD, userDto.getEncryptedPassword());
        assertEquals(PUBLIC_USER_ID, userDto.getUserId());
    }

    @Test
    void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser(USER_EMAIL);
                });
    }

    @Test
    void testCreateUser_UserServiceException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        assertThrows(UserServiceException.class,
                () -> {
                    userService.createUser(userDto);
                });
    }

    @Test
    void testCreatUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn(PUBLIC_ADDRESS_ID);
        when(utils.generateUserId(anyInt())).thenReturn(PUBLIC_USER_ID);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(roleRepository.findByName(anyString())).thenReturn(roleEntity);
        doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertEquals(userEntity.getEncryptedPassword(), storedUserDetails.getEncryptedPassword());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        assertEquals(userEntity.getRoles().size(), storedUserDetails.getRoles().size());
        verify(utils, times(2)).generateAddressId(30);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}