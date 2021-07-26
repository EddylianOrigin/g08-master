package Exceptions.SpielraumErstellen;

import Exceptions.SpielraumErstellenException;

/**
 * Exception, falls beim erstellen eines Spielraum eine ungueltige Spieleranzahl gewaehlt wurde
 */
public class SpielerAnzahlException extends SpielraumErstellenException{
    public SpielerAnzahlException()
    {
        super("Die Spieleranzahl muss zwischen 2 und 6 liegen");
    }
}
