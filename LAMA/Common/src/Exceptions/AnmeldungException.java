package Exceptions;

/**
 * Basisexception zu allen Exceptions, welche beim Anmelden eines Spielers auftreten koennen
 */
public class AnmeldungException extends IllegalArgumentException {
    public AnmeldungException(String msg) {
        super(msg);
    }
}
