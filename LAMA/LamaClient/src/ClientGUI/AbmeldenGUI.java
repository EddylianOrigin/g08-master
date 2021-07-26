package ClientGUI;

import ClientRMI.RMIClient;
import ServerRMIInterfaces.Account;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Das GUI-Fenster zum Abmelden.
 */
public class AbmeldenGUI implements GUIinit {

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
     * Bring den Nutzer in das Vorraum-interface.
     *
     * @param actionEvent repraesentiert das Druecken des bestätigen Knopfes.
     */
    public void bestätigenBtn(ActionEvent actionEvent) {

        try {
            Scene scene = window.getScene();

            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerRegistryPort());

            // eigene IP-Adresse finden
            InetAddress ip = InetAddress.getLocalHost();
            //System.out.println(ip);

            Account account = (Account) registry.lookup("Account");

            account.logout(RMIClient.getNutzerName(), RMIClient.getRegistryPort(), ip);

            GUIMain.changeScene(window, "VorraumGUI.fxml");

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
