package ClientRMI;

import ClientGUI.GUIMain;
import ClientGUI.LobbyGUI;
import ClientGUI.SpielraumGUI;
import ClientRMIInterfaces.Lobby;
import ClientRMIInterfaces.Spielraum;
import SpielRaumVerwaltung.Karte;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Stellt die RMI Methoden/Dienste zum Aktualisieren des Clients durch den Server bereit
 * und speichert den Nutzernamen, mit dem sich der Spieler anmeldet.
 */
public class RMIClient implements Lobby, Spielraum {

    //private static final int registryPort = 1098;
    //private static final int serverRegistryPort = 1099;
    //private static final String serverIP = "localhost";
    private static LobbyGUI lobby;
    private static SpielraumGUI spielraum;
    private static String nutzerName;
    //private static Registry registry;

    /**
     * Gibt den Nutzername des Spieler zurueck.
     * @return Nutzername des Spielers.
     */
    public static String getNutzerName() {
        return nutzerName;
    }

    public static void setNutzerName(String nutzer) {
        nutzerName = nutzer;
    }

    /**
     * Lama Client RMI Port wird aus client.config geladen
     *
     * @return Lama Client RMI Port
     * @throws IOException bei Problemen mit der Kommuntikation zum Server.
     */
    public static int getRegistryPort() throws IOException {
        //Konfiguration laden
        Properties prop = new Properties();
        String fileName = "client.config";
        InputStream is;
        is = new FileInputStream(fileName);
        prop.load(is);

        return Integer.parseInt(prop.getProperty("lama.client.port"));
        //return registryPort;
    }

    /**
     * Lama Server RMI Port wird aus client.config geladen
     *
     * @return Lama Server RMI Port
     * @throws IOException bei Problemen mit der Kommuntikation zum Server.
     */
    public static int getServerRegistryPort() throws IOException {
        //Konfiguration laden
        Properties prop = new Properties();
        String fileName = "client.config";
        InputStream is;
        is = new FileInputStream(fileName);
        prop.load(is);

        return Integer.parseInt(prop.getProperty("lama.server.port"));
        //return serverRegistryPort;
    }

    /**
     * Lama Server Hostname/IP wird aus client.config geladen
     *
     * @return Lama Server Hostname/IP
     * @throws IOException bei Problemen mit der Kommuntikation zum Server.
     */
    public static String getServerIP() throws IOException {
        //Konfiguration laden
        Properties prop = new Properties();
        String fileName = "client.config";
        InputStream is;
        is = new FileInputStream(fileName);
        prop.load(is);

        return prop.getProperty("lama.server.host");
        //return serverIP;
    }

    /**
     * Exportiert remote Object und registriert die Lobby RMI-Dienste, insofern diese bisher noch nicht existieren
     * und uebergibt ein LobbyGUI Objekt fuer das Updaten dieser.
     *
     * @param l Lobby, die durch die RMI-Aufrufe zu aktualisieren ist.
     */
    public static void createLobbyService(LobbyGUI l) {
        lobby = l;
        try {
            //das bereits in der main Methode erzeugte Registry suchen
            Registry registry = LocateRegistry.getRegistry("localhost", getRegistryPort());

            //erst schauen ob der Dienste bereits existiert, wenn nicht Dienste erzeugen

            try {
                registry.lookup("Lobby");
            } catch (NotBoundException e) {
                // Remote Object exportieren, Bereitstellen der Dienste
                RMIClient obj = new RMIClient();

                // aktuell mit anonymen Portnummern (2. Parameter = 0), eventuell später ändern
                Lobby stub1 = (Lobby) UnicastRemoteObject.exportObject(obj, 0);

                // Registrierung des Dienstes Lobby
                registry.bind("Lobby", stub1);
            }

        } catch (AlreadyBoundException | IOException e) {
            System.err.println("RMIClient exception:");
            e.printStackTrace();
        }
    }

    /**
     * Exportiert remote Object und registriert die Spielraum RMI-Dienste, insofern diese bisher noch nicht existieren
     * und uebergibt ein LobbyGUI Objekt fuer das Updaten dieser.
     *
     * @param sr Spielraum, der durch die RMI-Aufrufe zu aktualisieren ist.
     */
    public static void createSpielraumService(SpielraumGUI sr) {
        spielraum = sr;
        try {
            //das bereits in der main Methode erzeugte Registry suchen
            Registry registry = LocateRegistry.getRegistry("localhost", getRegistryPort());

            //erst schauen ob der Dienste bereits existiert, wenn nicht Dienste erzeugen
            try {
                registry.lookup("Spielraum");
            } catch (NotBoundException e) {
                // Remote Object exportieren, Bereitstellen der Dienste
                RMIClient obj = new RMIClient();

                // aktuell mit anonymen Portnummern (2. Parameter = 0), eventuell später ändern
                Spielraum stub2 = (Spielraum) UnicastRemoteObject.exportObject(obj, 0);

                // Registrierung des Dienstes Lobby
                registry.bind("Spielraum", stub2);
            }
        } catch (AlreadyBoundException | IOException e) {
            System.err.println("RMIClient exception:");
            e.printStackTrace();
        }
    }

    /**
     * Erzeugt das RMI Registry und startet die GUI.
     * @param args command line Argumente.
     */
    public static void main(String[] args) {
        try {

            //registry = LocateRegistry.createRegistry(registryPort);
            LocateRegistry.createRegistry(getRegistryPort());

        } catch (Exception e) {
            System.err.println("RMIClient exception:");
            e.printStackTrace();
        }

        //starte die GUI
        GUIMain.main();
    }

    /**
     * Gibt die Informationen des Servers ueber die Bestenliste an das Lobby Objekt weiter.
     *
     * @param bestenliste ordet Nutzernamen eine Punktzahl zu, muss nicht sortiert sein.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateBestenliste(Map<String, Integer> bestenliste) throws RemoteException{

        javafx.application.Platform.runLater(() -> lobby.updateBestenliste(bestenliste));
    }

    /**
     * Gibt die Informationen des Servers ueber die existierenden Spielraueme an das Lobyb Objekt weiter.
     *
     * @param spielraumNamen Liste an existierenden Spielraumennamen.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateSpielraeume(List<String> spielraumNamen) throws RemoteException{
        javafx.application.Platform.runLater(() -> lobby.updateSpielraueme(spielraumNamen));
    }

    /**
     * Gibt die Informationen des Servers ueber die neue Chat Nachricht an das Lobby Objekt weiter.
     *
     * @param nutzername Name der Person, die die Nachricht verfasst hat.
     * @param nachricht  Von einer Person verschickter Inhalt(Text).
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateLobbyChat(String nutzername, String nachricht) throws RemoteException {
        lobby.updateLobbyChat(nutzername, nachricht);
    }

    /**
     * Gibt die Informationen des Servers ueber den Spielraum an das SpielraumGUI Objekt weiter.
     * Aktualisiert die Raumstatus-Anzeige und die Spieler in der GUI.
     *
     * @param size    Eingestelle maximal Spieleranzahl.
     * @param spieler Liste von sich aktuell im Raum befindeten Spielern.
     * @param raumName (eventuell geaenderter) Name des Rauemes.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateSpielraum(int size, List<String> spieler, String raumName) throws RemoteException {
        //muss im javafx.application Thead aufgerufen werden, da die GUI Elemente angepasst werden
        javafx.application.Platform.runLater(() -> spielraum.updateSpielraum(size, spieler, raumName));
    }

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
    @Override
    public void updateSpielStatus(int amZug, List<Karte> eigeneHand, int[] kartenAnzahl_Spieler,
                                  int[] weißeChips_Spieler, int[] schwarzeChips_Spieler,
                                  Karte karte_Ablagestapel, int kartenAnzahl_Nachziehstapel, boolean[] ausgestiegen) throws RemoteException {
        //muss im javafx.application Thead aufgerufen werden, da die GUI Elemente angepasst werden
        javafx.application.Platform.runLater(() -> spielraum.updateSpielStatus(amZug, eigeneHand, kartenAnzahl_Spieler, weißeChips_Spieler, schwarzeChips_Spieler, karte_Ablagestapel, kartenAnzahl_Nachziehstapel, ausgestiegen));
    }

    /**
     * Gibt die Informationen des Servers, dass das Spiel am laufen ist an das SpielraumGUI Objekt weiter.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateGestartet() throws RemoteException {
        spielraum.updateGestartet();
    }

    /**
     * Gibt die Informationen des Servers, dass das Spiel am beendet ist an das SpielraumGUI Objekt weiter.
     *
     * @param sieger Spielername des Spielers der gewonnen hat.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateBeendet(List<String> sieger) throws RemoteException {
        //muss im javafx.application Thead aufgerufen werden, da die GUI Elemente angepasst werden
        javafx.application.Platform.runLater(() -> spielraum.updateBeendet(sieger));
    }

    /**
     * Gibt die Informationen des Servers ueber die neue Chat Nachricht an das SpielraumGUI Objekt weiter.
     *
     * @param nutzername Name der Person, die die Nachricht verfasst hat.
     * @param nachricht  Von einer Person verschickter Inhalt(Text).
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateRaumChat(String nutzername, String nachricht) throws RemoteException {
        spielraum.updateRaumChat(nutzername, nachricht);
    }

    /**
     * Gibt die Informationen des Servers, dass der Spieler einen Chip abgeben soll SpielraumGUI Objekt weiter.
     *
     * @param hasWChips gibt an, ob der Spieler weiße Chips besitzt.
     * @param hasBchips gibt an, ob der Spieler schwarze Chips bestizt.
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public boolean chipAbgeben(boolean hasWChips, boolean hasBchips) throws RemoteException {
        return spielraum.chipAbgeben(hasWChips, hasBchips);
    }

    /**
     * Gibt die Informationen des Servers, dass der Sielraum geloescht wurde an das SpielraumGUI Objekt weiter.
     * @throws RemoteException bei Problemen mit der Kommuntikation zum Server.
     */
    @Override
    public void updateGeloescht() throws RemoteException {
        //muss im javafx.application Thead aufgerufen werden, da Änderungen an der GUI vorgenommen werden
        javafx.application.Platform.runLater(() -> spielraum.updateGeloescht());
    }
}
