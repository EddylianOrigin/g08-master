package ServerRMIInterfaces;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
/**
 * Beinhaltet die abstrakten Methoden zur Lobby, die der Client Nutzen kann.
 */
public interface Lobby extends java.rmi.Remote {

    /**
     * Erstellt einen neuen Spielraum mit übergebenen Raumname und Raumgröße
     *
     * @param raumname   Raumname des zu erstellenden Spielraums
     * @param size       Raumsgröße des zu erstellenden Spielraums
     * @param nutzername Nutzername des Raumhosts
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void createSpielraum(String raumname, int size, String nutzername) throws RemoteException;


    /**
     * Gibt eine Liste der Nutzernamen aller angemeldeten Benutzer zurück
     *
     * @return Liste der Nutzernamen aller angemeldeten Benutzer
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public List<String> getNutzerListe() throws RemoteException;


    /**
     * Gibt eine Liste der Nutzernamen aller Benutzer in der Lobby zurück
     *
     * @return Liste der Nutzernamen aller Benutzer in der Lobby zurück
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public List<String> getLobbyNutzerListe() throws RemoteException;


    /**
     * Fügt Spieler in Spielraum ein
     *
     * @param raumname   Raumname des Spielraums
     * @param nutzername Nutzername des Spielers
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void joinSpielraum(String raumname, String nutzername) throws RemoteException;


    /**
     * An den Server gesendete Nachricht wird mit Benutzername des Senders in der Lobby, der sich dort befindeten Nutzer angezeigt
     *
     * @param nutzername Name des Nutzers, der die Nachricht gesendet hat.
     * @param message    Nachricht des Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public void sendChatmessage(String nutzername, String message) throws RemoteException;


    /**
     * Gibt von MySQL Server abgefragte Bestenliste zurück
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     * @return Bestenliste.
     */
    public Map<String, Integer> getBestenliste() throws IOException, SQLException;


    /**
     * Gibt Spielraumliste zurück
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @return Liste der Namen der Spielraeume.
     */
    public List<String> getSpielraumNamen() throws RemoteException;
}
