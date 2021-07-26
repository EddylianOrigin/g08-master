package Exceptions.Anmeldung;

import Exceptions.AnmeldungException;

/**
 * Exception, falls das eingegebene Passwort nicht korrekt ist
 */
public class PasswortException extends AnmeldungException {
    public PasswortException() {
        super("Das eingegebene Passwort ist nicht korrekt");
    }
}
