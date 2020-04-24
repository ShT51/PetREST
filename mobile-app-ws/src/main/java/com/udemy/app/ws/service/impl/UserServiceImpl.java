package com.udemy.app.ws.service.impl;

import com.udemy.app.ws.exceptions.AppExceptionsHandler;
import com.udemy.app.ws.exceptions.UserServiceException;
import com.udemy.app.ws.io.entity.PasswordResetTokenEntity;
import com.udemy.app.ws.io.entity.RoleEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import com.udemy.app.ws.io.repository.PasswordResetTokenRepository;
import com.udemy.app.ws.io.repository.RoleRepository;
import com.udemy.app.ws.io.repository.UserRepository;
import com.udemy.app.ws.security.AuthenticationFilter;
import com.udemy.app.ws.security.UserPrincipals;
import com.udemy.app.ws.service.UserService;
import com.udemy.app.ws.shared.AmazonSES;
import com.udemy.app.ws.shared.Utils;
import com.udemy.app.ws.shared.dto.AddressDto;
import com.udemy.app.ws.shared.dto.UserDto;
import com.udemy.app.ws.ui.controller.UserController;
import com.udemy.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private AmazonSES amazonSES;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Create a new User and save in DB
     * Step_1. Check this user in DB, if User is already exists - throw {@link UserServiceException}
     * Step_2. Generate public ID for each address (to use it in a Response body).
     *         Set publicId and userDetails for each address and store it back to {@link UserDto}.
     * Step_3. Map {@link UserDto} to {@link UserEntity} object using {@link ModelMapper}
     * Step_4. Generate User's public ID (to use it in a Response body) and set it to User
     * Step_5. Encrypt the User provided password (to store it in DB) and set it to User
     * Step_6. Generate Email Verification Token and Email Verification Status add set them to User
     * Step_7. Get from UserDto User's Roles {@link RoleEntity} add set them to User
     * Step_8. Save User {@link UserEntity} to the DB using {@method save} {@link UserRepository}
     * Step_9. Map {@link UserEntity} to {@link UserDto} object using {@link ModelMapper}
     * Step_10. Send an Email to User to verify their email address throw {@link AmazonSES} service.
     *
     * @param user {@link UserDto} object from {@link UserController}
     * @return {@link UserDto} object back to {@link UserController}
     * @throws UserServiceException - exception specific for UserService, see also {@link AppExceptionsHandler}
     */
    @Override
    public UserDto createUser(UserDto user) {

        // Step_1
        if (userRepository.findByEmail(user.getEmail()) != null) throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.name());

        // Step_2
        if (user.getAddresses() != null) {
            for (int i = 0; i < user.getAddresses().size(); i++) {
                AddressDto address = user.getAddresses().get(i);
                address.setUserDetails(user);
                address.setAddressId(utils.generateAddressId(30));
                user.getAddresses().set(i, address);
            }
        }
        // Step_3
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        // Step_4.
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        // Step_5.
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // Step_6.
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);
        // Step_7.
        Collection<RoleEntity> roleEntities = new HashSet<>();
        for(String role: user.getRoles()) {
            RoleEntity roleEntity = roleRepository.findByName(role);
            if (roleEntity != null) {
                roleEntities.add(roleEntity);
            }
        }
        userEntity.setRoles(roleEntities);
        // Step_8.
        UserEntity storedUserDetails = userRepository.save(userEntity);
        // Step_9.
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
        // Step_10.
        amazonSES.verifyEmail(returnValue);

        return returnValue;
    }

    /**
     * Get User by ID from DB.
     * Check for null and transfer it to UserDto object
     *
     * @param userId - User public ID
     * @return UserDto object
     * @throws UserServiceException if this User was not found
     */
    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    /**
     * UPDATE User's First Name and Last Name.
     *
     * @param userId - User's public ID
     * @param user UserDto object from Controller layer
     * @return UserDto object to Controller layer
     * @throws UserServiceException if this User was not found
     */
    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnValue = new UserDto();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        // it is possible to add additional checks here (null, empty string and etc)
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    /**
     * DELETE User by ID
     *
     * @param userId - User's public ID
     * @throws UserServiceException if this User was not found
     */
    @Override
    public void deleteUser(String userId){
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.delete(userEntity);
    }

    /**
     * Return pages with Users
     *
     * @return list of {@link UserDto}
     */
    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        // pages from query will start from 0 instead of 1
        if (page > 0) page = page - 1;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }
        return returnValue;
    }

    /**
     * We use this method in {@link AuthenticationFilter} to get the User's public ID
     *
     * @param email
     * @return {@link UserDto}
     */
    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    /**
     * This method comes from {@link UserDetailsService} and it will be used in SignIn process.
     * Get the {@link UserEntity} from DB, check it for null, get his email, password and list of granted authorities.
     * Create new {@link UserPrincipals} object with email, password, email verification status and granted authorities.
     *
     * @param email - User's Email
     * @return {@link User} object. Which will be used in {@link UserDetailsService}
     * @throws UsernameNotFoundException if User with this {@param email} was not found
     */

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipals(userEntity);
    }

    /**
     * Check the email verification token (credentials and expiration time).
     * If token will be verified successful - User's email verification status will be set to true,
     * email verification token will be deleted from DB.
     *
     * @param token
     * @return true if verification was successful, false if it wasn't
     */
    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            // check token's expiration date
            boolean hastokenExpired = Utils.hasTokenExpired(token);

            if (!hastokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(true);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }
        return returnValue;
    }

    /**
     * Send User email with Password Reset Token
     * throw {@link AmazonSES} service.
     *
     * @param email
     * @return true if Email was send successful
     */
    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return returnValue;
        }

        String token = utils.generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnValue = new AmazonSES().sendPasswordResetRequest(
                userEntity.getFirstName(),
                userEntity.getEmail(),
                token);

        return returnValue;
    }

    /**
     * Set to User a new Password and remove password reset token from DB
     *
     * Password reset {@param token} and {@param password}
     * come from {@link UserController} {@method resetPassword} as a Request parameters
     *
     * @return true if {@param token} is valid and this User is store in DB
     */
    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        if( Utils.hasTokenExpired(token) ) {
            return returnValue;
        }
        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            return returnValue;
        }
        // generate new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        // update User password in DB
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        // verify if password was saved successfully
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }
        // remove Password Reset Token from DB
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }
}
