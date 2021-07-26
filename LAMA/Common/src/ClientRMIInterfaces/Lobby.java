package ClientRMIInterfaces;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Beinhaltet die abstrakten Methoden zum Aktualisieren der Lobby durch den Server.
 */
public interface Lobby extends java.rmi.Remote {

    /**
     * Gibt die Informationen des Servers ueber die Bestenliste an das Lobby Objekt weiter.
     *
     * @param bestenliste ordet Nutzernamen eine Punktzahl zu, muss nicht sortiert sein.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    public void updateBestenliste(Map<String, Integer> bestenliste) throws RemoteException;


    /**
     * Gibt die Informationen des Servers ueber die existierenden Spielraueme an das Lobyb Objekt weiter.
     *
     * @param spielraumNamen Liste an existierenden Spielraumennamen.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    public void updateSpielraeume(List<String> spielraumNamen) throws RemoteException;


    /**
     * Gibt die Informationen des Servers ueber die neue Chat Nachricht an das Lobby Objekt weiter.
     *
     * @param nutzername Name der Person, die die Nachricht verfasst hat.
     * @param nachricht  Von einer Person verschickter Inhalt(Text).
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    public void updateLobbyChat(String nutzername, String nachricht) throws RemoteException;

}
