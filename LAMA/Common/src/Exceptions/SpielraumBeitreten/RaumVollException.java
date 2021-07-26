package Exceptions.SpielraumBeitreten;

import Exceptions.SpielraumBeitretenException;

/**
 * Exception, falls der zu betretende Spielraum schon voll ist
 */
public class RaumVollException extends SpielraumBeitretenException {
    public RaumVollException() {
        super("Der ausgewählte Spielraum ist schon voll");
    }
}
