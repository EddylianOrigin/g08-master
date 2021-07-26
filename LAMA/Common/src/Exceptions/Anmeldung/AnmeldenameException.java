package Exceptions.Anmeldung;

import Exceptions.AnmeldungException;

/**
 * Exception, falls ein Spieler mit dem angegebenen Namen nicht existiert
 */
public class AnmeldenameException extends AnmeldungException {
    public AnmeldenameException() {
        super("Der eingegebene Anmeldename ist nicht vergeben");
    }
}
