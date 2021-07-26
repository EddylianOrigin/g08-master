package Exceptions;

/**
 * Basisexception zu allen Exceptions, welche beim Aendern eines Spielraums auftreten koennen
 */
public class SpielraumAendernException extends IllegalArgumentException {
    public SpielraumAendernException(String msg) {
        super(msg);
    }
}
