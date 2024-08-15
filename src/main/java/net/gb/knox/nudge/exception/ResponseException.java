package net.gb.knox.nudge.exception;

import lombok.Getter;

@Getter
public class ResponseException extends Exception {

    private final String message;
    private final String code;

    public ResponseException(String code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }
}
