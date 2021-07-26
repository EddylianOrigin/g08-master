package Exceptions;

/**
 * Exception fuer die Wahl einer, nach dem Regelwerk ungueltige, Karte
 */
public class KartenWahlException extends IllegalArgumentException {
    public KartenWahlException() {
        super("Bitte wählen sie eine nach Regelwerk gültige Karte zum ablegen ab.");
    }
}
