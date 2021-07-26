package Exceptions.SpielerLoeschen;

import Exceptions.SpielerLoeschenException;

/**
 * Exception, falls beim loeschen eines Spielers eine anzugebende Information nicht angegeben wurde
 */
public class MissingInputException extends SpielerLoeschenException {
    public MissingInputException() {
        super("Füllen Sie bitte alle Felder im Formular aus");
    }
}
