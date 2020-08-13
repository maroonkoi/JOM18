package com.softserve.edu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity doesn't exist")
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(){
        super();
    }

    public EntityNotFoundException(String message){
        super(message);
    }

    public EntityNotFoundException(Long id){
        super("Could not find User "+ id);
    }
}
