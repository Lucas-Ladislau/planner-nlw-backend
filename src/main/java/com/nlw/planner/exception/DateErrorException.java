package com.nlw.planner.exception;

public class DateErrorException extends RuntimeException{

    public DateErrorException(){
        super("Data inválida");
    }
    public DateErrorException(String message){
        super(message);
    }
}
