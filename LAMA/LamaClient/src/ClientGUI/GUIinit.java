package ClientGUI;

import javafx.stage.Stage;

/**
 * Interface fuer die GUI Fenster, die genau eine Stage uebergeben bekommen.
 */
public interface GUIinit {
    /**
     * Uebergibt die Stage an das Objekt.
     *
     * @param window Stage/Hauptfenster in dem sich der Nutzer befindet.
     */
    public void init(Stage window);
}
