package ClientGUI;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Das Fenster, in dem der Spieler einen Chip zum abgeben auswaehlen kann.
 */
public class ChipAbgebenGUI {
    private Stage window;
    private boolean isBlack;
    private boolean chipChosen = false;

    /**
     * Uebergibt die Stage und die Informationen, ob der Spieler aktuell weiße und/oder scharze Chips hat, an das Objekt.
     *
     * @param hasWChips Gibt an, ob der Spieler aktuell weiße Chips hat.
     * @param hasBChips Gibt an, ob der Spieler aktuell weiße Chips hat.
     * @param window Popup-Fenster in dem der Chip ausgewaehlt werden soll.
     */
    public void init(boolean hasWChips, boolean hasBChips, Stage window){
        this.window = window;
        if(!hasWChips){
            ImageView wChip = (ImageView) window.getScene().lookup("#wChip");
            //wChip.setDisable(true);
            wChip.setVisible(false);

        }
        if(!hasBChips){
            ImageView bChip = (ImageView) window.getScene().lookup("#bChip");
            //bChip.setDisable(true);
            bChip.setVisible(false);
        }
    }
    /**
     * Waehlt einen weißen Chip zum abgeben aus.
     * @param  mouseEvent repraesentiert das Druecken auf das Icon.
     */
    public synchronized void chooseWChip(MouseEvent mouseEvent) {
        isBlack = false;
        chipChosen = true;
        //benachrichtige andere wartende Threads(siehe returnChipType), dass sie weiter machen können
        notifyAll();
        //Fenster wieder schließen
        window.close();

    }
    /**
     * Waehlt einen schwarzen Chip zum abgeben aus.
     * @param  mouseEvent repraesentiert das Druecken auf das Icon.
     */
    public synchronized void chooseBChip(MouseEvent mouseEvent) {
        isBlack = true;
        chipChosen = true;
        //benachrichtige andere wartende Threads(siehe returnChipType), dass sie weiter machen können
        notifyAll();
        //Fenster wieder schließen
        window.close();
    }

    /**
     * Gibt zurueck, ob der Spieler einen weißen, oder einen schwarzen Chip ausgewaehlt hat.
     * Wartet, bis der Spieler den Chip ausgewählt hat.
     *
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     */
    public synchronized boolean returnChipType(){
        //wartet bis ein Chip ausgewählt wurde und gibt den Typ des Chips dann zurück.
        while(!chipChosen){
            try {
                //Thread wartet, bis der Benutzer ausgewaehlt hat, welche Art von Chip er abgeben möchte
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return isBlack;
    }
}
