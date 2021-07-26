package ClientGUI;

import ClientRMI.RMIClient;
import ServerRMIInterfaces.Spielraum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;


/**
 * Eine raum-lokale Bestenliste, in der zu sehen ist, welche Spieler im Raum die meisten Spiele gewonnen haben.
 */


public class BestenlisteGUI {
    @FXML
    private TableView<Spieler> Table;
    @FXML
    private ListView<String> listview;

    @FXML
    private TableColumn<Spieler, String> Benutzername;
    @FXML
    private TableColumn<Spieler, Integer> Anzahl_Spiele_gewonnen;

    /**
     * Uebergibt den Raumnamen an das Objekt.
     *
     * @param raumName Name des Raumes in dem sich der Spieler aktuell befindet.
     */
    public void init(String raumName) {

        try {
            Registry registry = LocateRegistry.getRegistry(RMIClient.getServerRegistryPort());

            //Dienst suchen
            Spielraum sr = (Spielraum) registry.lookup(raumName);

            //Dienste nutzen
            try {

                this.updateBestenliste(sr.getBestenliste());

            } catch (RemoteException e) {

                e.printStackTrace();
            }
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode UpdateBestenliste fuelle die Tabelle in dem Bestenlistenfenster
     *
     * @param list bezeichnet die Menge von alle spielername mit ihrer Jeweiligen GewonneneSpiele
     */

    private void updateBestenliste(Map<String, Integer> list) {
        boolean x = true;
        Integer rang = 1;
        ArrayList<Spieler> spielers = new ArrayList<>();

        switch (list.size()) {
            case 0:
                break;
            case 1:
                ObservableList<String> List = FXCollections.observableArrayList(
                        "1");
                listview.setItems(List);
                break;
            case 2:
                List = FXCollections.observableArrayList(
                        "1", "2");
                listview.setItems(List);
                break;

            case 3:
                List = FXCollections.observableArrayList(
                        "1", "2", "3");
                listview.setItems(List);
                break;

            case 4:
                List = FXCollections.observableArrayList(
                        "1", "2", "3", "4");
                listview.setItems(List);
                break;

            case 5:
                List = FXCollections.observableArrayList(
                        "1", "2", "3", "4", "5");
                listview.setItems(List);
                break;

            case 6:
                List = FXCollections.observableArrayList(
                        "1", "2", "3", "4", "5", "6");
                listview.setItems(List);
                break;

        }

        for (var key : list.keySet()) {
            spielers.add(new Spieler(rang, key, list.get(key)));
            rang++;

        }

        Benutzername.setCellValueFactory(new PropertyValueFactory<Spieler, String>("name"));
        Anzahl_Spiele_gewonnen.setCellValueFactory(new PropertyValueFactory<Spieler, Integer>("victories"));

        var ergebnis = FXCollections.observableArrayList(
                spielers
        );

        Anzahl_Spiele_gewonnen.setSortType(TableColumn.SortType.DESCENDING);
        Table.setItems(ergebnis);
        Table.getSortOrder().add(Anzahl_Spiele_gewonnen);

        Benutzername.setSortable(false);
        Anzahl_Spiele_gewonnen.setSortable(false);


    }

    /**
     * Die klasse Spieler vertritt alle user in Spiel
     */
    public class Spieler {

        private final String name;
        private final Integer victories;

        /**
         * hier ist Spieler eine Instance(Konstruktor) der Klasse Spieler.
         *
         * @param rang      fuer die Position als Gewinner in Spiel.
         * @param name      fuer den Name vom aktiven User.
         * @param victories fuer die Anzahl an gewonnenem Spiel.
         */
        public Spieler(Integer rang, String name, Integer victories) {

            this.name = name;
            this.victories = victories;
        }

        /**
         * die getName gibt den Name von spieler zurueck .
         *
         * @return Name des Spielers.
         */
        public String getName() {
            return name;
        }

        /**
         * die getVitories() method gibt die Anzahl an gewonnenen Spielen zurueck
         *
         * @return Anzahl der Siege des Spielers.
         */
        public Integer getVictories() {
            return victories;
        }

    }


}


