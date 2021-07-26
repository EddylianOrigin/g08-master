package ServerRMI;

import Exceptions.KartenWahlException;
import Exceptions.SpielraumAendern.MissingInputException;
import Exceptions.SpielraumAendern.RaumnameVergebenException;
import Exceptions.SpielraumAendern.SpielerAnzahlException;
import ServerRMIInterfaces.Spielraum;
import SpielRaumVerwaltung.Karte;
import Spieler.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ServerRMI.RMIServer.getMysqlConnection;

/**
 * Verwaltet den Spielraum.
 */
public class SpielraumImpl implements Spielraum {
    private String host;
    private String name;
    private int size;
    private final List<Spieler> spieler = new ArrayList<>();
    private final Stack<Karte> ablageStapel = new Stack<>();
    private Stack<Karte> nachziehstapelStapel;
    private int amZug;
    private Boolean spielLaufend = false;
    private final List<Nutzer> spielraumNutzer = new ArrayList<>();
    private final LobbyImpl lobby;

    public SpielraumImpl(String name, int size, Nutzer host, LobbyImpl lobby) {
        this.name = name;
        this.size = size;
        this.host = host.name;
        this.lobby = lobby;
        spielraumNutzer.add(host);
        spieler.add(new Benutzer(host.name));
    }

    /**
     * Gibt zurueck, ob das Spiel gerade laeuft.
     * @return True, falls das spiel aktuell laeuft, sonst false
     * @throws RemoteException           bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public Boolean getSpielLaufend() throws RemoteException {
        return spielLaufend;
    }

    /**
     * Aendert den Namen des Raumes und/oder die Groesse dessen.
     *
     * @param roomName neuer Raumname.
     * @param size     neue Raumgroesse.
     * @throws RemoteException           bei Problemen mit der Kommuntikation zum Client.
     * @throws MissingInputException     wird geworfen, falls roomName null oder "" ist.
     * @throws RaumnameVergebenException wird geworfen, falls ein anderer Raum bereits den neue festgelegten Raumnamen besitzt.
     * @throws SpielerAnzahlException    wird geworfen, falls die Raumgroesse nicht zwischen 2 und 6 liegt,
     *                                   oder falls bereits mehr Spieler im Raum sind, als die neue Raumgroesse es erlaubt.
     */
    @Override
    public void change(String roomName, int size) throws RemoteException, MissingInputException,
            RaumnameVergebenException, SpielerAnzahlException {
        if (roomName == null || roomName.equals("")) {
            throw new MissingInputException();
        } else if (size < 2 || size > 6 || size < spieler.size()) {
            throw new SpielerAnzahlException();
        } else if (!roomName.equals(this.name) && lobby.spielraumNameVorhanden(roomName)) {
            throw new RaumnameVergebenException();
        } else {
            lobby.changeSpielraumName(this.name,roomName,this);
            this.name = roomName;
            this.size = size;

            updateSpielraum();
        }
    }

    /**
     * Entfernt den Spielraum-verlassenden Nutzer aus der Nutzerliste und der SPielerliste und informiert die anderen Nutzer im Spielraum über die Aenderung.
     *
     * @param nutzername Name des Spielraum-verlassenden Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void leave(String nutzername) throws RemoteException {
        //Falls der Host das Spiel verlässt, wird der Raum gelöscht
        if (nutzername.equals(host)) {
            delete();
        } else{
            //Nutzer suchen
            Nutzer nutzer = null;
            for(Nutzer n : spielraumNutzer){
                if(nutzername.equals(n.name)){
                    nutzer = n;
                }
            }
            //Nutzer/Speiler entfernen
            spielraumNutzer.removeIf(n -> n.name.equals(nutzername));
            spieler.removeIf(s -> s.getName().equals(nutzername));
            //Nutzer wieder in die LobbyListe einfügen
            lobby.addLobbyNutzer(nutzername,nutzer.portNr,nutzer.ip);
            updateSpielraum();
        }
    }

    /**
     * Der Spielraum wird geloescht, alle Nutzer kehren in die Lobby zurueck.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void delete() throws RemoteException {
        //Spielraum aus Liste der Lobby entfernen
        lobby.removeSpielraum(name);
        //Spielraum löschen und alle Benutzer in die Lobby zurück bringen
        for (Nutzer n : this.spielraumNutzer) {
            lobby.addLobbyNutzer(n.name, n.portNr, n.ip);
        }
        RMIServer.unbindSpielraumService(SpielraumImpl.this.getName());
        //Benutzer wieder in die Lobby zurueck bringen (außer Host, dieser ist sowieso schon wieder in der Lobby)
        updateSpielraumGeloescht();
    }

    /**
     * Der Spieler, welcher aktuell am Zug ist steigt aus dem Durchgang aus.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void aussteigen() throws RemoteException {
        //unschöne Lösung aber behebt das Problem, welches durch die synchronität von RMI entsteht (wurde am Anfang nicht bedacht)
        Runnable r1 = () -> {
            Spieler s = spieler.get(amZug);
            s.setAusgestiegen(true);

            //Spieler der den Letzten Durchgang beendet hat beginnt den nächsten
            if(checkDurchgangBeendet()){
                durchgangBeendet();
            }
            else{
                changeTurn();
            }
        };
        /*
        Spieler s = spieler.get(amZug);
        s.setAusgestiegen(true);

        //Spieler der den Letzten Durchgang beendet hat beginnt den nächsten
        if(checkDurchgangBeendet()){
            durchgangBeendet();
        }
        else{
            changeTurn();
        }
*/
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(r1);
    }

    /**
     * Der Spieler legt eine Karte auf den Ablagestapel.
     *
     * @param kartenIndex Index der zu entfernenden Karte in der Liste.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     * @throws KartenWahlException wird geworfen, falls die gewählte Karte nicht nach Regelwerk ablegbar ist.
     */
    @Override
    public void ablegen(int kartenIndex) throws RemoteException, KartenWahlException {

        //unschöne Lösung aber behebt das Problem, welches durch die synchronität von RMI entsteht (wurde am Anfang nicht bedacht)
        Karte topCard = ablageStapel.peek();
        Karte card = spieler.get(amZug).getCards().get(kartenIndex);
        if (card.canBePlacedOnTopOf(topCard)) {
            Runnable r1 = () -> {
                ablageStapel.push(card);

                //Karte muss noch aus der Hand des Spielers entfernt werden
                spieler.get(amZug).removeCard(card);

                //Spieler der den Letzten Durchgang beendet hat beginnt den nächsten
                if(checkDurchgangBeendet()){
                    durchgangBeendet();
                }
                else{
                    changeTurn();
                }
            };
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(r1);
        } else {
            throw new KartenWahlException();
        }

        /*
        Karte topCard = ablageStapel.peek();
        Karte card = spieler.get(amZug).getCards().get(kartenIndex);
        if (card.canBePlacedOnTopOf(topCard)) {
            ablageStapel.push(card);

            //Karte muss noch aus der Hand des Spielers entfernt werden
            spieler.get(amZug).removeCard(card);

            //Spieler der den Letzten Durchgang beendet hat beginnt den nächsten
            if(checkDurchgangBeendet()){
                durchgangBeendet();
            }
            else{
                changeTurn();
            }
        } else {
            throw new KartenWahlException();
        }
        */

    }

    /**
     * Erzeugt die Bestenliste fuer die meschlichen Spieler im Raum.
     * @return Bestenliste.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public Map<String, Integer> getBestenliste() throws RemoteException {

        Map<String, Integer> bestenliste = new TreeMap<>();

        //besorge die Spielernamen und die gewonnen Spiele und füge sie in die Map ein.
        for(Spieler s : spieler){

            bestenliste.put(s.getName(), s.getGewonneneSpiele());

        }
        return  bestenliste;
    }


    /**
     * Eine Karte wird aus dem Nachziehstapel genommen (und entfernt) und in die Hand des Spielers eingefuegt.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void aufnehmen() throws RemoteException {
        //unschöne Lösung aber behebt das Problem, welches durch die synchronität von RMI entsteht (wurde am Anfang nicht bedacht)
        Runnable r1 = () -> {
            if (!nachziehstapelStapel.isEmpty()) {
                Karte k = nachziehstapelStapel.pop();
                Spieler s = spieler.get(amZug);
                s.addCard(k);

                //Spieler der den Letzten Durchgang beendet hat beginnt den nächsten
                if(checkDurchgangBeendet()){
                    durchgangBeendet();
                }
                else{
                    changeTurn();
                }
            }
        };
        /*
        if (!nachziehstapelStapel.isEmpty()) {
            Karte k = nachziehstapelStapel.pop();
            Spieler s = spieler.get(amZug);
            s.addCard(k);

            //Spieler der den Letzten Durchgang beendet hat beginnt den nächsten
            if(checkDurchgangBeendet()){
                durchgangBeendet();
            }
            else{
                changeTurn();
            }
        }
        */
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(r1);
    }

    /**
     * Startet das Spiel, ein zufaelliger Spieler begint.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void start() throws RemoteException {
        spielLaufend=true;
        updateSpielgestartet();
        stapelErstellen();
        stapelMischen();
        kartenVerteilen();


        //unschöne Lösung aber behebt das Problem, welches durch die synchronität von RMI entsteht (wurde am Anfang nicht bedacht)
        Runnable r1 = () -> {
            //ein zufälliger Spieler startet
            //random number generator erstellen und nutzen
            Random rng = new Random();
            amZug = rng.nextInt(spieler.size());
            updateSpielStatus();
            //Falls ein Bot am Zug ist muss dieser ein Zug machen
            if(spieler.get(amZug) instanceof Bot){
                ((Bot) spieler.get(amZug)).play();
            }
        };

       /*
        //ein zufälliger Spieler startet
        //random number generator erstellen und nutzen
        Random rng = new Random();
        amZug = rng.nextInt(spieler.size());
        //Falls ein Bot am Zug ist muss dieser ein Zug machen
        if(spieler.get(amZug) instanceof Bot){
            ((Bot) spieler.get(amZug)).play();
        }
        */
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(r1);
    }

    /**
     * An den Server gesendete Nachricht wird mit Benutzername des Senders in dem Spielraum, der sich dort befindeten Nutzer angezeigt
     *
     * @param nutzername Name des Nutzers, der die Nachricht gesendet hat.
     * @param message    Nachricht des Nutzers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void sendChatMessage(String nutzername, String message) throws RemoteException {
        //Client updaten
        for (Nutzer n : spielraumNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Spielraum clientSR = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                clientSR.updateRaumChat(nutzername, message);
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gibt die Liste von Spielern im Spielraum zurueck.
     * @return Liste von Spielern im Spielraum.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    public List<Spieler> getPlayerlist() throws RemoteException {
        return spieler;
    }

    /**
     * Gibt die Karten auf der Hand des Spielers mit dem Namen spielerName zurueck.
     * @param spielerName Name des Spieler.
     * @return Liste der Karten, die der Spieler auf der Hand hat.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public List<Karte> getPlayercards(String spielerName) throws RemoteException {
        for(Spieler s : spieler){
            if(s.getName().equals(spielerName)){
                return s.getCards();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gibt die Raumgroesse zurueck.
     *
     * @return maximal Spieleranzahl des Spielraumes.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public int getSize() throws RemoteException {
        return size;
    }

    /**
     * Gibt den Raumname zurueck.
     *
     * @return Name des Raumes.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public String getName() throws RemoteException {
        return this.name;
    }


    /**
     * Fuegt dem Spielraum einen Bot des normalen Schwierigkeitsgrades hinzu.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void addBotNormal() throws RemoteException {
        //Bots des Schwierigkeitsgrades zählen und Namen entsprechend anpassen
        int i = 1;
        for(Spieler s: spieler){
            if(s instanceof Bot_normal){
                i++;
            }
        }
        if (spieler.size() < size) {
            Bot_normal bot = new Bot_normal(this);
            bot.setName(bot.getName()+" "+i);
            spieler.add(bot);

            updateSpielraum();
        }
    }

    /**
     * Fuegt dem Spielraum einen Bot des schweren Schwierigkeitsgrades hinzu.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void addBotSchwer() throws RemoteException {
        //Bots des Schwierigkeitsgrades zählen und Namen entsprechend anpassen
        int i = 1;
        for(Spieler s: spieler){
            if(s instanceof Bot_schwer){
                i++;
            }
        }
        if (spieler.size() < size) {
            Bot_schwer bot = new Bot_schwer(this);
            bot.setName(bot.getName()+" "+i);
            spieler.add(bot);
            updateSpielraum();
        }
    }

    /**
     * entfernt einen Bot des normalen Schwierigkeitsgrades aus dem Raum, insofern dieser existiert.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void removeBotNormal() throws RemoteException {
        //Liste von hinten durchgehen, dann werden weniger Spieler auf andere Plaetze verschoben
        for (int i = spieler.size() - 1; i >= 0; i--) {
            if (spieler.get(i) instanceof Bot_normal) {
                spieler.remove(i);
                break;
            }
        }
        updateSpielraum();
    }

    /**
     * entfernt einen Bot des schweren Schwierigkeitsgrades aus dem Raum, insofern dieser existiert.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void removeBotSchwer() throws RemoteException {
        //Liste von hinten durchgehen, dann werden weniger Spieler auf andere Plaetze verschoben
        for (int i = spieler.size() - 1; i >= 0; i--) {
            if (spieler.get(i) instanceof Bot_schwer) {
                spieler.remove(i);
                break;
            }
        }
        updateSpielraum();
    }

    /**
     * gibt eine Liste der Namen der Nutzer im Raum zurueck.
     *
     * @return Liste der Namen der Nutzer im Raum
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public List<String> getPlayerNamelist() throws RemoteException {
        List<String> playerlist = new ArrayList<>();
        for (Spieler s : this.spieler) {
            playerlist.add(s.getName());
        }
        return playerlist;
    }

    /**
     * Gibt den Namen des Hosts des Spielraumes zurueck.
     *
     * @return Name des Spielraumverwalters.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public String getHost() throws RemoteException {
        //return host.getName();
        return host;
    }

    /**
     * Legt den Host des Spielraumes fest.
     *
     * @param hostIn Spielraumverwalter.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    @Override
    public void setHost(String hostIn) throws RemoteException {
        this.host = hostIn;
    }

    /**
     * Fügt den beigetretenen Nutzer in die Nutzerliste und Spielerliste ein und informiert die anderen Nutzer im Spielraum über die Änderung.
     *
     * @param nutzer beitretender Nutzer.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    void join(Nutzer nutzer) throws RemoteException {
        spielraumNutzer.add(nutzer);
        spieler.add(new Benutzer(nutzer.name));
        updateSpielraumJoin(nutzer.name);
    }

    /**
     * Alle Spieler kassieren Chips, nach den Regeln im Regelwerk.
     */
    private void chipsKassieren() {
        for(Spieler s : spieler){
            s.chipskassieren();
        }
    }

    /**
     * Der Nachziehstapel wird erstellt, in dem die Werte 1-6 und 10 acht mal im Nachziehstapel hinzugefuegt werden.
     * Kartenwert 10 repraesentiert die LAMA Karte
     */
    public void stapelErstellen() {
        this.nachziehstapelStapel = new Stack<>();
        for (int j = 0; j < 8; j++) {
            for (int i = 1; i < 7; i++) {
                this.nachziehstapelStapel.add(new Karte(i));
            }
            this.nachziehstapelStapel.add(new Karte(10));
        }
    }

    /**
     * Der Nachziehstapel wird gemischt
     */
    public void stapelMischen() {
        Collections.shuffle(nachziehstapelStapel);
    }

    /**
     * Jeden Spieler im Spielraum werden 6 Karten, die aus dem Nachziehstapel genommen (und entfernt) werden,  ausgeteilt.
     */
    public void kartenVerteilen() {
        for (Spieler p : this.spieler) {
            for (int i = 0; i < 6; i++) {
                Karte k = nachziehstapelStapel.pop();
                p.getCards().add(k);
            }
        }
        //Karte auf den Ablagestapel legen:
        ablageStapel.push(nachziehstapelStapel.pop());
    }

    /**
     * Raumgroesse und Namen der Spieler im Raum werden an client uebergeben.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    private void updateSpielraum() throws RemoteException {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten
        for (Nutzer n : spielraumNutzer) {
            try {
                //Spielraum updaten
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                clientsr.updateSpielraum(size, getPlayerNamelist(),name);

            } catch (IOException | NotBoundException e) {
                //Client hat die Verbindung verloren
                disconnectedNutzer.add(n);
            }

            //Anzeige in Lobby für die Nutzer in der Lobby updaten
            lobby.updateSpielraeume();

        }
        playerDisconnect(disconnectedNutzer);
    }
    /**
     * Raumgroesse und Namen der Spieler im Raum werden an client uebergeben
     * (außer beim Spieler, der gerade neu beigetreten ist, dieser holt sich sowieso ein update vom Server).
     *
     * @param name Name des neu beigetretenen Spielers.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    private void updateSpielraumJoin(String name) throws RemoteException {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten, außer beim Spieler, der gerade neu beigetreten ist.
        for (Nutzer n : spielraumNutzer) {
            if(!n.name.equals(name)) {
                try {
                    //Spielraum updaten
                    Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                    ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                    clientsr.updateSpielraum(size, getPlayerNamelist(),this.name);

                    //Anzeige in Lobby updaten
                    lobby.updateSpielraeume();
                } catch (IOException | NotBoundException e) {
                    //Client hat die Verbindung verloren
                    disconnectedNutzer.add(n);
                }
            }
        }
        playerDisconnect(disconnectedNutzer);
    }

    /**
     * Informationen ueber das laufende Spiel an client uebergeben.
     */
    public void updateSpielStatus() {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten
        for (Nutzer n : spielraumNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");

                //Karten des Nutzers n
                List<Karte> eigeneHand = new ArrayList<>();
                // Anzahl der Karten der Spieler
                int[] kartenAnzahl_Spieler = new int[spieler.size()];
                //Anzahl weiße Chips der Spieler
                int[] weißeChips_Spieler = new int[spieler.size()];
                // Anzahl schwarze Chips der Spieler
                int[] schwarzeChips_Spieler = new int[spieler.size()];
                boolean[] ausgestiegen = new boolean[spieler.size()];

                //entsprechende Daten heraussuchen
                for (int i = 0; i < spieler.size(); i++) {
                    if (spieler.get(i).getName().equals(n.name)) {
                        eigeneHand = spieler.get(i).getCards();
                    }
                    kartenAnzahl_Spieler[i] = spieler.get(i).getCards().size();
                    weißeChips_Spieler[i] = spieler.get(i).getWeißeChips();
                    schwarzeChips_Spieler[i] = spieler.get(i).getSchwarzeChips();
                    ausgestiegen[i] = spieler.get(i).getAusgestiegen();
                }
                Karte ablageStapelKarte = null;
                if (!ablageStapel.isEmpty()) {
                    ablageStapelKarte = ablageStapel.peek();
                }
                clientsr.updateSpielStatus(amZug, eigeneHand, kartenAnzahl_Spieler,
                        weißeChips_Spieler, schwarzeChips_Spieler, ablageStapelKarte, nachziehstapelStapel.size(), ausgestiegen
                );
            } catch (RemoteException | NotBoundException e) {
                //Client hat die Verbindung verloren
                disconnectedNutzer.add(n);
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
                this.lobby.removeNutzer(n.name);
                this.lobby.removeLobbyNutzer(n.name);
                this.spielraumNutzer.remove(n);
                if (n.name.equals(host)) {
                    this.delete();
                } else {
                    //Nutzer in Spielraum durch Bot_normal ersetzen
                    //Neuen Bot erstellen
                    int i = 1;
                    for (Spieler s : spieler) {
                        if (s instanceof Bot_normal) {
                            i++;
                        }
                    }
                    Bot_normal bot = new Bot_normal(this);
                    bot.setName(bot.getName() + " " + i);
                    //Spielerdaten übernehmen
                    for (Spieler s : this.spieler) {
                        if (s.getName().equals(n.name)) {
                            bot.copySpielerInfo(s);
                            spieler.set(spieler.indexOf(s), bot);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Signalisiert den Clients, dass das Spiel gestartet wurde.
     *
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Client.
     */
    private void updateSpielgestartet() throws RemoteException {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten
        for (Nutzer n : spielraumNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                clientsr.updateGestartet();
            } catch (IOException | NotBoundException e) {
                //Client hat die Verbindung verloren
                disconnectedNutzer.add(n);
            }
        }
        playerDisconnect(disconnectedNutzer);
    }

    /**
     * Signalisiert den Clients, dass das Spiel beendet wurde und uebergibt den Namen des Siegers.
     */
    private void updateSpielBeendet(List<String> sieger) {
        List<Nutzer> disconnectedNutzer = new ArrayList<>();

        //Client updaten
        for (Nutzer n : spielraumNutzer) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                clientsr.updateBeendet(sieger);
            } catch (IOException | NotBoundException e) {
                //Client hat die Verbindung verloren
                disconnectedNutzer.add(n);
            }
        }
        playerDisconnect(disconnectedNutzer);
    }

    /**
     * Signalisiert den Clients(außer dem Host), dass der Spielraum geloescht wurde.
     */
    private void updateSpielraumGeloescht(){
        List<Nutzer> disconnectedNutzer = new ArrayList<>();
        //Client updaten
        for (Nutzer n : spielraumNutzer) {
            if(!n.name.equals(host)){
                try {
                    Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                    ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                    clientsr.updateGeloescht();
                } catch (IOException | NotBoundException e) {
                    //Client hat die Verbindung verloren
                    disconnectedNutzer.add(n);
                }
            }
        }
        playerDisconnect(disconnectedNutzer);
        //informiere Clients in der Lobby,
        // dass der Spielraum nicht mehr existiert
        // und informiere die Spieler im Spielraum, darüber,
        // dass der Spielraum gelöscht wurde.
        lobby.updateSpielraeume();
    }

    /**
     * Wechselt den Zug zum naechsten Spieler im Raum und fuehrt die Spielzuege der Bots aus.
     */
    private void changeTurn() {
//        int spielerAnzahl = spieler.size();

//        amZug = (amZug + 1) % spielerAnzahl;
        amZug = getNextPlayer(amZug);

        //falls der neue Spieler bereits aus dem Durchgang ausgestiegen ist, wähle den nächste Spieler
        while (spieler.get(amZug).getAusgestiegen()) {
//            amZug = (amZug + 1) % spielerAnzahl;
            amZug = getNextPlayer(amZug);
        }

        updateSpielStatus();

        //Bots machen ihren Zug
        if(spieler.get(amZug) instanceof Bot) {
            //Verzögerung für Spielzüge der Bots:
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((Bot) spieler.get(amZug)).play();

        }
    }

    /**
     * Gibt den nächsten Spieler aus, der an der Reihe ist.
     * @param amZug index des Spielers, der aktuell am Zug ist.
     */
    private int getNextPlayer(int amZug){
        if(spieler.size()==6){
            switch (amZug) {
                case 0 -> {
                    amZug = 5;
                }
                case 1 -> {
                    amZug = 2;
                }
                case 2 -> {
                    amZug = 4;
                }
                case 3 -> {
                    amZug = 1;
                }
                case 4 -> {
                    amZug = 0;
                }
                case 5 -> {
                    amZug = 3;
                }
            }
        }
        else if (spieler.size()==5){
            switch (amZug) {
                case 0 -> {
                    amZug = 3;
                }
                case 1 -> {
                    amZug = 2;
                }
                case 2 -> {
                    amZug = 4;
                }
                case 3 -> {
                    amZug = 1;
                }
                case 4 -> {
                    amZug = 0;
                }
            }
        }
        else if (spieler.size()==4) {
            switch (amZug) {
                case 0 -> {
                    amZug = 3;
                }
                case 1 -> {
                    amZug = 2;
                }
                case 2 -> {
                    amZug = 0;
                }
                case 3 -> {
                    amZug = 1;
                }
            }
        }
        else if (spieler.size()==3) {
            switch (amZug) {
                case 0 -> {
                    amZug = 1;
                }
                case 1 -> {
                    amZug = 2;
                }
                case 2 -> {
                    amZug = 0;
                }
            }
        }
        else if (spieler.size()==2) {
            switch (amZug) {
                case 0 -> {
                    amZug = 1;
                }
                case 1 -> {
                    amZug = 0;
                }
            }
        }
        return amZug;
    }


    /**
     * Ueberprueft ob der aktuelle Durchgang, oder sogar das Spiel beendet ist
     */
    private boolean checkDurchgangBeendet(){
        int ausgestiegen = 0;

        for(Spieler s : spieler){
            if (s.getAusgestiegen()){
                ausgestiegen++;
            }

        }
        return (spieler.get(amZug).getCards().isEmpty() || ausgestiegen==spieler.size());
    }

    /**
     * Stellt den Raum auf den Beendeten Durchgang, oder sogar das beendete Spiel ein.
     */
    private void durchgangBeendet(){
        chipsKassieren();
        //Spieler gibt Chip seiner Wahl ab, falls er keine Karten mehr hat und bereits chips hat
        for(Spieler s : spieler){
            if(s.getCards().isEmpty() && (s.getWeißeChips()!=0 || s.getSchwarzeChips()!=0)){
                boolean isBlack = true;
                if(s instanceof Bot){
                    isBlack = ((Bot) s).chipAbgeben();
                }
                else {
                    //Client informieren
                    for (Nutzer n : spielraumNutzer) {
                        if (s.getName().equals(n.name)) {
                            try {
                                Registry registry = LocateRegistry.getRegistry(n.ip.getHostAddress(), n.portNr);
                                ClientRMIInterfaces.Spielraum clientsr = (ClientRMIInterfaces.Spielraum) registry.lookup("Spielraum");
                                isBlack = clientsr.chipAbgeben(s.getWeißeChips()>0,s.getSchwarzeChips()>0);
                            } catch (IOException | NotBoundException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
                //entsprechenden Chip abgeben
                if(isBlack){
                    s.abgebenSchwarz();
                } else{
                    s.abgebenWeiß();
                }

                updateSpielStatus();

            }
        }

        //prüfen ob Spiel Beendet ist
        boolean spielBeendet = false;
        for(Spieler s : spieler) {
            if (s.getWeißeChips() + s.getSchwarzeChips() * 10 >= 40) {
                spielBeendet = true;
                break;
            }
        }

        //Karten der Spieler entfernen und
        //ausgestiegen für die Spieler zurücksetzen
        for(Spieler s : spieler){
            s.setAusgestiegen(false);
            s.clearCards();
        }

        if(spielBeendet){
            spielLaufend = false;
            // finde Sieger heraus:
            List<String> siegerNamen = new ArrayList<>();
            List<Spieler> sieger = new ArrayList<>();
            //initialisiere minPunkte mit dem ersten Spieler
            int minPunkte = spieler.get(0).getSchwarzeChips()*10 + spieler.get(0).getWeißeChips();
            //trage die Spieler mit der kleinsten Punktzahl in die Liste ein.
            for(Spieler s : spieler){
                int punkte = s.getSchwarzeChips()*10 +s.getWeißeChips();
                if(punkte == minPunkte){
                    siegerNamen.add(s.getName());
                    sieger.add(s);
                }
                if(punkte < minPunkte){
                    minPunkte=punkte;
                    sieger.clear();
                    siegerNamen.clear();
                    siegerNamen.add(s.getName());
                    sieger.add(s);
                }

            }

            //Siege updaten:
            for(Spieler s: sieger){
                //Siege im Raum für Spieler updaten:
                s.incrementGewonneneSpiele();

                //Siege in Datenbank updaten, falls es sich um menschliche Spieler handelt
                if(s instanceof Benutzer){
                    try {
                        incrementBenutzerPunktestand(s.getName());
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            updateSpielBeendet(siegerNamen);

            //chips der Spieler entfernen:
            for(Spieler s : spieler){
                s.clearChips();
            }
        } else {
            //neu Mischen und Karten verteilen
            stapelErstellen();
            stapelMischen();
            kartenVerteilen();

            //Clients updaten
            updateSpielStatus();


            //Falls ein Bot am Zug ist, macht dieser seinen Spielzug
            if(spieler.get(amZug) instanceof Bot){
                ((Bot)spieler.get(amZug)).play();
            }
        }
    }
    /**
     * Inkrementiere die Anzahl der Siege für den Benutzer in der Datenbank.
     *
     * @param nutzername Nutzername des Benutzers.
     * @throws SQLException
     * @throws IOException
     */
    private void incrementBenutzerPunktestand(String nutzername) throws SQLException, IOException {
        //Mit MySQL Server verbinden
        Connection mysql = getMysqlConnection();
        //Connection mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/lama", "lamaAdmin", "lamaAdminPasswort");
        Statement stm = mysql.createStatement();
        //Nutzerdaten (Benutzername, Passwort) einfügen
        stm.executeUpdate( "UPDATE user SET punktestand = punktestand + 1 WHERE benutzername = '"+nutzername+"'");
        //MySQL Verbindung beenden
        mysql.close();
    }

    /**
     * Gibt die oberste Karte des Ablagestapel aus.
     * @return oberste Karte des Ablagestapels
     */
    public Karte getAblagestapelKarte(){
        return ablageStapel.peek();
    }
    /**
     * Gibt aus, ob der Nachziehstapel leer ist.
     * @return true, falls der Nachziehstapel leer ist, sonst false.
     */
    public boolean isNachziehstapelEmpty(){
        return nachziehstapelStapel.empty();
    }

    /**
     * Legt fest welche Spieler am Zug ist.
     * @param i Spieler, welcher am Zug sein soll.
     */
    //nur zum Testen
    public void setAmZug(int i){
        amZug=i;
    }
    /**
     * Gibt aus, den Nachziehstapel des Raums.
     * @return Nachziehstapel.
     */
    public Stack<Karte> getNachziehstapel(){ return this.nachziehstapelStapel; }
}
