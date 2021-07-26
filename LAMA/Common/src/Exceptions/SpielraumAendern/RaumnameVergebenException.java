package Exceptions.SpielraumAendern;

import Exceptions.SpielraumAendernException;

/**
 * Exception, falls beim aendern eines Spielraum ein bereits verwendeter Spielraumname gewaehlt wurde
 */
public class RaumnameVergebenException extends SpielraumAendernException {
    public RaumnameVergebenException() {
        super("Der eingegebene Raumname ist bereits vergeben");
    }
}
