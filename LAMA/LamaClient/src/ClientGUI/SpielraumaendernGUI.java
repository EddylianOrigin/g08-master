package ClientGUI;

import ClientRMI.RMIClient;
import Exceptions.Anmeldung.MissingInputException;
import Exceptions.SpielraumAendern.RaumnameVergebenException;
import Exceptions.SpielraumAendern.SpielerAnzahlException;
import ServerRMIInterfaces.Spielraum;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Das GUI-Fenster zum Spielraumaendern
 */
public class SpielraumaendernGUI {
    private Stage window;
    private String raumName;

    /**
     * Uebergibt die Stage an das Objekt.
     *
     * @param window   Stage/Fenster, in dem sich der Nutzer befindet.
     * @param raumName Name des Spielraumes, in dem sich der Nutzer befindet.
     */
    public void init(Stage window, String raumName) {
        this.window = window;
        this.raumName = raumName;
    }

    /**
     * Bring den Nutzer in das Spielraum-interface zurueck.
     *
     * @param actionEvent repraesentiert das Druecken des abbrechnen Knopfes.
     */
    public void abbrechnenBtn(ActionEvent actionEvent) {

        try {
            GUIMain.changeSceneInitWithRaumname(window, "SpielraumGUI.fxml", raumName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gibt einen Fehler aus, falls nicht alle Formularfelder ausgefuellt sind,
     * spieler anzahl nicht korrekt ist oder Raumname ist vergeben
     *
     * @param actionEvent repraesentiert das Druecken des bestätigen Knopfes.
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
                Spielraum spielraum = (Spielraum) registry.lookup(raumName);

                //Dienst nutzen
                spielraum.change(roomName, size);

                //Danach in der Spielraum wechseln
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
        } catch (RaumnameVergebenException | MissingInputException | SpielerAnzahlException e) {
            GUIMain.openErrorWindow(e);
        }
    }

}
