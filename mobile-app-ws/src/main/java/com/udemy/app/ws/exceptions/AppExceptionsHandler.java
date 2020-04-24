package com.udemy.app.ws.exceptions;

import com.udemy.app.ws.ui.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;


/**
 * Handle and customize Specific Exceptions
 */
@ControllerAdvice
public class AppExceptionsHandler {

    /**
     * Handle the {@link UserServiceException} exceptions.
     * Set the {@link ErrorMessage}, {@link HttpStatus} and return the {@link ResponseEntity} as a response
     *
     * @param ex {@link UserServiceException}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity} with HTTP Status, Header, Error Message and Time Stamp
     */
    @ExceptionHandler(value = {UserServiceException.class})
    public ResponseEntity<Object> handleUserServiceException (UserServiceException ex, WebRequest request) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Handle the {@link AddressServiceException} exceptions.
     */
    @ExceptionHandler(value = {AddressServiceException.class})
    public ResponseEntity<Object> handleAddressServiceException (AddressServiceException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle and customize all other Exceptions
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions (Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
