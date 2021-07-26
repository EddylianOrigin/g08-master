package Exceptions.SpielraumErstellen;

import Exceptions.SpielraumErstellenException;

/**
 * Exception, falls beim erstellen eines Spielraum ein bereits verwendeter Spielraumname gewaehlt wurde
 */
public class RaumnameVergebenException extends SpielraumErstellenException {
    public RaumnameVergebenException ()
    {
        super("Der eingegebene Raumname ist bereits vergeben");
    }
}
