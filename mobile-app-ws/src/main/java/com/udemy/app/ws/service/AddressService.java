package com.udemy.app.ws.service;

import com.udemy.app.ws.exceptions.AddressServiceException;
import com.udemy.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);

    AddressDto getAddress(String addressId) throws AddressServiceException;
}
