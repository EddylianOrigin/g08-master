package ServerRMI;

import ServerRMIInterfaces.Account;
import ServerRMIInterfaces.Lobby;
import ServerRMIInterfaces.Spielraum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Exportiert die Remote-Dienste Spielraum, Lobby und Account.
 */
public class RMIServer {

    //static private Registry registry = null;
    //static private Lobby stub1 = null;

    /**
     * Exportiert die Remote-Dienste Lobby und Account.
     * @param args command line Argumente.
     */
    public static void main(String[] args) {
        try {

            // MySQL Server Verbindung testen
            getMysqlConnection();

            // Remote Objects exportieren, Bereitstellen der Dienste
            // Kreierung von Servicethreads

            // aktuell mit anonymen Portnummern (2. Parameter = 0), eventuell später ändern
            LobbyImpl lobby = new LobbyImpl();
            Lobby stub1 = (Lobby) UnicastRemoteObject.exportObject(lobby, 0);

            Account stub2 = (Account) UnicastRemoteObject.exportObject(new AccountImpl(lobby), 0);

            //Konfiguration laden
            Properties prop = new Properties();
            String fileName = "server.config";
            InputStream is;
            is = new FileInputStream(fileName);
            prop.load(is);
            int port = Integer.parseInt(prop.getProperty("lama.server.port"));

            // Erzeugung des rmiRegistry auf dem lokalen Host
            Registry registry = LocateRegistry.createRegistry(port);

            // Registrierung der Dienste Lobby
            registry.bind("Lobby", stub1);

            // Registrierung der Dienste Account
            registry.bind("Account", stub2);


            System.out.println("Server gestartet");

        } catch (SQLException e) {
            System.err.println("MySQLServer exception:");
            e.printStackTrace();
        } catch (RemoteException | AlreadyBoundException e) {
            System.err.println("RMIServer exception:");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Konfig exception:");
            e.printStackTrace();
        }
    }

    /**
     * MySQL Konfiguration wird aus server.config geladen und eine Verbindung zum MySQL Server hergestellt
     *
     * @return Rueckgabe eines Connection Objekts, mit welchem die Interaktion mit dem MySQL Server möglich ist
     * @throws IOException Wenn die server.config Datein nicht gefunden wurde.
     * @throws SQLException Bei Fehlern bezueglich der Datenbank.
     */

    public static Connection getMysqlConnection() throws SQLException, IOException {

        //Konfiguration laden
        Properties prop = new Properties();
        String fileName = "server.config";
        InputStream is;
        is = new FileInputStream(fileName);
        prop.load(is);

        String mysqlUrl = "jdbc:mysql://" + prop.getProperty("lama.mysql.host") + ":" + prop.getProperty("lama.mysql.port") + "/" + prop.getProperty("lama.mysql.database");
        String mysqlUser = prop.getProperty("lama.mysql.user");
        String mysqlPass = prop.getProperty("lama.mysql.pass");

        //MySQL Server Verbindung zurückgeben
        return DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPass);
    }

    /**
     * Exportiert remote Object und registriert die Spielraum RMI-Dienste im Registry.
     * Der Dienst wird nach dem Namen des Spielraums benannt.
     *
     * @param sr Spielraum.
     */
    public static void createSpielraumService(SpielraumImpl sr) {
        try {
            //das bereits in der main Methode erzeugte Registry suchen
            Registry registry = LocateRegistry.getRegistry(1099);

            //aktuell mit anonymen Portnummern (2. Parameter = 0), eventuell später ändern
            Spielraum stub = (Spielraum) UnicastRemoteObject.exportObject(sr, 0);

            //Registrierung der Dienste Spielraum
            registry.rebind(sr.getName(), stub);

        } catch (RemoteException e) {
            System.err.println("RMIClient exception:");
            e.printStackTrace();
        }

    }

    /**
     * Entfernt die Spielraum RMI Dienste aus dem Registry.
     *
     * @param spielraumName Name des Spielraums, bzw. des Dienstes der gelöscht werden soll.
     */
    public static void unbindSpielraumService(String spielraumName) {
        try {
            //das bereits in der main Methode erzeugte Registry suchen
            Registry registry = LocateRegistry.getRegistry();

            registry.unbind(spielraumName);

        } catch (RemoteException | NotBoundException e) {
            System.err.println("RMIClient exception:");
            e.printStackTrace();
        }
    }

    /**
     * Entfernt die Spielraum RMI Dienste aus dem Registry und fuegt sie unter einem neuen Namen wieder ein.
     *
     * @param oldName Name des Dienstes vorher.
     * @param newName Name des Dienstes nachher.
     * @param sr      Spielraum.
     */
    public static void changeSpielraumServiceName(String oldName, String newName, SpielraumImpl sr) {
        try {
            //das bereits in der main Methode erzeugte Registry suchen
            Registry registry = LocateRegistry.getRegistry();

            registry.unbind(oldName);
            //sr wurde bereits exportiert
            registry.bind(newName, sr);

        } catch (RemoteException | NotBoundException | AlreadyBoundException e) {
            System.err.println("RMIClient exception:");
            e.printStackTrace();
        }
    }

}
