package Exceptions.Anmeldung;

import Exceptions.AnmeldungException;

/**
 * Exception, falls der anzumeldende Spieler bereits angemeldet ist
 */
public class BereitsAngemeldetException extends AnmeldungException {
    public BereitsAngemeldetException() {
        super("Der Benutzer ist bereits angemeldet.");
    }
}
