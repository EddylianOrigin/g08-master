package Exceptions.SpielraumErstellen;

import Exceptions.SpielraumErstellenException;

/**
 * Exception, falls beim erstellen eines Spielraum eine Spielrauminformation nicht angegeben wurde
 */
public class MissingInputException extends SpielraumErstellenException {
    public MissingInputException()
    {
        super("Füllen Sie bitte alle Felder im Formular aus");
    }
}
