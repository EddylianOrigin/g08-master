package Exceptions.Anmeldung;

import Exceptions.AnmeldungException;

/**
 * Exception, falls beim anmelden eines Spielers eine anzugebende Information nicht angegeben wurde
 */
public class MissingInputException extends AnmeldungException {
    public MissingInputException() {
        super("Füllen Sie bitte alle Felder im Formular aus");
    }
}
