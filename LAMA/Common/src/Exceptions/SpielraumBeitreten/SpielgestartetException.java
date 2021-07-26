package Exceptions.SpielraumBeitreten;

import Exceptions.SpielraumBeitretenException;

/**
 * Exception, falls das Spiel im zu betretenden Spielraum bereits gestartet wurde
 */
public class SpielgestartetException extends SpielraumBeitretenException {
    public SpielgestartetException() {
        super("Das Spiel im gewählten Spielraum läuft schon");
    }
}
