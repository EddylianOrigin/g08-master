package ClientGUI;

import ClientRMI.RMIClient;
import Exceptions.SpielraumBeitreten.RaumVollException;
import Exceptions.SpielraumBeitreten.RaumnichtVorhandenException;
import Exceptions.SpielraumBeitreten.SpielgestartetException;
import ServerRMIInterfaces.Lobby;
import ServerRMIInterfaces.Spielraum;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.*;

import static javafx.scene.text.TextAlignment.CENTER;

/**
 * Das Lobby GUI-Fenster.
 * Hier koennen Spielraeume betreten, gechattet und die Nutzerliste angezeigt werden.
 * Zudem wird eine Bestenliste angezeigt, in der zu sehen ist, welche Spieler die meisten Spiele gewonnen haben.
 */
public class LobbyGUI implements GUIinit {
    private Stage window;
    private ServerRMIInterfaces.Lobby lobby;

    /**
     * Initialisiert das Objekt, erzeugt den Dienst, der benötigt wird, um die Lobby zu aktualisieren und
     * besorgt sich die aktuelle Bestenliste vom Server um diese im Fenster zu visualisieren.
     *
     * @param window Stage/Fenster in dem sich der Nutzer befindet.
     */
    public void init(Stage window) {
        this.window = window;

        //Erzeuge Lobby Dienst
        RMIClient.createLobbyService(this);

        //code für RMI - Besentliste anzeigen

        try {
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());

            //Dienst suchen
            lobby = (Lobby) registry.lookup("Lobby");

            //Dienste nutzen
            this.updateBestenliste(lobby.getBestenliste());
            this.updateSpielraueme(lobby.getSpielraumNamen());
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
        }
    }

    /**
     * Bring den Nutzer in das Spielregeln-interface(Popup Fenster).
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void spielregelnBtn(ActionEvent actionEvent) {
        try {
            GUIMain.openPopupWindow("SpielregelnGUI.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Abmelde-interface.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void abmeldenBtn(ActionEvent actionEvent) {

        try {
            //Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            GUIMain.changeScene(window, "AbmeldenGUI.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Spieler-Loeschen-interface.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void loeschenBtn(ActionEvent actionEvent) {

        try {
            GUIMain.changeScene(window, "SpielerloeschenGUI.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bring den Nutzer in das Spieler-erstellen-interface.
     *
     * @param actionEvent repraesentiert das Druecken des Knopfes.
     */
    public void spielraumErstellenBtn(ActionEvent actionEvent) {

        try {
            GUIMain.changeScene(window, "SpielraumerstellenGUI.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fügt einen Eintrag für einen Raum in die entsprechende Box der GUI ein.
     *
     * @param spieler    Lister von Namen der Spieler, die sich im Spielraum befinden.
     * @param maxSpieler Maximale Anzahl an Spielern im Spielraum.
     */
    private void addRaum(String raum, List<String> spieler, int maxSpieler) {
        //später anpassen und aufräumen
        Scene scene = window.getScene();
        //Vbox für Raumliste suchen
        VBox rauemeBox = (VBox) scene.lookup("#VBox_Raeume");


        //GUI Elemente erstellen
        //Label raumName = new Label("testname");
        Label raumName = new Label(raum);


        //Füge Spielernamen in Label ein
        String labelText = spieler.get(0);

        for (int i = 1; i < spieler.size(); i++) {
            labelText = labelText.concat(", " + spieler.get(i));
        }

        Label spielerNamen = new Label(labelText);


        //Zeilenumbruch für Label einschalten und Alignment ändern
        spielerNamen.setWrapText(true);
        spielerNamen.setTextAlignment(CENTER);
        spielerNamen.setMinHeight(spielerNamen.getPrefHeight());
        //färbe Spielernamen grün
        spielerNamen.setTextFill(Color.LIGHTGREEN);

        //Label spielerAnzahl = new Label("6/6");
        Label spielerAnzahl = new Label(spieler.size() + "/" + maxSpieler);
        spielerAnzahl.setAlignment(Pos.BASELINE_CENTER);
        spielerAnzahl.setTextAlignment(CENTER);


        Button joinButton = new Button("beitreten");

        // Aktion für das Drücken des Knopfes festlegen
        joinButton.setOnAction(event -> {
            try {
                //Dienste nutzen
                lobby.joinSpielraum(raum, RMIClient.getNutzerName());

                //ins Spielraum Fenster gehen
                GUIMain.changeSceneInitWithRaumname(window, "SpielraumGUI.fxml", raum);

            } catch (RaumnichtVorhandenException | RaumVollException | SpielgestartetException e) {
                GUIMain.openErrorWindow(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //färbe Knopf grün, falls die Anzahl an Spielern im Raum geringer als die eingestelle maximale Spieleranzahl ist
        //andernfalls wird der Knopf rot gefärbt
        if (spieler.size() < maxSpieler) {
            joinButton.setStyle("-fx-background-color: #32CD32; ");
        } else {
            joinButton.setStyle("-fx-background-color: #D80909; ");
        }


        //das wir benötigt um später die Höhe/Breite der neu generierten Elemente zu
        // bekommen bevor die Stage angezeigt wird (show())
        //, sonst wird nur 0 bzw. -1 ausgegeben
        Group tmp = new Group();
        Scene dummyScene = new Scene(tmp);
        tmp.getChildren().add(raumName);
        tmp.getChildren().add(spielerNamen);
        tmp.getChildren().add(spielerAnzahl);
        tmp.getChildren().add(joinButton);
        tmp.applyCss();
        tmp.layout();


        //Boxen für die einzelnen Spalten erstellen, alignment innerhalb der Boxen und Größe festlegen

        HBox spalteLinks = new HBox(raumName);
        spalteLinks.setAlignment(Pos.CENTER);
        spalteLinks.setPrefWidth(rauemeBox.getWidth() / 5 * 2);

        VBox spalteMitte = new VBox(spielerAnzahl, spielerNamen);
        spalteMitte.setAlignment(Pos.CENTER);
        spalteMitte.setPrefWidth(rauemeBox.getWidth() / 5 * 2);

        VBox spalteRechts = new VBox(joinButton);
        spalteRechts.setAlignment(Pos.CENTER);
        spalteRechts.setPrefWidth(rauemeBox.getWidth() / 5);

        //Höhe für den Eintrag festlegen
        /*
        double boxHeight = Math.max(Math.max(raumName.getHeight(),
                spielerNamen.getHeight()+spielerAnzahl.getHeight()),
                joinButton.getHeight());
         */
        //Problem: label height wir nur für eine Zeile Berechnet(deshalb hier *3)
        // , bisher noch keine gute Lösung gefunden
        double boxHeight = Math.max(Math.max(raumName.getHeight(),
                spielerNamen.getHeight() + spielerAnzahl.getHeight() * 3),
                joinButton.getHeight());

        spalteLinks.setMinHeight(boxHeight);
        spalteMitte.setMinHeight(boxHeight);
        spalteRechts.setMinHeight(boxHeight);


        spalteLinks.setMinWidth(rauemeBox.getWidth() / 3);
        spalteMitte.setMinWidth(rauemeBox.getWidth() / 3);
        spalteRechts.setMinWidth(rauemeBox.getWidth() / 3);

        //Einträge für die einzelnen Spalten eine HBox packen
        HBox raumBox = new HBox(spalteLinks, spalteMitte, spalteRechts);

        //Füge die HBox für den Raum zur rauemeBox hinzu
        rauemeBox.getChildren().add(raumBox);
    }

    /**
     * Versendet die im entsprechenden Textfeld stehende Nachricht an alle Spieler in der Lobby.
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
                lobby.sendChatmessage(RMIClient.getNutzerName(), sendeFeld.getText());

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
     * Oeffnet und schließt die Nutzerliste, je nachdem ob sie gerade geoeffnet oder geschlossen ist.
     *
     * @param mouseEvent repraesentiert das Druecken auf das Icon.
     */
    public void userListClick(MouseEvent mouseEvent) {
        //Scene suchen
        //Scene scene = ((Node) mouseEvent.getSource()).getScene();

        Scene scene = window.getScene();

        //Vbox und Scrollpane für Userliste suchen
        VBox ulist_VBOX = (VBox) scene.lookup("#ulist_VBox");
        ScrollPane ulist_scroll = (ScrollPane) scene.lookup("#ulist_scroll");

        //Falls Die Nutzerliste aktuell angezeigt wird soll ein erneuter Click diese wieder unsichtbar machen
        if (ulist_VBOX.isVisible()) {
            ulist_VBOX.setVisible(false);
            ulist_scroll.setVisible(false);
            //Liste leeren
            ulist_VBOX.getChildren().clear();
        } else {
            //Vbox und Scrollpane sichtbar machen
            ulist_VBOX.setVisible(true);
            ulist_scroll.setVisible(true);

            // besorge Liste der angemeldeten Nutzer vom Server:

            //RMI Registry suchen (ServerPort = 1099)
            try {
                //Dienst nutzen
                List<String> nutzerListe = lobby.getNutzerListe();


                //für alle Einträge Labels erzeugen:
                List<Label> labels = new ArrayList<>();
                for (String name : nutzerListe) {
                    labels.add(new Label(name));
                }

                //Nutzernamen hinzufügen
                ulist_VBOX.getChildren().addAll(labels);
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
     * Schreibt die uebergeben Nachricht in den Chat in der Form nutzername: nachricht
     *
     * @param nutzerName Name des Absenders der Nachricht.
     * @param nachricht  Text der als Nachricht eines Nutzers in den Chat geschrieben wird.
     */
    public void updateLobbyChat(String nutzerName, String nachricht) {

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
     * aktualisiert die Bestenliste der Lobby, Einträge werden nach Punkten absteigend eingetragen.
     *
     * @param bestenliste ordet Nutzernamen eine Punktzahl zu, muss nicht sortiert sein.
     */
    public void updateBestenliste(Map<String, Integer> bestenliste) {
        Scene scene = window.getScene();
        VBox bestenlisteBox = (VBox) scene.lookup("#bestenlisteBox");
        double blwidth = bestenlisteBox.getWidth();

        //vorherige Einträge erst entfernen
        bestenlisteBox.getChildren().clear();

        //Einträge aus Map holen
        Set<Map.Entry<String, Integer>> entries = bestenliste.entrySet();

        //Liste zum sortieren erstellen und absteigend sortieren
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(entries);
        sortedEntries.sort(new Comparator<>() {
            @Override
            //vergleiche die Punkte
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                } else if (o1.getValue().equals(o2.getValue())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        //trage alle Einträge ein
        for (Map.Entry<String, Integer> e : sortedEntries) {
            addBestenlisteEntry(blwidth, bestenlisteBox, e.getKey(), e.getValue());
        }

    }

    /**
     * fügt einen Eintrag in die Bestenliste der GUI ein.
     *
     * @param blwidth     Breite der Bestenliste in der GUI.
     * @param bestenListe VBox der Bestenliste.
     * @param name        Spielername für den Eintrag in der Bestenliste.
     * @param punkte      Punkte des Spielers für den Eintrag in der Bestenliste.
     */
    private void addBestenlisteEntry(double blwidth, VBox bestenListe, String name, int punkte) {
        Label nameLabel = new Label(name);
        nameLabel.setMinWidth(blwidth / 5 * 4);
        nameLabel.setTextAlignment(CENTER);
        Label punkteLabel = new Label(Integer.toString(punkte));
        punkteLabel.setMinWidth(blwidth / 5);
        punkteLabel.setTextAlignment(CENTER);

        //Schriftgröße festlegen
        nameLabel.setFont(new Font(15));
        punkteLabel.setFont(new Font(15));

        HBox entry = new HBox(nameLabel, punkteLabel);
        entry.setPrefWidth(blwidth);

        //outlines für Hbox setzen
        String hboxOutlines = "-fx-border-width: 0.4;\n" +
                "-fx-border-style: solid;\n";
        entry.setStyle(hboxOutlines);

        bestenListe.getChildren().add(entry);
    }

    /**
     * zeigt die uebergebenen Spielraeume in der GUI an.
     *
     * @param spielraumNamen Namen der aktuell offenen Spielraeume.
     */
    public void updateSpielraueme(List<String> spielraumNamen) {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerIP(), RMIClient.getServerRegistryPort());

            // vorherige Spielraum Einträge löschen
            //Vbox für Raumliste suchen
            VBox rauemeBox = (VBox) window.getScene().lookup("#VBox_Raeume");

            rauemeBox.getChildren().clear();

            //neue hinzufügen
            for (String name : spielraumNamen) {
                // besorge die Größe des Raums und die SPieleranzahl über die Server Dienste
                Spielraum sr = (Spielraum) registry.lookup(name);
                addRaum(name, sr.getPlayerNamelist(), sr.getSize());
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
        }

    }


}
