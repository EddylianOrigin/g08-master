package Exceptions;

/**
 * Basisexception zu allen Exceptions, welche beim Beitreten eines Spielraums auftreten koennen
 */
public class SpielraumBeitretenException extends IllegalArgumentException {
    public SpielraumBeitretenException(String msg) {
        super(msg);
    }
}
