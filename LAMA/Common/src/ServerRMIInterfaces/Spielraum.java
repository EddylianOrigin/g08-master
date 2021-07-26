package ServerRMIInterfaces;

import Exceptions.KartenWahlException;
import Exceptions.SpielraumAendern.MissingInputException;
import Exceptions.SpielraumAendern.RaumnameVergebenException;
import Exceptions.SpielraumAendern.SpielerAnzahlException;
import SpielRaumVerwaltung.Karte;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
/**
 * Beinhaltet die abstrakten Methoden zum Spielraum, die der Client Nutzen kann.
 */
public interface Spielraum extends java.rmi.Remote {

    /**
     * Gibt zurueck, ob das Spiel gerade laeuft.
     * @return True, falls das spiel aktuell laeuft, sonst false
     * @throws RemoteException           bei Problemen mit der Kommuntikation zum Client.
     */
    public Boolean getSpielLaufend() throws RemoteException;


    /**
     * Ändert den Namen des Raumes und/oder die Größe dessen.
     *
     * @param roomName neuer Raumname.
     * @param size     neue Raumgroeße.
     * @throws RemoteException           bei Problemen mit der Kommuntikation zum Client.
     * @throws MissingInputException     wird geworfen, falls roomName null oder "" ist.
     * @throws RaumnameVergebenException wird geworfen, falls ein anderer Raum bereits den neue festgelegten Raumnamen besitzt.
     * @throws SpielerAnzahlException    wird geworfen, falls die Raumgroeße nicht zwischen 2 und 6 liegt,
     *                                   oder falls bereits mehr Spieler im Raum sind, als die neue Raumgroeße es erlaubt.
     */
    public void change(String roomName, int size) throws RemoteException;


    /**
     * Entfernt den Spielraum-verlassenden Nutzer aus der Nutzerliste und der SPielerliste und informiert die anderen Nutzer im Spielraum über die Änderung.
     *
     * @param nutzerName Name des Spielraum-verlassenden Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void leave(String nutzerName) throws RemoteException;


    /**
     * Der Spielraum wird geloescht, alle Nutzer kehren in die Lobby zurueck.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void delete() throws RemoteException;


    /**
     * Der Spieler, welcher aktuell am Zug ist steigt aus dem Durchgang aus.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void aussteigen() throws RemoteException;


    /**
     * Erzeugt die Bestenliste für die meschlichen Spieler im Raum.
     * @return Bestenliste.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public Map<String, Integer> getBestenliste() throws RemoteException;


    /**
     * Der Spieler legt eine Karte auf den Ablagestapel.
     *
     * @param kartenIndex Index der zu entfernenden Karte in der Liste.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws KartenWahlException wird geworfen, falls die gewählte Karte nicht nach Regelwerk ablegbar ist.
     */
    public void ablegen(int kartenIndex) throws RemoteException;


    /**
     * Eine Karte wird aus dem Nachziehstapel genommen (und entfernt) und im Hand des Spielers hinzugefügt.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void aufnehmen() throws RemoteException;


    /**
     * Startet das Spiel, ein zufaelliger Spieler begint.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void start() throws RemoteException;


    /**
     * An den Server gesendete Nachricht wird mit Benutzername des Senders in dem Spielraum, der sich dort befindeten Nutzer angezeigt
     *
     * @param nutzername Name des Nutzers, der die Nachricht gesendet hat.
     * @param message    Nachricht des Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void sendChatMessage(String nutzername, String message) throws RemoteException;


    /**
     * Gibt die Raumgroeße zurueck.
     *
     * @return maximal Spieleranzahl des Spielraumes.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public int getSize() throws RemoteException;


    /**
     * Gibt den Raumname zurueck.
     *
     * @return Name des Raumes.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public String getName() throws RemoteException;


    /**
     * Fuegt dem Spielraum einen Bot des normalen Schwierigkeitsgrades hinzu.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void addBotNormal() throws RemoteException;


    /**
     * Fuegt dem Spielraum einen Bot des schweren Schwierigkeitsgrades hinzu.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void addBotSchwer() throws RemoteException;


    /**
     * entfernt einen Bot des normalen Schwierigkeitsgrades aus dem Raum, insofern dieser existiert.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void removeBotNormal() throws RemoteException;


    /**
     * entfernt einen Bot des schweren Schwierigkeitsgrades aus dem Raum, insofern dieser existiert.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void removeBotSchwer() throws RemoteException;


    /**
     * Gibt den Namen des Hosts des Spielraumes zurueck.
     *
     * @return Name des Spielraumverwalters.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public String getHost() throws RemoteException;


    /**
     * Legt den Host des Spielraumes fest.
     *
     * @param hostIn Spielraumverwalter.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void setHost(String hostIn) throws RemoteException;


    /**
     * gibt eine Liste der Namen der Nutzer im Raum zurueck.
     *
     * @return Liste der Namen der Nutzer im Raum
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public List<String> getPlayerNamelist() throws RemoteException;


    /**
     * Gibt die Karten auf der Hand des Spielers mit dem Namen spielerName zurueck.
     * @param spielerName Name des Spieler.
     * @return Liste der Karten, die der Spieler auf der Hand hat.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public List<Karte> getPlayercards(String spielerName) throws RemoteException;
}
