package me.gg.pinit.infra.exception;

public class OIDCException extends RuntimeException {
    public OIDCException(String message) {
        super(message);
    }
}
