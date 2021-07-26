package ClientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

/**
 * Klasse zum Starten der GUI, wechseln der Fenster und zum oeffnen von popup Fenstern oder Fehlerfenstern.
 */
public class GUIMain extends Application {
    /**
     * Erzeugt das Fenster fuer den Vorraum der grafischen Benutzeroberflaeche und zeigt dieses an.
     * @param window Fenster des Programms.
     */
    @Override
    public void start(Stage window) throws IOException {
        //Parent root = FXMLLoader.load(getClass().getResource("VorraumGUI.fxml"));
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource("VorraumGUI.fxml"));
        Parent root = loader.load();
        window.setTitle("LAMA");

        Scene vorraum = new Scene(root);

        window.setScene(vorraum);

        //initialisiere Vorraum Fenster (setzte Stage)
        ((GUIinit) loader.getController()).init(window);

        window.setResizable(false);
        window.show();
        //window.centerOnScreen();
    }
    /**
     * Wechselt die Szene.
     * @param window Stage/Fenster in dem sich der Nutzer befindet.
     * @param resource fxml Dateiname für die Szene.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    public static void changeScene(Stage window, String resource) throws IOException {
        //Lade die Szene
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource(resource));
        Parent root = loader.load();
        Scene s = new Scene(root);
        window.setScene(s);
        //Falls eine GUI mit init() Methode geöffnet werden soll muss eine stage übergeben werden
        if(loader.getController() instanceof GUIinit) {
            ((GUIinit) loader.getController()).init(window);
        }

        //window.show();
    }
    /**
     * Wechselt die Szene.
     * @param window Stage/Fenster in dem sich der Nutzer befindet.
     * @param resource fxml Dateiname für die Szene.
     * @param raumName Name des Raums, in den der Spieler eintritt.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    public static void changeSceneInitWithRaumname(Stage window, String resource, String raumName) throws IOException {
        //Lade die Szene
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource(resource));
        Parent root = loader.load();
        Scene s = new Scene(root);
        window.setScene(s);
        //Falls eine GUI mit init() Methode geöffnet werden soll muss eine stage übergeben werden
        if(loader.getController() instanceof SpielraumGUI) {
            ((SpielraumGUI) loader.getController()).init(window,raumName);
        }
        if(loader.getController() instanceof SpielraumloeschenGUI) {
            ((SpielraumloeschenGUI) loader.getController()).init(window,raumName);
        }
        if(loader.getController() instanceof SpielraumverlassenGUI) {
            ((SpielraumverlassenGUI) loader.getController()).init(window,raumName);
        }
        if(loader.getController() instanceof SpielraumaendernGUI) {
            ((SpielraumaendernGUI) loader.getController()).init(window,raumName);
        }

        //window.show();
    }


    /**
     * Oeffnet ein Popup-Fenster mit der als resource übergebenen Szene(unresizable).
     * @param resource fxml Dateiname für die Szene.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    public static void openPopupWindow(String resource) throws IOException{
        Parent root = FXMLLoader.load(GUIMain.class.getResource(resource));

        Scene s= new Scene(root);

        // Neue Stage für popup erstellen
        Stage popup = new Stage();
        popup.setScene(s);
        popup.setResizable(false);
        //Anderes Fenster blockieren
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        //Fenster öffnen
        popup.show();
    }

    /**
     * Oeffnet ein Popup-Fenster mit der als resource übergebenen Szene(unresizable).
     * @param resource fxml Dateiname für die Szene.
     * @param raumName Name des Spielraumes, in dem der Spieler sich befindet.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    public static void openPopupWindowInitWithRaumname(String resource,String raumName) throws IOException{
        //Lade die Szene
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource(resource));
        Parent root = loader.load();

        Scene s= new Scene(root);

        // Neue Stage für popup erstellen
        Stage popup = new Stage();
        popup.setScene(s);
        popup.setResizable(false);
        //Anderes Fenster blockieren
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        if(loader.getController() instanceof BestenlisteGUI) {
            ((BestenlisteGUI) loader.getController()).init(raumName);
        }
        //Fenster öffnen
        popup.show();
    }


    /**
     * Oeffnet ein Popup-Fenster mit der als resource übergebenen Szene(unresizable) und uebergibt das Hauptfenster.
     * @param resource fxml Dateiname für die Szene.
     * @param mainStage das Hauptfenster, welches zusätzlich geöffnet bleibt.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    /*
    public static void openPopupWindow( Stage mainStage,String resource) throws IOException{
        //Lade die Szene
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource(resource));
        Parent root = loader.load();

        Scene s= new Scene(root);

        // Neue Stage für popup erstellen
        Stage popup = new Stage();
        popup.setScene(s);
        popup.setResizable(false);
        //Anderes Fenster blockieren
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        //Falls eine GUI mit init() Methode geöffnet werden soll muss eine stage übergeben werden
        if(loader.getController() instanceof GUIinit) {
            ((GUIinit) loader.getController()).init(mainStage);
        }

        //Fenster öffnen
        popup.show();
    }
    */

    /**
     * Oeffnet ein Popup-Fenster mit der als resource übergebenen Szene(unresizable).
     * @param sieger Spielername des Spielers der gewonnen hat.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    public static void openPopupSpielBeendet(List<String> sieger) throws IOException{
        //Lade die Szene
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource("SpielBeendeGUI.fxml"));
        Parent root = loader.load();

        Scene s= new Scene(root);

        // Neue Stage für popup erstellen
        Stage popup = new Stage();
        popup.setScene(s);
        popup.setResizable(false);
        //Anderes Fenster blockieren
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        if(loader.getController() instanceof SpielBeendeGUI) {
            ((SpielBeendeGUI) loader.getController()).init(popup, sieger);
        }
        //Fenster öffnen
        popup.show();
    }

    /**
     * Öffnet das ChipAbgeben GUI-Fenster als Popup-Fenster.
     * @param hasWChips Gibt an, ob der Spieler aktuell weiße Chips hat.
     * @param hasBchips Gibt an, ob der Spieler aktuell schwarze Chips hat.
     * @return Das ChipAbgebenGUI Objekt.
     * @throws IOException wird geworfen, falls die fxml Datei nicht geladen werden konnte.
     */
    public static ChipAbgebenGUI openchipAbgebenPopupWindow(boolean hasWChips, boolean hasBchips) throws IOException{
        FXMLLoader loader = new FXMLLoader(GUIMain.class.getResource("ChipAbgebenGUI.fxml"));
        Parent root = loader.load();
        Scene s= new Scene(root);

        // Neue Stage für popup erstellen
        Stage popup = new Stage();
        popup.setScene(s);
        popup.setResizable(false);

        //TitelLeiste ausblenden(keine Buttons)
        //popup.initStyle(StageStyle.UNDECORATED);

        //close button deaktivieren
        popup.setOnCloseRequest(event -> event.consume());

        //Anderes Fenster blockieren
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        ChipAbgebenGUI controller = null;
        if(loader.getController() instanceof ChipAbgebenGUI) {
            controller = loader.getController();
            controller.init(hasWChips,hasBchips, popup);
        }
        //Fenster öffnen
        popup.show();
        return controller;
    }

    /**
     * Oeffnet ein Error-Fenster.(unresizable).
     * @param e Exception, die abgefangen wurde.
     */
    public static void openErrorWindow(Exception e) {
        //Fehlermeldung erzeugen
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //Einstellungen für Fenstertext
        alert.getDialogPane().setContentText(e.getMessage());
        alert.getDialogPane().setHeaderText(e.getClass().getName());
        //Fenster öffnen
        alert.show();

    }
    /**
     * Oeffnet ein Informations-Fenster, welches den Spieler über Verbindungsprobleme Informiert.(unresizable).
     */
    public static void openDisconnectedWindow() {
        // Spieler wird informiert, dass es Verbindungsprobleme gab
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //Einstellungen für Fenstertext
        alert.getDialogPane().setContentText("Es gab Probleme mit der Verbindung zu Server");
        alert.getDialogPane().setHeaderText("Server nicht erreichbar");
        //Fenster öffnen
        alert.show();

    }

    /**
     * Ruft die start Methode auf.
     */
    public static void main() {
        launch();
    }
}
