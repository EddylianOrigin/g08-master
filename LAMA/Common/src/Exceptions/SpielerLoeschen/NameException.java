package Exceptions.SpielerLoeschen;

import Exceptions.SpielerLoeschenException;

/**
 * Exception, falls der Spieler mit dem angegebenen Namen nicht existiert
 */
public class NameException extends SpielerLoeschenException {
    public NameException() {
        super("Der eingegebene Benutzername ist nicht korrekt");
    }
}
