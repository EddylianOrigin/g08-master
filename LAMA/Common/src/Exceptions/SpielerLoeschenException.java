package Exceptions;

/**
 * Basisexception zu allen Exceptions, welche beim Loeschen eines Spielers auftreten koennen
 */
public class SpielerLoeschenException extends IllegalArgumentException {
    public SpielerLoeschenException(String msg) {
        super(msg);
    }

}
