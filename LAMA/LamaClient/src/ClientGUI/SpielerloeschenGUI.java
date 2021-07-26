package ClientGUI;

import ClientRMI.RMIClient;
import Exceptions.SpielerLoeschen.KontoNichtAngemeldetException;
import Exceptions.SpielerLoeschen.MissingInputException;
import Exceptions.SpielerLoeschen.NameException;
import Exceptions.SpielerLoeschen.PasswortException;
import ServerRMIInterfaces.Account;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * Das GUI-Fenster zum Spielerloeschen
 */
public class SpielerloeschenGUI implements GUIinit {
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
     * Bringt den Nutzer in das Lobby-Interface.
     *
     * @param actionEvent repraesentiert das Druecken des abbrechnen Knopfes.
     */
    public void abbrechnenBtn(ActionEvent actionEvent) {

        try {
            GUIMain.changeScene(window, "LobbyGUI.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loescht den Nutzer und bring den Nutzer in das Vorraum-Interface.
     * Gibt einen Fehler aus, falls nicht alle Formularfelder ausgefüllt sind,
     * der Name nicht in der Datenbank existiert, oder das Passwort nicht richtig ist.
     *
     * @param actionEvent repraesentiert das Druecken des bestätigen Knopfes.
     */
    public void bestätigenBtn(ActionEvent actionEvent) {
        try {


            //Benutzername und Passwort auslesen
            Scene scene = window.getScene();
            String nutzerName = ((TextField) scene.lookup("#nameFeld")).getText();
            String pw = ((TextField) scene.lookup("#pwFeld")).getText();

            //RMI Registry suchen (ServerPort = 1099)
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());
            //Dienst suchen
            Account account = (Account) registry.lookup("Account");

            //Dienst nutzen
            account.delete(nutzerName, pw, RMIClient.getNutzerName());


            //Danach in der Vorraum wechseln
            GUIMain.changeScene(window, "VorraumGUI.fxml");

        } catch (NotBoundException | RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            GUIMain.openErrorWindow(new Exception("Server Fehler"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NameException | MissingInputException | PasswortException | KontoNichtAngemeldetException e) {
            GUIMain.openErrorWindow(e);
        }
    }
}


