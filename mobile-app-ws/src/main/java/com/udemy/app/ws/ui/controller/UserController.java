package com.udemy.app.ws.ui.controller;

import com.udemy.app.ws.exceptions.UserServiceException;
import com.udemy.app.ws.service.AddressService;
import com.udemy.app.ws.service.UserService;
import com.udemy.app.ws.shared.Roles;
import com.udemy.app.ws.shared.dto.AddressDto;
import com.udemy.app.ws.shared.dto.UserDto;
import com.udemy.app.ws.ui.model.request.PasswordResetModel;
import com.udemy.app.ws.ui.model.request.PasswordResetRequestModel;
import com.udemy.app.ws.ui.model.request.UserDetailsRequestModel;
import com.udemy.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    private AddressService addressesService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAddressesService(AddressService addressesService) {
        this.addressesService = addressesService;
    }


    /**
     * GET User by ID.
     * This method allowed only for User itself or Admin User
     *
     * @param id - public userID
     * @return {@link UserRest} class object that will be converted from POJO to Response with JSON/XML format payload
     */
    @PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.userId == principal.userId")
    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest getUser(@PathVariable String id) {
        ModelMapper modelMapper = new ModelMapper();

        UserDto userDto = userService.getUserByUserId(id);
        return modelMapper.map(userDto, UserRest.class);
    }


    /**
     * GET list of pages with Users
     *
     * @return list of {@link UserRest}
     */
    @PostAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            ModelMapper modelMapper = new ModelMapper();
            UserRest userModel = modelMapper.map(userDto, UserRest.class);
            returnValue.add(userModel);
        }
        return returnValue;
    }

    /**
     * Create User
     * {@link UserDto} - DTO object to transfer User thought different layers
     *
     * @param userDetails - {@link UserDetailsRequestModel} class to convert Request's payload from JSON/XML format to POJO
     * @return {@link UserRest} class object
     */
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest creatUser(@RequestBody UserDetailsRequestModel userDetails) {

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        UserRest returnValue;
        // get UserDto from UserDetails using modelMapper
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        // assign to all new users ROLE_USER
        userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));
        // we implement the business logic (crypt the password and id, e.t.c)
        UserDto createdUser = userService.createUser(userDto);
        // map the "created" User to our "response" User
        returnValue = modelMapper.map(createdUser, UserRest.class);
        return returnValue;
    }

    /**
     * UPDATE User details by ID (Firs and Last Names)
     * Password and Email will not update. To changed Password see {@method requestReset} and {@method resetPassword}
     *
     * @param id          - public userID
     * @param userDetails - {@link UserDetailsRequestModel}
     * @return {@link UserRest} object
     */
    @PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.userId == principal.userId")
    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        ModelMapper modelMapper = new ModelMapper();

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto updatedUser = userService.updateUser(id, userDto);
        return modelMapper.map(updatedUser, UserRest.class);
    }

    /**
     * Delete User by public ID.
     * This method allowed only for User itself or Admin User
     *
     * @param id - public userID
     * @return {@link OperationStatusModel} that shows operation status {@link RequestOperationStatus}
     * and operation name {@link RequestOperationName}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.userId")
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/[user public ID]/addresses

    /**
     * Return a list of addresses of specific user with HEATOS links in HAL format
     *
     * @param id - public userID
     * @return {@link CollectionModel<AddressRest>} for representation in HAL format
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.userId")
    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"}
    )
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id){

        List<AddressRest> addressesListModel = new ArrayList<>();
        // get List<addressesDto> from DB
        List<AddressDto> addressesDto = addressesService.getAddresses(id);
        // if they not null convert them List<AddressDto> -> List<AddressRest>
        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            addressesListModel = new ModelMapper().map(addressesDto, listType);
            // create and add HEATOS links to each address
            for (AddressRest addressRest : addressesListModel) {
                // link to itself
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
                addressRest.add(addressLink);
                // link to user
                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressRest.add(userLink);
            }
        }
        return new CollectionModel<>(addressesListModel);
    }

    // http://localhost:8080/mobile-app-ws/users/[user public ID]/addresses/[address public ID]

    /**
     * Return the specific address of user with HEATOS links
     *
     * @param userId    - public userID
     * @param addressId - public addressID
     * @return {@link EntityModel<AddressRest>} for representation in HAL format
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == principal.userId")
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"}
    )
    public EntityModel<AddressRest> getUserAddress(@PathVariable String userId,
                                                   @PathVariable String addressId){
        // get AddressDto from DB
        AddressDto addressDto = addressesService.getAddress(addressId);
        // convert AddressDto -> AddressRest
        ModelMapper modelMapper = new ModelMapper();
        AddressRest addressRestModel = modelMapper.map(addressDto, AddressRest.class);
        // create HEATOS link to itself, user and all user's addresses
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        // add all links to AddressRest
        addressRestModel.add(addressLink);
        addressRestModel.add(userLink);
        addressRestModel.add(addressesLink);

        return new EntityModel<>(addressRestModel);
    }

    // http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsfs

    /**
     * Handle GET request from Email Verification Service to verify token
     *
     * @param token - email verification token from request
     * @return {@link OperationStatusModel}
     */
    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        // check that token is valid and has not expired
        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }
        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/password-reset-request

    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/password-reset

    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return returnValue;
    }
}
