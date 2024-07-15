package com.nlw.planner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DateErrorException.class)
    public ResponseEntity<RestErrorResponse> handleDateErrorException(DateErrorException e){
        RestErrorResponse errorResponse = new RestErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleTripNotFoundErrorException(TripNotFoundException e){
        RestErrorResponse errorResponse = new RestErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
