package Exceptions.SpielraumAendern;

import Exceptions.SpielraumAendernException;

/**
 * Exception, falls beim aendern eines Spielraum eine Spielrauminformation nicht angegeben wurde
 */
public class MissingInputException extends SpielraumAendernException {
    public MissingInputException() {
        super("Füllen Sie bitte alle Felder im Formular aus");
    }
}
