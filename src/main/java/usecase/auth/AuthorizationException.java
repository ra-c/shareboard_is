package usecase.auth;

import javax.ejb.ApplicationException;

/**
 * Eccezione relativa all'autenticazione
 */
@ApplicationException(rollback = true)
public class AuthorizationException extends RuntimeException{
    public AuthorizationException() {
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }
}
