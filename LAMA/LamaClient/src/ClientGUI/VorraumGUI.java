package ClientGUI;


import ClientRMI.RMIClient;
import Exceptions.Anmeldung.AnmeldenameException;
import Exceptions.Anmeldung.BereitsAngemeldetException;
import Exceptions.Anmeldung.MissingInputException;
import Exceptions.Anmeldung.PasswortException;
import ServerRMIInterfaces.Account;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * Das GUI-Fenster zum Anmelden.
 */
public class VorraumGUI implements GUIinit {
    private Stage window;

    /**
     * Uebergibt die Stage an das Objekt.
     *
     * @param window Stage/Fenster in dem sich der Nutzer befindet.
     */
    public void init(Stage window) {
        this.window = window;
    }

    /**
     * Bring den Nutzer in das Registrierungs-interface.
     *
     * @param actionEvent repraesentiert das Druecken des abbrechen Knopfes.
     */
    public void registrierungBtn(ActionEvent actionEvent) {

        try {
            //in die Lobby wechseln
            GUIMain.changeScene(window, "RegistrierungGUI.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Meldet den Spieler im System an und bringt ihn in die Lobby.
     * <p>
     * Gibt einen Fehler aus, falls nicht alle Formularfelder ausgefüllt sind,
     * der Name nicht in der Datenbank existiert, oder das Passwort nicht richtig ist.
     *
     * @param actionEvent repraesentiert das Druecken des Anmeldung Knopfes.
     */
    public void anmeldungBtn(ActionEvent actionEvent) {

        try {
            //Anmelden:

            //Benutzername und Passwort auslesen
            Scene scene = window.getScene();
            String nutzerName = ((TextField) scene.lookup("#nameFeld")).getText();
            String pw = ((TextField) scene.lookup("#pwFeld")).getText();

            //RMI Registry suchen (ServerPort = 1099)
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());
            //Dienst suchen
            Account account = (Account) registry.lookup("Account");

            // eigene IP-Adresse finden
            InetAddress ip = InetAddress.getLocalHost();

            //Dienst nutzen
            account.login(nutzerName, pw, RMIClient.getRegistryPort(), ip);

            //Nutzername in Client speichern
            RMIClient.setNutzerName(nutzerName);

            //Danach in die Lobby wechseln
            GUIMain.changeScene(window, "LobbyGUI.fxml");

        } catch (IOException | SQLException | NotBoundException e) {
            GUIMain.openErrorWindow(new Exception("Server Fehler"));
        } catch (AnmeldenameException | MissingInputException | PasswortException | BereitsAngemeldetException e) {
            GUIMain.openErrorWindow(e);
        }
    }
}
