package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;

/**
 * GUI Fenster, das anzeigt welche Spieler gewonnen haben.
 */
public class SpielBeendeGUI {
    @FXML
    Label XXX = new Label("");
    private Stage window;

    /**
     * Uebergibt die Stage und die Liste der Sieger an das Objekt.
     *
     * @param window Stage/Hauptfenster in dem sich der Nutzer befindet.
     * @param sieger Lister der Namen der Sieger des Spiels.
     */
    public void init(Stage window, List<String> sieger) {
        this.window = window;
        window.setResizable(true);

        String siegerString = "";
        int i = 0;
        //Sieger zum String hinzufügen
        if (sieger.size() == 1) {
            siegerString = siegerString.concat(sieger.get(i) + " hat ");
        } else {
            for (; i < sieger.size() - 1; i++) {
                siegerString = siegerString.concat(sieger.get(i) + ", ");
            }
            siegerString = siegerString.concat(sieger.get(i) + " haben");
        }

        XXX.setText(siegerString);
    }
}
