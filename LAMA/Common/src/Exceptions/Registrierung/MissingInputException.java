package Exceptions.Registrierung;

import Exceptions.RegistrierungException;

/**
 * Exception, falls beim registrieren eines Spielers eine anzugebende Information nicht angegeben wurde
 */
public class MissingInputException extends RegistrierungException {
    public MissingInputException() {
        super("Füllen Sie bitte alle Felder im Formular aus");
    }
}
