package Exceptions;

/**
 * Basisexception zu allen Exceptions, welche beim Erstellen eines Spielraums auftreten koennen
 */
public class SpielraumErstellenException extends IllegalArgumentException {
    public SpielraumErstellenException(String msg) {
        super(msg);
    }
}
