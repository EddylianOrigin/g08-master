package ClientRMIInterfaces;

import SpielRaumVerwaltung.Karte;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Beinhaltet die abstrakten Methoden zum Aktualisieren des Spielraumes durch den Server.
 */
public interface Spielraum extends java.rmi.Remote {

    /**
     * Gibt die Informationen des Servers ueber den Spielraum an das SpielraumGUI Objekt weiter.
     * Aktualisiert die Raumstatus-Anzeige und die Spieler in der GUI.
     *
     * @param size    Eingestelle maximal Spieleranzahl.
     * @param spieler Liste von sich aktuell im Raum befindeten Spielern.
     * @param raumName (eventuell geaenderter) Name des Rauemes.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    void updateSpielraum(int size, List<String> spieler, String raumName) throws RemoteException;


    /**
     * Gibt die Informationen des Servers ueber den Spielstatus an das SpielraumGUI Objekt weiter.
     *
     * @param amZug                 gibt an, ob der Spieler am Zug ist, oder nicht.
     * @param eigeneHand            Liste der Karten, die sich auf der Hand des Spielers befinden.
     * @param kartenAnzahl_Spieler  Anzahl der Karten pro Spieler im Spiel.
     * @param weißeChips_Spieler    Anzahl der weißen Chips pro Spieler im Spiel.
     * @param schwarzeChips_Spieler Anzahl der schwarzen CHips pro Spieler im Spiel.
     * @param karte_Ablagestapel    Karte, die oben auf dem Ablagestapel liegt.
     * @param kartenAnzahl_Nachziehstapel Anzahl der Karten auf dem Nachziehstapel.
     * @param ausgestiegen Gibt pro Spieler im Spiel an, ob der Spieler ausgestiegen ist.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    void updateSpielStatus(int amZug, List<Karte> eigeneHand, int[] kartenAnzahl_Spieler,
                           int[] weißeChips_Spieler, int[] schwarzeChips_Spieler,
                           Karte karte_Ablagestapel, int kartenAnzahl_Nachziehstapel, boolean[] ausgestiegen) throws RemoteException;


    /**
     * Gibt die Informationen des Servers, dass das Spiel am laufen ist an das SpielraumGUI Objekt weiter.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    void updateGestartet() throws RemoteException;


    /**
     * Gibt die Informationen des Servers, dass das Spiel am beendet ist an das SpielraumGUI Objekt weiter.
     *
     * @param sieger Spielername des Spielers der gewonnen hat.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    void updateBeendet(List<String> sieger) throws RemoteException;


    /**
     * Gibt die Informationen des Servers ueber die neue Chat Nachricht an das SpielraumGUI Objekt weiter.
     *
     * @param nutzername Name der Person, die die Nachricht verfasst hat.
     * @param nachricht  Von einer Person verschickter Inhalt(Text).
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    void updateRaumChat(String nutzername, String nachricht) throws RemoteException;


    /**
     * Gibt die Informationen des Servers, dass der Spieler einen Chip abgeben soll SpielraumGUI Objekt weiter.
     *
     * @param hasWChips gibt an, ob der Spieler weiße Chips besitzt.
     * @param hasBChips gibt an, ob der Spieler schwarze Chips bestizt.
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    boolean chipAbgeben(boolean hasWChips, boolean hasBChips) throws RemoteException;


    /**
     * Gibt die Informationen des Servers, dass der Sielraum geloescht wurde an das SpielraumGUI Objekt weiter.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    void updateGeloescht() throws  RemoteException;

}
