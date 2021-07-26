package Exceptions.SpielraumAendern;

import Exceptions.SpielraumAendernException;

/**
 * Exception, falls beim aendern eines Spielraum eine unguelltige Spieleranzahl gewählt wurde
 */
public class SpielerAnzahlException extends SpielraumAendernException {
    public SpielerAnzahlException() {
        super("Die Spieleranzahl muss zwischen 2 und 6 liegen und " +
                "darf nicht kleiner sein, als die aktuelle Spieleranzahl des Raumes");
    }
}
