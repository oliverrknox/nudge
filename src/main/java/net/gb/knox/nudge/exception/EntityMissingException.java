package net.gb.knox.nudge.exception;

public class EntityMissingException extends ResponseException {
    public EntityMissingException(String message) {
        super("NOT_FOUND", message);
    }
}