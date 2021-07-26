package SpielRaumVerwaltung;

import java.io.Serializable;

/**
 * Klasse fuer die Karten des LAMA-Spiels.
 */
public class Karte implements Serializable {

    public final int kartenWert;

    public Karte(int kartenWert) {
        this.kartenWert = kartenWert;
    }

    /**
     * Prueft ob die Karte nach Regelwerk auf eine andere Karte gelegt werden darf.
     *
     * @param andereKarte Karte, auf die die Karte gelegt werden soll.
     * @return true, falls die Aktion nach Regelwerk zulaessig ist, sonst false.
     */
    public boolean canBePlacedOnTopOf(Karte andereKarte) {
        if (kartenWert == 10) {
            //eine Lamakarte darf auf eine andere Lamakarte, oder eine 6 gelegt werden
            return andereKarte.kartenWert == 10 || andereKarte.kartenWert == 6;
        } else {
            if (kartenWert == 1) {
                // eine 1 darf auf eine Lamakarte, oder eine Karte des Wertes 1 gelegt werden
                return andereKarte.kartenWert == 10 || andereKarte.kartenWert == 1;
            } else {
                //andere Karten dürfen abgelegt werden, wenn der Kartenwert gleich, oder um 1 höher als der Wert der anderen Karte ist
                return kartenWert == andereKarte.kartenWert || kartenWert == andereKarte.kartenWert + 1;
            }
        }
    }
}
