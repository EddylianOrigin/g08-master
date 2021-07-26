package Exceptions;

/**
 * Basisexception zu allen Exceptions, welche beim Registrieren eines Spielers auftreten koennen
 */
public class RegistrierungException extends IllegalArgumentException {
    public RegistrierungException(String msg) {
        super(msg);
    }
}
