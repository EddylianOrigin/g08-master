package ClientGUI;

import ClientRMI.RMIClient;
import Exceptions.SpielraumErstellen.MissingInputException;
import Exceptions.SpielraumErstellen.RaumnameVergebenException;
import Exceptions.SpielraumErstellen.SpielerAnzahlException;
import ServerRMIInterfaces.Lobby;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Das GUI-Fenster zum Spielraumerstellen
 */
public class SpielraumerstellenGUI implements GUIinit {
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
     * Bring den Nutzer in das Lobby-interface zurueck.
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
     * Bring den Nutzer in das Spielraum-interface.
     * Gibt einen Fehler aus, falls nicht alle Formularfelder ausgefüllt sind,
     * spieler anzahl nicht korrekt ist oder Raumname ist vergeben
     *
     * @param actionEvent repraesentiert das Druecken des bestaetigen Knopfes.
     */
    public void bestaetigenBtn(ActionEvent actionEvent) {
        try {
            //Raumname und spieleranzahl auslesen
            Scene scene = window.getScene();
            String roomName = ((TextField) scene.lookup("#raumnameFeld")).getText();
            try {
                int size = Integer.parseInt(((TextField) scene.lookup("#spieleranzahlFeld")).getText());


                //RMI Registry suchen (ServerPort = 1099)
                Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());
                //Dienst suchen
                Lobby lobby = (Lobby) registry.lookup("Lobby");

                //Dienst nutzen
                lobby.createSpielraum(roomName, size, RMIClient.getNutzerName());

                //Danach in der Spielraum wechseln
                //GUIMain.changeScene(window,"SpielraumGUI.fxml");
                GUIMain.changeSceneInitWithRaumname(window, "SpielraumGUI.fxml", roomName);

            } catch (NumberFormatException e) {
                //falls der Nutzer keine Zahl für die Raumgröße eingetragen hat, wird auch die SpielerAnzahlException geworfen
                throw new SpielerAnzahlException();
            }

        } catch (NotBoundException | RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MissingInputException | RaumnameVergebenException | SpielerAnzahlException e) {
            GUIMain.openErrorWindow(e);
        }
    }

}