package com.udemy.app.ws.ui.controller;

import com.udemy.app.ws.service.UserService;
import com.udemy.app.ws.shared.dto.UserDto;
import com.udemy.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.udemy.app.ws.TestHelper.PUBLIC_USER_ID;
import static com.udemy.app.ws.TestHelper.getUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userDto = getUserDto();
    }

    @Test
    void testGetUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(PUBLIC_USER_ID);

        assertNotNull(userRest);
        assertEquals(PUBLIC_USER_ID, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
    }

    @Test
    void creatUser() {
    }
}