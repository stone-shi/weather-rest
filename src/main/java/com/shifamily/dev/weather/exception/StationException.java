package com.shifamily.dev.weather.exception;

public class StationException extends Exception {
    public StationException(String reason){
        super(reason);
    }
    public StationException(String reason, Throwable e ){
        super(reason, e);
    }

}
