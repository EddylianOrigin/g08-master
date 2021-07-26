package Exceptions.SpielraumBeitreten;

import Exceptions.SpielraumBeitretenException;

/**
 * Exception, falls der zu betretende Spielraum nicht existiert
 */
public class RaumnichtVorhandenException extends SpielraumBeitretenException {
    public RaumnichtVorhandenException() {
        super("Der ausgewählte Spielraum konnte nicht gefunden werden");
    }
}
