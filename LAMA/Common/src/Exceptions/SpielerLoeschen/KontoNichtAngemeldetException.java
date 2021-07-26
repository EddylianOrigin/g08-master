package Exceptions.SpielerLoeschen;

import Exceptions.SpielerLoeschenException;

/**
 * Exception, falls der angemeldete Nutzername nicht mit dem, des zu loeschenden Konto uebereinstimmt.
 */
public class KontoNichtAngemeldetException extends SpielerLoeschenException{
    public KontoNichtAngemeldetException(){
        super("Sie können nur das angemeldete Benutzerkonto löschen," +
                " bitte geben Sie den entsprechenden Nutzernamen ein");
    }
}
