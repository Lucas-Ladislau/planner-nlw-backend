package com.nlw.planner.exception;

public class TripNotFoundException extends RuntimeException{

    public TripNotFoundException(){
        super("Trip não encontrada");
    }
    public TripNotFoundException(String message){
        super(message);
    }

}
