package com.simplecasino.walletservice.exception;

import org.springframework.http.HttpStatus;

public class RestApiException extends RuntimeException {

    public enum Type {
        PLAYER_ALREADY_EXIST(HttpStatus.CONFLICT, 40901, "Player already registered"),
        PLAYER_NOT_FOUND(HttpStatus.NOT_FOUND, 40401, "Player not found"),
        INSUFFICIENT_BALANCE(HttpStatus.CONFLICT, 40902, "Insufficient funds");

        private HttpStatus status;
        private int code;
        private String message;

        Type(HttpStatus status, int code, String message) {
            this.status = status;
            this.code = code;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    private Type type;

    public RestApiException(Type type) {
        this.type = type;
    }

    public RestApiException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
