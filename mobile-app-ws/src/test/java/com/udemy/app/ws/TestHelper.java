package com.udemy.app.ws;

import com.udemy.app.ws.io.entity.AddressEntity;
import com.udemy.app.ws.io.entity.AuthorityEntity;
import com.udemy.app.ws.io.entity.RoleEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import com.udemy.app.ws.shared.Roles;
import com.udemy.app.ws.shared.dto.AddressDto;
import com.udemy.app.ws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class TestHelper {

    public static final String FIRST_NAME = "Freddy";
    public static final String LAST_NAME = "Krueger";
    public static final String PUBLIC_USER_ID = "$userId$";
    public static final String ENCRYPTED_PASSWORD = "$password$";
    public static final String USER_EMAIL = "night@mare.com";
    public static final String EMAIL_VERIFICATION_TOKEN = "$emailVerificationToken$";
    public static final String RAW_PASSWORD = "qwery1234";
    public static final String PUBLIC_ADDRESS_ID = "$publicAddressId$";

    public static UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setLastName(LAST_NAME);
        userEntity.setUserId(PUBLIC_USER_ID);
        userEntity.setEncryptedPassword(ENCRYPTED_PASSWORD);
        userEntity.setEmail(USER_EMAIL);
        userEntity.setEmailVerificationToken(EMAIL_VERIFICATION_TOKEN);
        userEntity.setAddresses(createAddressesEntity());
        userEntity.setRoles(Arrays.asList(getRoleEntity()));
        return userEntity;
    }

    public static UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setUserId(PUBLIC_USER_ID);
        userDto.setEncryptedPassword(ENCRYPTED_PASSWORD);
        userDto.setEmail(USER_EMAIL);
        userDto.setEmailVerificationToken(EMAIL_VERIFICATION_TOKEN);
        userDto.setAddresses(createAddressesDto());
        userDto.setRoles(Arrays.asList(Roles.ROLE_USER.name()));
        return userDto;
    }

    public static RoleEntity getRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setAuthorities(createAuthorities(Roles.ROLE_USER));
        roleEntity.setName(Roles.ROLE_USER.name());
        return roleEntity;
    }
    public static List<AddressDto> createAddressesDto() {
        AddressDto shippingAddressDto = new AddressDto();
        shippingAddressDto.setAddressId("$shippingAddressId$");
        shippingAddressDto.setType("shipping");
        shippingAddressDto.setCity("Saint-Petersburg");
        shippingAddressDto.setCountry("Russia");
        shippingAddressDto.setPostalCode("666");
        shippingAddressDto.setStreetName("Elm Street");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setAddressId("$billingAddressId$");
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Saint-Petersburg");
        billingAddressDto.setCountry("Russia");
        billingAddressDto.setPostalCode("999");
        billingAddressDto.setStreetName("Elm Street");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(shippingAddressDto);
        addresses.add(billingAddressDto);

        return addresses;
    }

    public static List<AddressEntity> createAddressesEntity() {
        List<AddressDto> addressesDto = createAddressesDto();
        Type listType = new TypeToken<List<AddressEntity>>() {
        }.getType();
        return new ModelMapper().map(addressesDto, listType);
    }

    private static Collection<AuthorityEntity> createAuthorities(Roles roles) {
        AuthorityEntity readAuthority = new AuthorityEntity("READ_AUTHORITY");
        AuthorityEntity writeAuthority = new AuthorityEntity("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = new AuthorityEntity("DELETE_AUTHORITY");
        switch (roles) {
            case ROLE_USER:
                return Arrays.asList(readAuthority, writeAuthority);
            case ROLE_ADMIN:
                return Arrays.asList(readAuthority, writeAuthority, deleteAuthority);
            default:
                return Collections.emptyList();
        }

    }
}
