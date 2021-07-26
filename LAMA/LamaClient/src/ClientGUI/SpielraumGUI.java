package ClientGUI;

import ClientRMI.RMIClient;
import Exceptions.KartenWahlException;
import ServerRMIInterfaces.Spielraum;
import SpielRaumVerwaltung.Karte;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GUI Fenster fuer den Spielraum.
 * Hier sammeln sich die Spieler und koennen dann das eigentliche Spiel spielen.
 * Weiterhin gibt es einen Rauminteren Chat und die Moeglichkeit die Bestenliste, fuer den Raum anzeigen zu lassen.
 */
public class SpielraumGUI {
    // Bilder für die Karten
    Image cardBack;
    Image lama;
    Image one;
    Image two;
    Image three;
    Image four;
    Image five;
    Image six;
    private Stage window;
    private String raumName;
    private Spielraum sr;

    /**
     * Initialisiert das Objekt, erzeugt den Dienst, der benoetigt wird, um den Spielraum zu aktualisieren und
     * besorgt sich die Raumgroeße und die aktuellen Spieler im Raum um diese entsprechend anzuzeigen
     *
     * @param window   Stage/Fenster in dem sich der Nutzer befindet.
     * @param raumName Bezeichner des Spielraumes.
     */
    public void init(Stage window, String raumName) {
        //zum testen:
        //chipAbgeben(true, true);


        //public void init(Stage window) {
        this.window = window;
        this.raumName = raumName;
        Scene scene = window.getScene();

        //Erzeuge Spielraum Dienste
        RMIClient.createSpielraumService(this);

        //Lade die Bilder für die Karten
        loadCardImages();


        //RMI Registry suchen (ServerPort = 1099)
        try {
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());

            //Dienste nachschlagen
            sr = (Spielraum) registry.lookup(raumName);

            //prüfen ob Spieler der Host ist und stellt die GUI dementsprechend ein
            if (sr.getHost().equals(RMIClient.getNutzerName())) {

                (scene.lookup("#addBot_normal")).setDisable(false);
                (scene.lookup("#addBot_hard")).setDisable(false);
                (scene.lookup("#removeBot_normal")).setDisable(false);
                (scene.lookup("#removeBot_hard")).setDisable(false);
                (scene.lookup("#deleteSR")).setDisable(false);
                (scene.lookup("#changeSR")).setDisable(false);

            }
            //Raumstatus und SpielerNamen des Raumes anzeigen:
            updateSpielraum(sr.getSize(), sr.getPlayerNamelist(), raumName);

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

    /**
     * Laedt die Bilder für die Karten des Spiels.
     */
    private void loadCardImages() {
        try {
            cardBack = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/rueckseite.png")));
            one = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_1.png")));
            two = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_2.png")));
            three = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_3.png")));
            four = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_4.png")));
            five = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_5.png")));
            six = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_6.png")));
            lama = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("images/lamacard_LAMA.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Spieler nimmt eine Karte vom Nachziehstapel auf.
     *
     * @param actionEvent actionEvent repräsentiert das Druecken des Knopfes.
     */
    public void aufnehmenBtn(ActionEvent actionEvent) {
        try {
            sr.aufnehmen();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Spieler legt eine gewaehlte Karte auf dem Ablagestapel.
     *
     * @param actionEvent actionEvent repräsentiert das Druecken des Knopfes.
     */
    public void ablegenBtn(ActionEvent actionEvent) {
        Scene scene = window.getScene();


        //schauen welchen index der Spieler in der SpielerListe hat
        try {
            List<String> namen = sr.getPlayerNamelist();
            int i = namen.indexOf(RMIClient.getNutzerName());

            //Karten aus der Szene finden
            StackPane handCards_Spieler = (StackPane) scene.lookup("#handCards_Spieler" + (i + 1));

            for (int j = 0; j < handCards_Spieler.getChildren().size(); j++) {
                ImageView card = (ImageView) handCards_Spieler.getChildren().get(j);

                final int kartenIndex = j;
                //Aktion nach Klicken auf die Karte/das Bild der Karte festlegen
                card.setOnMouseClicked(mouseEvent -> {
                    //Server Dienst nutzen
                    try {
                        sr.ablegen(kartenIndex);
                    } catch (RemoteException e) {
                        //Szene wechseln und InformationsFenster oeffnen
                        try {
                            GUIMain.changeScene(window, "VorraumGUI.fxml");
                            GUIMain.openDisconnectedWindow();
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    } catch (KartenWahlException e) {
                        GUIMain.openErrorWindow(e);
                    }
                });
            }
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Spieler steigt aus dem Durchgang aus.
     *
     * @param actionEvent repräsentiert das Druecken des Knopfes.
     */
    public void aussteigenBtn(ActionEvent actionEvent) {
        try {
            sr.aussteigen();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ruft die start Methode des Servers zum starten des Spiels auf.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void spielStartenBtn(ActionEvent actionEvent) {
        try {
            sr.start();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fuegt dem Spiel einen Bot des schweren Schwierigkeitsgrades hinzu.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void addBotSchwerBtn(ActionEvent actionEvent) {
        try {
            //Dienst nachschlagen
            //Spielraum sr = (Spielraum) registry.lookup(raumName);
            //Dienst nutzen
            sr.addBotSchwer();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fuegt dem Spiel einen Bot des normalen Schwierigkeitsgrades hinzu.
     *
     * @param actionEvent actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void addBotNormalBtn(ActionEvent actionEvent) {
        try {
            //Dienst nutzen
            sr.addBotNormal();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Entfernt einen Bot des normalen Schwierigkeitsgreades aus dem Spiel.
     *
     * @param actionEvent actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void removeBotSchwerBtn(ActionEvent actionEvent) {
        try {
            //Dienst nutzen
            sr.removeBotSchwer();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Entfernt einen Bot des normalen Schwierigkeitsgreades aus dem Spiel.
     *
     * @param actionEvent actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void removeBotNormalBtn(ActionEvent actionEvent) {
        try {
            //Dienst nutzen
            sr.removeBotNormal();
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Versendet die im entsprechenden Textfeld stehende Nachricht an alle Spieler im Spielraum.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void sendenBtn(ActionEvent actionEvent) {
        Scene scene = window.getScene();

        //über die id Textfeld suchen
        TextField sendeFeld = (TextField) scene.lookup("#tf_send");

        //wenn im Sendefeld noch nichts steht muss nicht gemacht werden
        if (!sendeFeld.getText().equals("")) {
            // Text an Server senden

            try {
                //Dienst nutzen
                sr.sendChatMessage(RMIClient.getNutzerName(), sendeFeld.getText());

                //Sendetextfeld leeren
                sendeFeld.setText("");

            } catch (RemoteException e) {
                try {
                    //Szene wechseln und InformationsFenster oeffnen
                    GUIMain.changeScene(window, "VorraumGUI.fxml");
                    GUIMain.openDisconnectedWindow();
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Schreibt die uebergeben Nachricht in den Raum-Chat in der Form nutzername: nachricht
     *
     * @param nutzerName Name des Absenders der Nachricht.
     * @param nachricht  Text der als Nachricht eines Nutzers in den Chat geschrieben wird.
     */
    public void updateRaumChat(String nutzerName, String nachricht) {
        Scene scene = window.getScene();

        //über id TextArea suchen
        TextArea chatBox = (TextArea) scene.lookup("#chatBox");
        String currentChatText = chatBox.getText();

        // falls im Chat schon Nachrichten stehen wird ein Zeilenumbruch eingefügt
        if (currentChatText.equals("")) {
            chatBox.setText(nutzerName + ": " + nachricht);
        } else {
            chatBox.setText(chatBox.getText() + "\n" + nutzerName + ": " + nachricht);
        }
    }

    /**
     * Aktualisiert das Fenster, sodass der Spieler den aktuellen Stand des Spiels sehen kann.
     *
     * @param amZug                       gibt an, ob der Spieler am Zug ist, oder nicht.
     * @param eigeneHand                  Liste der Karten, die sich auf der Hand des Spielers befinden.
     * @param kartenAnzahl_Spieler        Anzahl der Karten pro Spieler im Spiel.
     * @param weißeChips_Spieler          Anzahl der weißen Chips pro Spieler im Spiel.
     * @param schwarzeChips_Spieler       Anzahl der schwarzen CHips pro Spieler im Spiel.
     * @param karte_Ablagestapel          Karte, die oben auf dem Ablagestapel liegt.
     * @param kartenAnzahl_Nachziehstapel Anzahl der Karten auf dem Nachziehstapel.
     * @param ausgestiegen                Gibt für jeden Spieler an, ob dieser bereits aus dem Durchgang ausgestiegen ist, oder nicht.
     */
    public void updateSpielStatus(int amZug, List<Karte> eigeneHand, int[] kartenAnzahl_Spieler,
                                  int[] weißeChips_Spieler, int[] schwarzeChips_Spieler,
                                  Karte karte_Ablagestapel, int kartenAnzahl_Nachziehstapel, boolean[] ausgestiegen) {

        Scene scene = window.getScene();
        int spielerAnzahl = kartenAnzahl_Spieler.length;

        int anzahlAusgestiegeneSpieler = 0;

        //für alle Spieler im Spiel, aktualisiere die Anzeige
        for (int i = 1; i <= spielerAnzahl; i++) {

            Label spielerName = (Label) scene.lookup("#nameLabel_Spieler" + i);
            StackPane handCards_Spieler = (StackPane) scene.lookup("#handCards_Spieler" + i);


            //vorherige Karten entfernen
            handCards_Spieler.getChildren().clear();


            //Karten updaten,falls der Spieler bereits ausgestiegen ist,
            //werden die Karten verdeckt um 90 Grad gedreht hingelegt
            if (ausgestiegen[i - 1]) {
                anzahlAusgestiegeneSpieler++;
                for (int j = 0; j < kartenAnzahl_Spieler[i - 1]; j++) {
                    ImageView card = new ImageView(cardBack);
                    //Einstellungen vornehmen und Karte in Container einfügen
                    card.setPreserveRatio(true);
                    double cardHeight = handCards_Spieler.getPrefHeight();
                    card.setFitHeight(cardHeight);
                    card.setRotate(90);
                    handCards_Spieler.getChildren().add(card);

                    //offset ist hier der Offset zwischen 2 Karten
                    //offset*(kartenAnzahl_Spieler[i-1]-1)+cardHeight = handCards_Spieler.getPrefWidth()
                    //offset = (handCards_Spieler.getPrefWidth()-cardHeight)/(kartenAnzahl_Spieler[i-1]-1)
                    double offset = (handCards_Spieler.getPrefWidth() - cardHeight) / (kartenAnzahl_Spieler[i - 1] - 1);
                    //offset setzen, damit die Karten nicht direkt übereinander sind
                    // (Offset ist immer vom linken Rand ausgehend, deshalb *j)
                    StackPane.setMargin(card, new Insets(0, 0, 0, offset * j));
                }
            } else {
                for (int j = 0; j < kartenAnzahl_Spieler[i - 1]; j++) {
                    ImageView card;

                    //falls Spieler i ein anderer Spieler ist, zeige die Rückseite der Karten an
                    if (!RMIClient.getNutzerName().equals(spielerName.getText())) {
                        card = new ImageView(cardBack);
                    } else {
                        //Zeige die Bilder der Karten an, die der Spieler selbst auf der Hand hat.
                        int kartenWert = eigeneHand.get(j).kartenWert;
                        card = chooseCardImage(kartenWert);
                    }

                    //Einstellungen vornehmen und Karte in Container einfügen
                    card.setPreserveRatio(true);
                    card.setFitHeight(handCards_Spieler.getPrefHeight());
                    handCards_Spieler.getChildren().add(card);


                    //durch setPreserveRatio wird die Breite skaliert,
                    // mit getBoundsInParent werde die Transofmationen mit betrachtet/applied
                    //getFitWidth gibt nur 0 aus
                    double cardWidth = card.getBoundsInParent().getWidth();

                    //offset ist hier der Offset zwischen 2 Karten
                    //offset*(kartenAnzahl_Spieler[i-1]-1)+cardWidth = handCards_Spieler.getPrefWidth()
                    //offset = (handCards_Spieler.getPrefWidth()-cardWidth)/(kartenAnzahl_Spieler[i-1]-1)
                    double offset = (handCards_Spieler.getPrefWidth() - cardWidth) / (kartenAnzahl_Spieler[i - 1] - 1);

                    //offset setzen, damit die Karten nicht direkt übereinander sind
                    // (Offset ist immer vom linken Rand ausgehend, deshalb *j)
                    StackPane.setMargin(card, new Insets(0, 0, 0, offset * j));
                }
            }

            //Anzahl Chips updaten
            Label wChips = (Label) scene.lookup("#wChips_Spieler" + i);
            Label sChips = (Label) scene.lookup("#sChips_Spieler" + i);


            wChips.setText(Integer.toString(weißeChips_Spieler[i - 1]));
            sChips.setText(Integer.toString(schwarzeChips_Spieler[i - 1]));

        }

        //Karte auf AblageStapel aktualisieren
        ImageView ablageStapel = (ImageView) scene.lookup("#ablageStapel");

        if (!(karte_Ablagestapel == null)) {
            int kartenWert = karte_Ablagestapel.kartenWert;
            chooseCardImage(kartenWert, ablageStapel);
        }

        //Nachziehstapel aktualisieren
        ImageView nachziehStapel = (ImageView) scene.lookup("#nachziehStapel");
        //wenn Karten auf nachziehstapel, Kartenrücken sichtbar machen
        nachziehStapel.setVisible(kartenAnzahl_Nachziehstapel > 0);


        //schauen, ob der Spieler gerade am Zug ist und Szene demenstprechend ändern
        try {
            //Position des Spielers ist der Index in der Liste
            List<String> namen = sr.getPlayerNamelist();
            int i = namen.indexOf(RMIClient.getNutzerName());
            if (amZug == i) {
                setSceneAmZug(anzahlAusgestiegeneSpieler == spielerAnzahl - 1);
            } else {
                setScenenichtAmZug();
            }
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stellt die Szene auf den Zug des Spielers ein
     * (aktiviert die Knoepfe, mit denen der Spieler Aktionen in seinem Zug taetigen kann).
     */
    private void setSceneAmZug(boolean lastPlayer) {
        Scene scene = window.getScene();

        //über die id Objekte der Szene suchen
        Button aussteigenBtn = (Button) scene.lookup("#aussteigen");
        Button aufnehmenBtn = (Button) scene.lookup("#aufnehmen");
        Button ablegenBtn = (Button) scene.lookup("#ablegen");

        //Knöpfe aktivieren
        aussteigenBtn.setDisable(false);
        ablegenBtn.setDisable(false);

        //Falls der Nachziehstapel leer ist, oder alle anderen Spieler aus dem Durchgang ausgestiegen sind,
        //soll der Button zum Nachziehen deaktiviert bleiben
        if ((scene.lookup("#nachziehStapel")).isVisible() && !lastPlayer) {
            aufnehmenBtn.setDisable(false);
        }
    }

    /**
     * Stellt die Szene darauf ein, dass der Spieler nicht am Zug ist
     * (deaktiviert die Knoepfe, mit denen der Spieler Aktionen in seinem Zug taetigen kann).
     */
    private void setScenenichtAmZug() {
        Scene scene = window.getScene();

        //über die id Objekte der Szene suchen
        Button aussteigenBtn = (Button) scene.lookup("#aussteigen");
        Button aufnehmenBtn = (Button) scene.lookup("#aufnehmen");
        Button ablegenBtn = (Button) scene.lookup("#ablegen");

        //Knöpfe deaktivieren
        aussteigenBtn.setDisable(true);
        aufnehmenBtn.setDisable(true);
        ablegenBtn.setDisable(true);
    }

    /**
     * Wählt das Bild für den entsprechenden Kartenwert aus.
     *
     * @param kartenWert Wert, der die Karte im Spiel hat.
     * @return Objekt der Klasse ImageView, Container für das Bild einer Karte in der GUI.
     */
    private ImageView chooseCardImage(int kartenWert) {
        return switch (kartenWert) {
            case 1 -> new ImageView(one);
            case 2 -> new ImageView(two);
            case 3 -> new ImageView(three);
            case 4 -> new ImageView(four);
            case 5 -> new ImageView(five);
            case 6 -> new ImageView(six);
            default -> new ImageView(lama);
        };
    }

    /**
     * Wählt das Bild für den entsprechenden Kartenwert aus.
     *
     * @param kartenWert Wert, der die Karte im Spiel hat.
     * @param imgView    Container, in dem das Bild angezeigt werden soll.
     */
    private void chooseCardImage(int kartenWert, ImageView imgView) {
        switch (kartenWert) {
            case 1 -> imgView.setImage(one);
            case 2 -> imgView.setImage(two);
            case 3 -> imgView.setImage(three);
            case 4 -> imgView.setImage(four);
            case 5 -> imgView.setImage(five);
            case 6 -> imgView.setImage(six);
            default -> imgView.setImage(lama);
        }
    }

    /**
     * Stellt die Szene des Spielraums auf das gestartete Spiel ein.
     */
    public void updateGestartet() {
        //nehme Einstellungen für die Szene vor
        setSceneLaufend();
    }

    /**
     * Stellt die Szene des Spielraums wieder um und zeigt in einem popup Fenster den Gewinner an.
     *
     * @param sieger Spielernamen der Spielers die gewonnen haben.
     */
    public void updateBeendet(List<String> sieger) {
        //nehme Einstellungen für die Szene vor
        setScenenichtLaufend();
        // Zeige Gewinner in einem popup Fenster an.
        try {
            GUIMain.openPopupSpielBeendet(sieger);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stellt die Szene für ein nicht laufendes Spiel ein.
     */
    private void setScenenichtLaufend() {
        Scene scene = window.getScene();

        //Einstellung für noch nicht laufendes Spiel vornehmen

        //über die id Objekte der Szene suchen
        Button aussteigenBtn = (Button) scene.lookup("#aussteigen");
        Button aufnehmenBtn = (Button) scene.lookup("#aufnehmen");
        Button ablegenBtn = (Button) scene.lookup("#ablegen");
        Button startBtn = (Button) scene.lookup("#spielStarten");
        Label raumStatusLabel = (Label) scene.lookup("#raumStatusLabel");
        ImageView nachziehStapel = (ImageView) scene.lookup("#nachziehStapel");
        ImageView ablageStapel = (ImageView) scene.lookup("#ablageStapel");
        Button leaveSRBtn = (Button) scene.lookup("#leaveSR");

        //Buttons deaktivieren
        aufnehmenBtn.setDisable(true);
        aussteigenBtn.setDisable(true);
        ablegenBtn.setDisable(true);
        //Raumstatus anzeigen
        raumStatusLabel.setVisible(true);
        //Stapel ausblenden
        nachziehStapel.setVisible(false);
        ablageStapel.setVisible(false);


        try {
            //Button wieder aktivieren, falls es sich um den Host handelt
            if (RMIClient.getNutzerName().equals(sr.getHost())) {
                startBtn.setDisable(false);
            }

            leaveSRBtn.setDisable(false);

            //Karten und Chips für die Spieler im Raum unsichtbar machen:

            int spielerAnzahl = sr.getPlayerNamelist().size();

            for (int i = 1; i <= spielerAnzahl; i++) {
                scene.lookup("#ingame_spieler" + i).setVisible(false);
            }

            //prüfen ob Spieler der Host ist und stellt die GUI dementsprechend ein
            if (sr.getHost().equals(RMIClient.getNutzerName())) {

                (scene.lookup("#addBot_normal")).setDisable(false);
                (scene.lookup("#addBot_hard")).setDisable(false);
                (scene.lookup("#removeBot_normal")).setDisable(false);
                (scene.lookup("#removeBot_hard")).setDisable(false);
                (scene.lookup("#deleteSR")).setDisable(false);
                (scene.lookup("#changeSR")).setDisable(false);

            }
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stellt die Szene für ein laufendes Spiel ein.
     */
    private void setSceneLaufend() {
        Scene scene = window.getScene();

        //Einstellung für noch nicht laufendes Spiel vornehmen

        //über die id Objekte der Szene suchen
        Label raumStatusLabel = (Label) scene.lookup("#raumStatusLabel");
        Button spielStarten = (Button) scene.lookup("#spielStarten");
        Button addBot_normal = (Button) scene.lookup("#addBot_normal");
        Button addBot_hard = (Button) scene.lookup("#addBot_hard");
        Button removeBot_normal = (Button) scene.lookup("#removeBot_normal");
        Button removeBot_hard = (Button) scene.lookup("#removeBot_hard");
        Button deleteSR = (Button) scene.lookup("#deleteSR");
        Button changeSR = (Button) scene.lookup("#changeSR");
        Button leaveSRBtn = (Button) scene.lookup("#leaveSR");
        ImageView nachziehStapel = (ImageView) scene.lookup("#nachziehStapel");
        ImageView ablageStapel = (ImageView) scene.lookup("#ablageStapel");


        //Buttons aktivieren bzw. deaktivieren
        spielStarten.setDisable(true);
        addBot_normal.setDisable(true);
        addBot_hard.setDisable(true);
        removeBot_normal.setDisable(true);
        removeBot_hard.setDisable(true);
        deleteSR.setDisable(true);
        changeSR.setDisable(true);
        leaveSRBtn.setDisable(true);

        //Raumstatus ausblenden
        raumStatusLabel.setVisible(false);

        //Stapel anzeigen
        nachziehStapel.setVisible(true);
        ablageStapel.setVisible(true);

        //Karten und Chips für die Spieler im Raum anzeigen:
        try {
            int spielerAnzahl = sr.getPlayerNamelist().size();

            for (int i = 1; i <= spielerAnzahl; i++) {
                scene.lookup("#ingame_spieler" + i).setVisible(true);
            }
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Aktualisiert die Raumstatus-Anzeige und die Spieler in der GUI <!-- -->.
     * Falls der Raum voll ist, wird der Spiel starten Button aktiviert
     *
     * @param size     Eingestelle maximal Spieleranzahl.
     * @param spieler  Liste von sich aktuell im Raum befindeten Spielern.
     * @param raumName (eventuell geaenderter) Name des Rauemes.
     */
    public void updateSpielraum(int size, List<String> spieler, String raumName) {

        this.raumName = raumName;

        int spielerAnzahl = spieler.size();

        setRaumStatusLabel(size, spielerAnzahl);
        setPlayerNameLabels(spieler);

        //falls der Raum voll ist, kann der Host das Spiel starten
        try {
            //aktiviere Knopf zum Spiel starten für den Host
            (window.getScene().lookup("#spielStarten")).setDisable(size != spielerAnzahl || !sr.getHost().equals(RMIClient.getNutzerName()));
        } catch (RemoteException e) {
            try {
                //Szene wechseln und InformationsFenster oeffnen
                GUIMain.changeScene(window, "VorraumGUI.fxml");
                GUIMain.openDisconnectedWindow();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Aktualisiert die Raumstatus-Anzeige in der GUI.
     *
     * @param size          Eingestelle maximal Spieleranzahl.
     * @param spielerAnzahl aktuelle Spieleranzahl im Raum.
     */
    private void setRaumStatusLabel(int size, int spielerAnzahl) {
        Scene scene = window.getScene();

        Label raumStatusLabel = (Label) scene.lookup("#raumStatusLabel");


        //Statusanzeige aktualisieren
        if (size == spielerAnzahl) {
            raumStatusLabel.setText("Der Spielraum ist voll, das Spiel kann gestartet werden");
        } else {
            raumStatusLabel.setText("Warten auf Spieler: " + spielerAnzahl + "/" + size);
        }
    }

    /**
     * Aktualisiert die Spieler in der GUI.
     *
     * @param spieler Liste von sich aktuell im Raum befindeten Spielern.
     */
    private void setPlayerNameLabels(List<String> spieler) {
        Scene scene = window.getScene();

        //über die id Objekte der Szene suchen
        Label nameLabel_Spieler1 = (Label) scene.lookup("#nameLabel_Spieler1");
        Label nameLabel_Spieler2 = (Label) scene.lookup("#nameLabel_Spieler2");
        Label nameLabel_Spieler3 = (Label) scene.lookup("#nameLabel_Spieler3");
        Label nameLabel_Spieler4 = (Label) scene.lookup("#nameLabel_Spieler4");
        Label nameLabel_Spieler5 = (Label) scene.lookup("#nameLabel_Spieler5");
        Label nameLabel_Spieler6 = (Label) scene.lookup("#nameLabel_Spieler6");

        // Spielerlabels im Raum aktualisieren

        List<Label> nameLabelListe = new ArrayList<>();
        nameLabelListe.add(nameLabel_Spieler1);
        nameLabelListe.add(nameLabel_Spieler2);
        nameLabelListe.add(nameLabel_Spieler3);
        nameLabelListe.add(nameLabel_Spieler4);
        nameLabelListe.add(nameLabel_Spieler5);
        nameLabelListe.add(nameLabel_Spieler6);

        int spielerAnzahl = spieler.size();

        //für die Spieler im Spiel die Namen setzen
        int i = 0;
        for (; i < spielerAnzahl; i++) {
            nameLabelListe.get(i).setText(spieler.get(i));
        }
        //für den Rest der Labels trage leeren String ein(so werden die Namen der Spieler, die den Raum verlassen wieder gelöscht)
        for (; i < 6; i++) {
            nameLabelListe.get(i).setText("");
        }
    }

    /**
     * Bring den Nutzer in das SpielraumLoeschen-interface.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void spielraumLoeschenBtn(ActionEvent actionEvent) {
        try {
            GUIMain.changeSceneInitWithRaumname(window, "SpielraumloeschenGUI.fxml", raumName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Bestenliste-interface(PopUp-Fenster).
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void bestenlisteBtn(ActionEvent actionEvent) {
        try {
            GUIMain.openPopupWindowInitWithRaumname("BestenlisteGUI.fxml", raumName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Spielraum-Verlassen-interface.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void spielraumVerlassenBtn(ActionEvent actionEvent) {
        try {
            GUIMain.changeSceneInitWithRaumname(window, "SpielraumverlassenGUI.fxml", raumName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Spieler-aendern-interface.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void spielraumAendernBtn(ActionEvent actionEvent) {
        try {
            GUIMain.changeSceneInitWithRaumname(window, "SpielraumaendernGUI.fxml", raumName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Oeffnet ein Fenster, in dem der Spieler entscheiden kann, ob er schwarze, oder weiße Chips abgibgt.
     *
     * @param hasWChips gibt an, ob der Spieler weiße Chips besitzt.
     * @param hasBchips gibt an, ob der Spieler schwarze Chips bestizt.
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     */
    public boolean chipAbgeben(boolean hasWChips, boolean hasBchips) {

        //GUI in application Thread erstellen
        //in lambda Expression wird final Element benötigt
        final ChipAbgebenGUI[] chipAbgebenController = new ChipAbgebenGUI[1];
        javafx.application.Platform.runLater(() ->
        {
            try {
                chipAbgebenController[0] = GUIMain.openchipAbgebenPopupWindow(hasWChips, hasBchips);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //der Rest wird im anderen Thread gemacht
        //Der Thread wartet, bis das ChipAbgebenGUI Objekt durch den application Thread erzeugt wurde
        while (chipAbgebenController[0] == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //danach wird hier die returnChipType Methode aufgerufen, der Thread wartet also,
        // bis der Nutzer den Chip zum abgeben ausgewählt hat(siehe returnChipType Methode)
        boolean isBlack = chipAbgebenController[0].returnChipType();
        return isBlack;
    }

    /**
     * Bringt den Spieler wieder in das LobbyInterface zurueck.
     */
    public void updateGeloescht() {
        try {
            GUIMain.changeScene(window, "LobbyGUI.fxml");
            // Spieler wird informiert, dass de Raum gelöscht wurde
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            //Einstellungen für Fenstertext
            alert.getDialogPane().setContentText("Der Spielraum wurde durch den Host gelöscht");
            alert.getDialogPane().setHeaderText("Spielraum gelöscht!");
            //Fenster öffnen
            alert.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


