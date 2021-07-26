package Exceptions.Registrierung;

import Exceptions.RegistrierungException;

/**
 * Exception, falls ein Spieler mit dem angegebenen Namen bereits existiert
 */
public class RegistrierungnameException extends RegistrierungException {
    public RegistrierungnameException() {
        super("Der eingegebene Benutzername ist bereits vergeben");
    }
}
