package ServerRMI;

import ServerRMIInterfaces.Account;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static ServerRMI.RMIServer.getMysqlConnection;

/**
 * Verwaltet das anmelden, abmelden, registrieren und loeschen von Nutzern.
 */
public class AccountImpl implements Account {


    private final LobbyImpl lobby;

    /**
     * Erstellt das AccountImpl Objekt und weist diesem eine Lobby zu.
     *
     * @param lobby Objekt in dem die Lobby und deren Nutzer verwaltet werden.
     */
    public AccountImpl(LobbyImpl lobby) {
        this.lobby = lobby;
    }

    /**
     * Login eines Benutzers
     * Nutzername und Passwort werden geprueft
     * RMI Addresse und Port bei erfolgreichem Prüfen abgespeichert
     *
     * @param nutzername Beim Login verwendeter Nutzername
     * @param pw         Beim Login verwendetes Passwort
     * @param portNr     RMI Port Nummer des LamaClients
     * @param ip         RMI IP Addresse des LamaClients
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    @Override
    public void login(String nutzername, String pw, int portNr, InetAddress ip) throws SQLException, IOException, RemoteException {
        if (nutzername == null || nutzername.equals("") || pw == null || pw.equals("")) {
            throw new Exceptions.Anmeldung.MissingInputException();
        } else if (benutzernamePasswortKorrekt(nutzername, pw)) {
            for (String n : this.lobby.getNutzerListe()) {
                if (n.equals(nutzername)) {
                    throw new Exceptions.Anmeldung.BereitsAngemeldetException();
                }
            }
            this.lobby.addNutzer(nutzername, portNr, ip);
            this.lobby.addLobbyNutzer(nutzername, portNr, ip);
        } else if (benutzernameNichtVergeben(nutzername)) {
            throw new Exceptions.Anmeldung.AnmeldenameException();
        } else {
            throw new Exceptions.Anmeldung.PasswortException();
        }
    }

    /**
     * Logout eines Benutzers
     * Nutzer wird aus Liste der angemeldeten Spieler entfernt
     *
     * @param nutzername Nutzername des abzumeldenden Benutzers
     * @param portNr     RMI Port Nummer des LamaClients des abzumeldenden Benutzers
     * @param ip         RMI IP Addresse des LamaClients des abzumeldenden Benutzers
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void logout(String nutzername, int portNr, InetAddress ip) throws RemoteException {
        this.lobby.removeNutzer(nutzername);
        this.lobby.removeLobbyNutzer(nutzername);
    }

    /**
     * Registrierung eines neuen Benutzers
     * Es wird geprüft ob Nutzername und Passwort eingegeben wurden und der Nutzername noch nicht vergeben wurde
     *
     * @param nutzername Bei der Registrierung eingegebener Nutzername
     * @param pw         Bei der Registrierung
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    @Override
    public void register(String nutzername, String pw, int portNr, InetAddress ip) throws RemoteException, SQLException, IOException {
        if (nutzername == null || nutzername.equals("") || pw == null || pw.equals("")) {
            throw new Exceptions.Registrierung.MissingInputException();
        } else if (benutzernameNichtVergeben(nutzername)) {
            benutzerHinzufuegen(nutzername, pw);
            this.lobby.addNutzer(nutzername, portNr, ip);
            this.lobby.addLobbyNutzer(nutzername, portNr, ip);
            //lobby.updateBestenliste();
            lobby.updateBestenlisteRegister(nutzername);
        } else {
            throw new Exceptions.Registrierung.RegistrierungnameException();
        }
    }

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
    @Override
    public void delete(String nutzername, String pw, String angemeldeterNutzername) throws SQLException, IOException, RemoteException {
        if (nutzername == null || nutzername.equals("") || pw == null || pw.equals("")) {
            throw new Exceptions.SpielerLoeschen.MissingInputException();
        } else if (benutzernameNichtVergeben(nutzername)) {
            throw new Exceptions.SpielerLoeschen.NameException();
        } else if(!angemeldeterNutzername.equals(nutzername)){
            throw new Exceptions.SpielerLoeschen.KontoNichtAngemeldetException();
        } else if (benutzernamePasswortKorrekt(nutzername, pw)) {
            benutzerLoeschen(nutzername);
            this.lobby.removeNutzer(nutzername);
            this.lobby.removeLobbyNutzer(nutzername);
            lobby.updateBestenliste();
        } else {
            throw new Exceptions.SpielerLoeschen.PasswortException();
        }
    }

    /**
     * Testet ob in der Datenbank ein Datensatz mit den angegebenen Argumenten Nutzername und Passwort existiert
     *
     * @param nutzername Zu überpruefender Nutzername
     * @param pw         Zu überpruefendes Passwort
     * @return true, wenn Datensatz in Datenbank, sonst false
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    boolean benutzernamePasswortKorrekt(String nutzername, String pw) throws SQLException, IOException {
        int size = 0;
        //Mit MySQL Server verbinden
        Connection mysql = getMysqlConnection();
        //Connection mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/lama", "lamaAdmin", "lamaAdminPasswort");
        Statement stm = mysql.createStatement();
        //Alle Datensätze mit Benutzername = nutzername und Passwort = pw abfragen
        ResultSet rs = stm.executeQuery("SELECT * FROM `user` WHERE `benutzername` = \"" + nutzername + "\" AND `passwort` = \"" + pw + "\"");
        //Datensätze zählen
        while (rs.next()) {
            size += 1;
        }
        //MySQL Verbindung beenden
        mysql.close();
        return size == 1;
    }

    /**
     * Testet ob der angegebene Benutzername bereits in der Datenbank gespeichert ist
     *
     * @param nutzername Zu Ueberpruefender Benutzername
     * @return true, wenn Benutzername in Datenbank, sonst false
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    boolean benutzernameNichtVergeben(String nutzername) throws SQLException, IOException {
        int size = 0;
        //Mit MySQL Server verbinden
        Connection mysql = getMysqlConnection();
        //Connection mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/lama", "lamaAdmin", "lamaAdminPasswort");
        Statement stm = mysql.createStatement();
        //Alle Datensätze mit Benutzername = nutzername abfragen
        ResultSet rs = stm.executeQuery("SELECT * FROM `user` WHERE `benutzername` = \"" + nutzername + "\"");
        //Datensätze zählen
        while (rs.next()) {
            size += 1;
        }
        //MySQL Verbindung beenden
        mysql.close();
        return size == 0;
    }

    /**
     * Fuegt einen Benutzer mit Nutzername und Passwort zur Datenbank hinzu
     *
     * @param nutzername Hinzuzufuegender Nutzername
     * @param pw         Hinzuzufuegendes Passwort
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    void benutzerHinzufuegen(String nutzername, String pw) throws SQLException, IOException {
        //Mit MySQL Server verbinden
        Connection mysql = getMysqlConnection();
        //Connection mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/lama", "lamaAdmin", "lamaAdminPasswort");
        Statement stm = mysql.createStatement();
        //Nutzerdaten (Benutzername, Passwort) einfügen
        stm.executeUpdate("INSERT INTO `user`(`benutzername`, `passwort`) VALUES (\"" + nutzername + "\", \"" + pw + "\")");
        //MySQL Verbindung beenden
        mysql.close();
    }

    /**
     * Löscht einen Benutzer anhand des Nutzernames aus der Datenbank
     *
     * @param nutzername Nutzername des zu loeschenden Benutzers
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */
    void benutzerLoeschen(String nutzername) throws SQLException, IOException {
        //Mit MySQL Server verbinden
        Connection mysql = getMysqlConnection();
        //Connection mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/lama", "lamaAdmin", "lamaAdminPasswort");
        Statement stm = mysql.createStatement();
        //Nutzerdaten (Benutzername, Passwort) einfügen
        stm.executeUpdate("DELETE FROM `user` WHERE `benutzername` = \"" + nutzername + "\"");
        //MySQL Verbindung beenden
        mysql.close();
    }
}
