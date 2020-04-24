package com.udemy.app.ws.service;

import com.udemy.app.ws.exceptions.UserServiceException;
import com.udemy.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * business logic of our app
 * {@link UserDetailsService} from Spring
 */
public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user) throws UserServiceException;

    UserDto getUser(String email);

    List<UserDto> getUsers(int page, int limit);

    UserDto getUserByUserId(String userId) throws UserServiceException;

    UserDto updateUser(String userId, UserDto user) throws UserServiceException;

    void deleteUser(String userId) throws UserServiceException;

    boolean verifyEmailToken(String token);

    boolean requestPasswordReset(String email);

    boolean resetPassword(String token, String password);
}
