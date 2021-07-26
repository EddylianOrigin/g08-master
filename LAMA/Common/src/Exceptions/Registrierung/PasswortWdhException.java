package Exceptions.Registrierung;

import Exceptions.RegistrierungException;

/**
 * Exception, falls die, bei der Registrierung, eingegebenen Passwoerter nicht uebereinstimmen
 */
public class PasswortWdhException extends RegistrierungException {
    public PasswortWdhException() {
        super("Die Passwörter im Passwort Feld " +
                "und im PasswortWdh Feld stimmen nicht überein");
    }
}
