package ServerRMIInterfaces;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.sql.SQLException;
/**
 * Beinhaltet die abstrakten Methoden zur Kontoverwaltung, die der Client Nutzen kann.
 */
public interface Account extends java.rmi.Remote {

    /**
     * Login eines Benutzers
     * Nutzername und Passwort werden geprueft
     * RMI Addresse und Port bei erfolgreichem Pruefen abgespeichert
     *
     * @param nutzername Beim Login verwendeter Nutzername
     * @param pw         Beim Login verwendetes Passwort
     * @param portNr     RMI Port Nummer des LamaClients
     * @param ip         RMI IP Addresse des LamaClients
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    void login(String nutzername, String pw, int portNr, InetAddress ip) throws RemoteException, IOException, SQLException;

    /**
     * Logout eines Benutzers
     * Nutzer wird aus Liste der angemeldeten Spieler entfernt
     *
     * @param nutzername Nutzername des abzumeldenden Benutzers
     * @param portNr     RMI Port Nummer des LamaClients des abzumeldenden Benutzers
     * @param ip         RMI IP Addresse des LamaClients des abzumeldenden Benutzers
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    void logout(String nutzername, int portNr, InetAddress ip) throws RemoteException;

    /**
     * Registrierung eines neuen Benutzers
     * Es wird geprueft ob Nutzername und Passwort eingegeben wurden und der Nutzername noch nicht vergeben wurde
     *
     * @param nutzername Bei der Registrierung eingegebener Nutzername
     * @param pw         Bei der Registrierung
     * @param portNr     Port des Nutzers(Registry des Nutzers).
     * @param ip         IP-Adresse des Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    void register(String nutzername, String pw, int portNr, InetAddress ip) throws RemoteException, IOException, SQLException;

    /**
     * Loeschen eines Benutzers
     * Es wird ob Nutzername und Passwort eingegeben wurden und korrekt sind
     * Daraufhin werden die Nutzerdaten aus der Datenbank entfernt und der Nutzer abgemeldet
     *
     * @param nutzername Nutzername des zu loeschenden Nutzers
     * @param pw         Passwort des zu loeschenden Nutzers
     * @param angemeldeterNutzername     Nutzername des angemeldeten Accounts.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    void delete(String nutzername, String pw, String angemeldeterNutzername ) throws IOException, SQLException;
}
