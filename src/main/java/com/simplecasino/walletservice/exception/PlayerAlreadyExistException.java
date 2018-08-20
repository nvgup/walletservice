package com.simplecasino.walletservice.exception;

public class PlayerAlreadyExistException extends RuntimeException {

    public PlayerAlreadyExistException(String s) {
        super(s);
    }
}