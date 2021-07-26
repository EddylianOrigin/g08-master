package ClientGUI;

import ClientRMI.RMIClient;
import ServerRMIInterfaces.Spielraum;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * GUI Fenster fuer den Spielraumloeschen.
 */
public class SpielraumloeschenGUI {
    private Stage window;
    private String raumName;

    /**
     * Uebergibt die Stage an das Objekt.
     *
     * @param window   Stage/Fenster in dem sich der Nutzer befindet.
     * @param raumName Name des Spielraumes, in dem sicher der Nutzer befindet.
     */
    public void init(Stage window, String raumName) {
        this.window = window;
        this.raumName = raumName;
    }

    /**
     * Bring den Nutzer in das Spielraum-interface zurueck.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void abbrechnenBtn(ActionEvent actionEvent) {

        try {
            GUIMain.changeSceneInitWithRaumname(window, "SpielraumGUI.fxml", raumName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Lobby-interface und loescht den Spielraum.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void bestaetigenBtn(ActionEvent actionEvent) {

        try {

            Scene scene = window.getScene();

            //RMI Registry suchen (ServerPort = 1099)
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());
            //Dienst suchen
            Spielraum spielraum = (Spielraum) registry.lookup(raumName);

            //Dienst nutzen
            spielraum.delete();

            //Danach in der Lobby wechseln
            GUIMain.changeScene(window, "LobbyGUI.fxml");

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
        }
    }

}
