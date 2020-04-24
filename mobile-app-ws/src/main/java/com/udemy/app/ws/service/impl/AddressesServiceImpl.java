package com.udemy.app.ws.service.impl;

import com.udemy.app.ws.exceptions.AddressServiceException;
import com.udemy.app.ws.io.entity.AddressEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import com.udemy.app.ws.io.repository.AddressRepository;
import com.udemy.app.ws.io.repository.UserRepository;
import com.udemy.app.ws.service.AddressService;
import com.udemy.app.ws.shared.dto.AddressDto;
import com.udemy.app.ws.ui.model.response.ErrorMessage;
import com.udemy.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    /**
     * Get a list of addresses of specific user from DB
     *
     * @param userId - public userID
     * @return list of {@link AddressDto}
     */
    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        // Step 1. Get UserEntity by ID or return an empty list
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) return returnValue;
        // Step 2. Find all addresses related with this UserEntity
        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        // Step 3. Convert AddressEntity -> AddressDto and add them to result list
        for (AddressEntity addressEntity : addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
        }
        return returnValue;
    }

    /**
     * Get the specific address from DB
     *
     * @param addressId - public addressID
     * @return {@link AddressDto}
     * @throws AddressServiceException
     */
    @Override
    public AddressDto getAddress(String addressId) throws AddressServiceException {
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity == null) throw new AddressServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new ModelMapper().map(addressEntity, AddressDto.class);
    }
}
