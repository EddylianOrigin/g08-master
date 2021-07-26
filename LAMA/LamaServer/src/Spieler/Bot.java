package Spieler;

/**
 * Interface fuer Bots.
 */
public interface Bot {
    /**
     * Fuehrt die Spielzuege der Bots aus.
     */
    public void play();

    /**
     * Waehlt einen Chip zum abgeben aus und gibt zurueck, ob der ausgewaehlte Chip schwarz ist, oder nicht.
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     */
    public boolean chipAbgeben();
}
