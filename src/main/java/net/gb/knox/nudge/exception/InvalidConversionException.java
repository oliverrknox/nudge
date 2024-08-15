package net.gb.knox.nudge.exception;

public class InvalidConversionException extends ResponseException {
    public InvalidConversionException(String message) {
        super("INVALID_CONVERSION", message);
    }
}
