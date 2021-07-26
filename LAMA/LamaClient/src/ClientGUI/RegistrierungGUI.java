package ClientGUI;

import ClientRMI.RMIClient;
import Exceptions.Registrierung.MissingInputException;
import Exceptions.Registrierung.PasswortWdhException;
import Exceptions.Registrierung.RegistrierungnameException;
import ServerRMIInterfaces.Account;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * Das GUI-Fenster zum Registrieren.
 */
public class RegistrierungGUI implements GUIinit {

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
     * Bring den Nutzer in das Vorraum-interface zurueck.
     *
     * @param actionEvent repraesentiert das Druecken des abbrechen Knopfes.
     */
    public void abbrechenBtn(ActionEvent actionEvent) {

        try {
            GUIMain.changeScene(window, "VorraumGUI.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registriert den Spieler im System und bringt ihn in die Lobby.
     * <p>
     * Gibt einen Fehler aus, falls die Formularfelder nicht richtig ausgefuellt sind
     * oder der gewaehlte Name im System bereits existiert.
     *
     * @param actionEvent repraesentiert das Druecken des Account erstellen Knopfes.
     */
    public void accountErstellenBtn(ActionEvent actionEvent) {
        //Registrieren:

        //Benutzername und Passwort auslesen
        Scene scene = window.getScene();
        String nutzerName = ((TextField) scene.lookup("#nameFeld")).getText();
        String pw = ((TextField) scene.lookup("#pwFeld")).getText();
        String pwWdh = ((TextField) scene.lookup("#pwWdhFeld")).getText();

        //Prüfe ob die Passwörter in beiden Passwort Felder überein stimmen
        //if(!(nutzerName.equals("") || pw.equals("")|| pwWdh.equals(""))) {
        if (pw.equals(pwWdh)) {
            try {
                //RMI Registry suchen (ServerPort = 1099)
                Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());
                //Dienst suchen
                Account account = (Account) registry.lookup("Account");

                // eigene IP-Adresse finden
                InetAddress ip = InetAddress.getLocalHost();

                //Dienst nutzen
                account.register(nutzerName, pw, RMIClient.getRegistryPort(), ip);


                //Nutzername in Client speichern
                RMIClient.setNutzerName(nutzerName);
                //zum Lobby Interface Wechseln
                try {
                    GUIMain.changeScene(window, "LobbyGUI.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (NotBoundException | RemoteException e) {
                try {
                    //Szene wechseln und InformationsFenster oeffnen
                    GUIMain.changeScene(window, "VorraumGUI.fxml");
                    GUIMain.openDisconnectedWindow();
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            } catch (SQLException | IOException e) {
                GUIMain.openErrorWindow(new Exception("Server Fehler"));
            } catch (MissingInputException | RegistrierungnameException e) {
                GUIMain.openErrorWindow(e);
            }
        } else {
            try {
                throw new PasswortWdhException();
            } catch (PasswortWdhException e) {
                GUIMain.openErrorWindow(e);
            }
        }
    }
}
