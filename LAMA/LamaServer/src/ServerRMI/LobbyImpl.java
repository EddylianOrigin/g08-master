package ServerRMI;

import ServerRMIInterfaces.Lobby;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ServerRMI.RMIServer.getMysqlConnection;

/**
 * Verwaltet die Lobby.
 */
public class LobbyImpl implements Lobby {

    private final List<String> spielraeume = new ArrayList<>();
    private final List<SpielraumImpl> spielraeumeImpl = new ArrayList<>();
    private final List<Nutzer> angemeldeteNutzer = new ArrayList<>();
    private final List<Nutzer> lobbyNutzer = new ArrayList<>();

    /**
     * Erstellt einen neuen Spielraum mit uebergebenen Raumname und Raumgroeße
     *
     * @param raumname   Raumname des zu erstellenden Spielraums
     * @param size       Raumsgroeße des zu erstellenden Spielraums
     * @param nutzername Nutzername des Raumhosts
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client
     */
    @Override
    public void createSpielraum(String raumname, int size, String nutzername) throws RemoteException {
        if (raumname == null || raumname.equals("") || nutzername == null || nutzername.equals("")) {
            throw new Exceptions.SpielraumErstellen.MissingInputException();
        } else if (size < 2 || size > 6) {
            throw new Exceptions.SpielraumErstellen.SpielerAnzahlException();
        } else if (spielraumNameVorhanden(raumname)) {
            throw new Exceptions.SpielraumErstellen.RaumnameVergebenException();
        } else {
            //Nutzer finden
            Nutzer nutzer = null;
            for (Nutzer n : lobbyNutzer) {
                if (n.name.equals(nutzername)) {
                    nutzer = n;
                    break;
                }
            }
            if (nutzer != null) {
                //Neuen Spielraum erstellen
                SpielraumImpl spielraumimpl = new SpielraumImpl(raumname, size, nutzer, this);
                RMIServer.createSpielraumService(spielraumimpl);
                //SpielraumImpl in Liste hinzufügen
                this.spielraeumeImpl.add(spielraumimpl);
                //Spieler aus lobbyNutzer entfernen
                this.lobbyNutzer.remove(nutzer);
                //Spielraum in Liste hinzufügen
                spielraeume.add(raumname);
                //Spielräume bei Clients updaten
                this.updateSpielraeume();
            }
        }
    }

    /**
     * Testet, ob Spielraumname bereits vergeben
     *
     * @param raumname Zu testender Raumname
     * @return true, wenn Raumname bereits vergeben, sonst false
     */
    boolean spielraumNameVorhanden(String raumname) throws RemoteException {
        for (String name : this.spielraeume) {
            if (name.equals(raumname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt eine Liste der Nutzernamen aller angemeldeten Benutzer zurueck
     *
     * @return Liste der Nutzernamen aller angemeldeten Benutzer
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client
     */
    @Override
    public List<String> getNutzerListe() throws RemoteException {
        List<String> namen = new ArrayList<>();
        for (Nutzer n : this.angemeldeteNutzer) {
            namen.add(n.name);
        }
        return namen;
    }

    /**
     * Gibt eine Liste der Nutzernamen aller Benutzer in der Lobby zurueck
     *
     * @return Liste der Nutzernamen aller Benutzer in der Lobby zurueck
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client
     */
    @Override
    public List<String> getLobbyNutzerListe() throws RemoteException {
        List<String> namen = new ArrayList<>();
        for (Nutzer n : this.lobbyNutzer) {
            namen.add(n.name);
        }
        return namen;
    }

    /**
     * Fügt Nutzer zu Liste der angemeldeten Nutzer hinzu
     *
     * @param nutzername Nutzername des hinzuzufuegenden Nutzers
     * @param portNr     Port Nummer des hinzuzufuegenden Nutzers
     * @param ip         IP des hinzuzufuegenden Nutzers
     */
    void addNutzer(String nutzername, int portNr, InetAddress ip) {
        this.angemeldeteNutzer.add(new Nutzer(nutzername, ip, portNr));
    }

    /**
     * Fuegt Nutzer zu Liste der Nutzer hinzu, welche sich in der Lobby befinden
     *
     * @param nutzername Nutzername des hinzuzufuegenden Nutzers
     * @param portNr     Port Nummer des hinzuzufuegenden Nutzers
     * @param ip         IP des hinzuzufuegenden Nutzers
     */
    void addLobbyNutzer(String nutzername, int portNr, InetAddress ip) {
        this.lobbyNutzer.add(new Nutzer(nutzername, ip, portNr));
    }

    /**
     * Loescht Nutzer aus Liste der angemeldeten Nutzer
     *
     * @param nutzername Nutzername des zu loeschenden Benutzers
     */
    void removeNutzer(String nutzername) {
        this.angemeldeteNutzer.removeIf(n -> n.name.equals(nutzername));
    }

    /**
     * Loescht Nutzer aus Liste der Nutzer, welche sich in der Lobby befinden
     *
     * @param nutzername Nutzername des zulöschenden Benutzers
     */
    void removeLobbyNutzer(String nutzername) {
        this.lobbyNutzer.removeIf(n -> n.name.equals(nutzername));
    }

    /**
     * Fuegt Spieler in Spielraum ein
     *
     * @param raumname   Raumname des Spielraums
     * @param nutzername Nutzername des Spielers
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void joinSpielraum(String raumname, String nutzername) throws RemoteException {
        SpielraumImpl spielraumimpl = null;

        for (SpielraumImpl raum : this.spielraeumeImpl) {
            if (raum.getName().equals(raumname)) {
                spielraumimpl = raum;
            }
        }
        if (spielraumimpl == null) {
            throw new Exceptions.SpielraumBeitreten.RaumnichtVorhandenException();
        } else {
            if (spielraumimpl.getSize() <= spielraumimpl.getPlayerlist().size()) {
                throw new Exceptions.SpielraumBeitreten.RaumVollException();
            } else if (spielraumimpl.getSpielLaufend()) {
                throw new Exceptions.SpielraumBeitreten.SpielgestartetException();
            } else {
                //Nutzer finden
                Nutzer nutzer = null;
                for (Nutzer n : lobbyNutzer) {
                    if (n.name.equals(nutzername)) {
                        nutzer = n;
                        break;
                    }
                }
                //Spieler aus lobbyNutzer entfernen
                this.lobbyNutzer.remove(nutzer);
                //Spieler in Spielraum bewegen
                spielraumimpl.join(nutzer);
            }
        }
    }

    /**
     * An den Server gesendete Nachricht wird mit Benutzername des Senders in der Lobby, der sich dort befindeten Nutzer angezeigt
     *
     * @param nutzername Name des Nutzers, der die Nachricht gesendet hat.
     * @param message    Nachricht des Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void sendChatmessage(String nutzername, String message) throws RemoteException {
        //Client updaten
        for (Nutzer n : lobbyNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Lobby clientlobby = (ClientRMIInterfaces.Lobby) registry.lookup("Lobby");
                clientlobby.updateLobbyChat(nutzername, message);
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gibt von MySQL Server abgefragte Bestenliste zurueck
     *
     * @return Bestenliste.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws IOException     Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException    Bei Fehlern bezueglich der Datenbank.
     */
    @Override
    public Map<String, Integer> getBestenliste() throws IOException, SQLException, RemoteException {
        //Mit MySQL Server verbinden
        Connection mysql = getMysqlConnection();
        //Connection mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/lama", "lamaAdmin", "lamaAdminPasswort");
        Statement stm = mysql.createStatement();
        //Alle Datensätze mit Benutzername = nutzername und Passwort = pw abfragen
        ResultSet rs = stm.executeQuery("SELECT `benutzername`,`punktestand` FROM `user` ORDER BY `punktestand` DESC");
        //ResultSet to List
        Map<String, Integer> bestenliste = new TreeMap<>();
        while (rs.next()) {
            String nutzer = rs.getString("benutzername");
            int punkte = rs.getInt("punktestand");

            bestenliste.put(nutzer, punkte);
        }
        //MySQL Verbindung beenden
        mysql.close();
        return bestenliste;
    }

    /**
     * Gibt Spielraumliste zurueck
     *
     * @return Liste der Namen der Spielraeume.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public List<String> getSpielraumNamen() throws RemoteException {
        return spielraeume;
    }

    /**
     * Benutzerbestenliste wird aus der Datenbank abgefragt und an Clients gesendet
     */
    void updateBestenliste() throws SQLException {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten
        for (Nutzer n : lobbyNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Lobby clientlobby = (ClientRMIInterfaces.Lobby) registry.lookup("Lobby");
                clientlobby.updateBestenliste(this.getBestenliste());
            } catch (IOException | NotBoundException e) {
                //Client hat die Verbindung verloren
                disconnectedNutzer.add(n);
                //e.printStackTrace();
            }
        }
        playerDisconnect(disconnectedNutzer);
    }

    /**
     * Spielraumliste wird an Clients gesendet
     */
    void updateSpielraeume() {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten
        for (Nutzer n : lobbyNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Lobby clientlobby = (ClientRMIInterfaces.Lobby) registry.lookup("Lobby");
                clientlobby.updateSpielraeume(this.getSpielraumNamen());
            } catch (IOException | NotBoundException e) {
                //Client hat die Verbindung verloren
                disconnectedNutzer.add(n);
                //e.printStackTrace();
            }
        }
        playerDisconnect(disconnectedNutzer);
    }


    /**
     * Meldet Benutzer, welche die Verbindung verloren haben, ab.
     *
     * @param nutzerlist Nutzer mit verbindungsverlust
     */
    void playerDisconnect(List<Nutzer> nutzerlist) {
        try {
            for (Nutzer n : nutzerlist) {
                //Client hat die Verbindung verloren
                //Nutzer aus Lobby entfernen
                this.removeNutzer(n.name);
                this.removeLobbyNutzer(n.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Benutzerbestenliste wird aus der Datenbank abgefragt und an Clients gesendet(außer fuer neu registrierten Nutzer,
     * diese bekommt die aktuelle Bestenliste sowieso über die getter Methode)
     *
     * @param name Benutzername des neu registrierten Nutzers
     */
    void updateBestenlisteRegister(String name) throws SQLException {
        //Client updaten
        for (Nutzer n : lobbyNutzer) {
            try {
                if (!n.name.equals(name)) {
                    Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);

                    ClientRMIInterfaces.Lobby clientlobby = (ClientRMIInterfaces.Lobby) registry.lookup("Lobby");
                    clientlobby.updateBestenliste(this.getBestenliste());
                }
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * entfernt den Spielraum aus den Listen
     *
     * @param name des zu entfernenden Spielraums
     */
    void removeSpielraum(String name) {
        spielraeume.removeIf(sr -> sr.equals(name));
        try {
            int i = 0;
            for (; i < spielraeumeImpl.size(); i++) {
                if (spielraeumeImpl.get(i).getName().equals(name)) {
                    break;
                }
            }
            spielraeumeImpl.remove(i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ersetzt oldName in der Spielraeume Liste durch newName
     *
     * @param oldName Vorheriger Name des Spielraumes.
     * @param newName Jetziger Name des Spielraumes.
     * @
     */
    void changeSpielraumName(String oldName, String newName, SpielraumImpl sr) {
        int oldNameIndex = spielraeume.indexOf(oldName);
        spielraeume.add(oldNameIndex, newName);
        spielraeume.remove(oldName);
        RMIServer.changeSpielraumServiceName(oldName, newName, sr);
    }

    // nur für die Testfälle
    List<SpielraumImpl> getSpielraumImplListe() {
        return spielraeumeImpl;
    }

    // nur für die Testfälle
    void addSpielraumName(String name) {
        this.spielraeume.add(name);
    }
}
