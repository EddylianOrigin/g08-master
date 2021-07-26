package Exceptions.SpielerLoeschen;

import Exceptions.SpielerLoeschenException;

/**
 * Exception, falls das eingegebene Passwort nicht korrekt ist
 */
public class PasswortException extends SpielerLoeschenException {
    public PasswortException() {
        super("Das eingegebene Passwort ist nicht korrekt");
    }
}
