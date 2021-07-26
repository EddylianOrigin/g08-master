package ServerRMI;

import SpielRaumVerwaltung.Karte;
import Spieler.Benutzer;
import Spieler.Bot_normal;
import Spieler.Bot_schwer;
import Spieler.Spieler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import static java.rmi.server.UnicastRemoteObject.exportObject;
import static org.junit.jupiter.api.Assertions.*;

class MockSpielraum implements ClientRMIInterfaces.Spielraum {

    @Override
    public void updateSpielraum(int size, List<String> spieler, String raumName) throws RemoteException {

    }

    @Override
    public void updateSpielStatus(int amZug, List<Karte> eigeneHand, int[] kartenAnzahl_Spieler, int[] weißeChips_Spieler, int[] schwarzeChips_Spieler, Karte karte_Ablagestapel, int kartenAnzahl_Nachziehstapel, boolean[] ausgestiegen) throws RemoteException {

    }

    @Override
    public void updateGestartet() throws RemoteException {

    }

    @Override
    public void updateBeendet(List<String> sieger) throws RemoteException {

    }

    @Override
    public void updateRaumChat(String nutzername, String nachricht) throws RemoteException {

    }

    @Override
    public boolean chipAbgeben(boolean hasWChips, boolean hasBChips) throws RemoteException {
        return false;
    }

    @Override
    public void updateGeloescht() throws RemoteException {

    }
}

/**
 * Tests fuer SpielraumImpl.
 */
public class SpielraumImplTest {
    private static LobbyImpl lobby;

    @BeforeAll
    public static void CreateRegistry() {
        //teste ob das registry aus vorherigen Testfällen existiert (test auf null funktioniert nicht,
        //list() wirft aber nullpointerexception falls das registry nicht funktioniert)
        try {
            //System.out.println(LocateRegistry.getRegistry(1099));
            LocateRegistry.getRegistry(1099).list();
        } catch (Exception e) {
            try {
                LocateRegistry.createRegistry(1099);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        }
        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            MockLobby mocklobby = new MockLobby();
            registry.bind("Lobby", exportObject(mocklobby, 0));
            MockSpielraum mockSpielraum = new MockSpielraum();
            registry.bind("Spielraum", exportObject(mockSpielraum, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void createLobbyImpl() {
        lobby = new LobbyImpl();
    }

    @Test
    void getSizeInBounds() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            assertEquals(3, sr.getSize());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getName() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            assertEquals("raum", sr.getName());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getHost() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            assertEquals("host", sr.getHost());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void setHost() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.setHost("newHost");
            assertEquals("newHost", sr.getHost());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getPlayerListe() {
        try {
            String nutzername = "nutzername";
            String raumname = "raum";

            SpielraumImpl sr = new SpielraumImpl(raumname, 3, new Nutzer(nutzername, InetAddress.getByName("127.0.0.1"), 1099), lobby);

            assertEquals(1, sr.getPlayerlist().size());
            assertEquals(nutzername, sr.getPlayerlist().get(0).getName());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void change_MissingInputException() throws UnknownHostException {
        SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
        assertThrows(Exceptions.SpielraumAendern.MissingInputException.class, () -> sr.change("", 3));
    }

    @Test
    void change_RaumnameVergebenException() {
        try {
            //funktioniert aktuell nicht, da die Lobby l nicht weiß, dass der Spielraum sr existiert
            //LobbyImpl l = new LobbyImpl();
            //lobby.addSpielraumName("raum");
            lobby.addLobbyNutzer("host", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.createSpielraum("raum", 3, "host");
            lobby.addLobbyNutzer("host2", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.createSpielraum("raum2", 3, "host2");
            SpielraumImpl sr = lobby.getSpielraumImplListe().get(1);
            assertThrows(Exceptions.SpielraumAendern.RaumnameVergebenException.class, () -> sr.change("raum", 3));

        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void change_sameName() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            lobby.addSpielraumName("raum");
            sr.change("raum", 4);
            assertEquals("raum", sr.getName());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void change_SpielerAnzahlException_outOfBounds() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            assertThrows(Exceptions.SpielraumAendern.SpielerAnzahlException.class, () -> sr.change("raum", 1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void change_SpielerAnzahlException_zuWenigPlaetze() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler3", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler4", InetAddress.getByName("127.0.0.1"), 1099));
            assertThrows(Exceptions.SpielraumAendern.SpielerAnzahlException.class, () -> sr.change("raum", 3));
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void change() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            lobby.addSpielraumName("raum");
            sr.join(new Nutzer("spieler", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotNormal();
            sr.change("raum2", 4);
            assertEquals("raum2", sr.getName());
            assertEquals(4, sr.getSize());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void join() throws RemoteException {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler", InetAddress.getByName("127.0.0.1"), 1099));
            List<String> spielerListe = new ArrayList<>();
            spielerListe.add("host");
            spielerListe.add("spieler");
            assertEquals(spielerListe, sr.getPlayerNamelist());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void join2() throws RemoteException {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            List<String> spielerListe = new ArrayList<>();
            spielerListe.add("host");
            spielerListe.add("spieler");
            spielerListe.add("spieler2");
            assertEquals(spielerListe, sr.getPlayerNamelist());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void leave() throws RemoteException {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler3", InetAddress.getByName("127.0.0.1"), 1099));
            List<String> spielerListe = new ArrayList<>();
            spielerListe.add("host");
            spielerListe.add("spieler1");
            spielerListe.add("spieler3");

            sr.leave("spieler2");
            assertEquals(spielerListe, sr.getPlayerNamelist());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addBotSchwer() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.addBotSchwer();
            sr.addBotSchwer();
            List<Spieler> playerList = sr.getPlayerlist();

            int i = 0;
            for (Spieler p : playerList) {
                if (p instanceof Bot_schwer) {
                    i++;
                }
            }
            assertEquals(2, i);
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addBotNormal() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.addBotNormal();
            sr.addBotNormal();
            List<Spieler> playerList = sr.getPlayerlist();

            int i = 0;
            for (Spieler p : playerList) {
                if (p instanceof Bot_normal) {
                    i++;
                }
            }
            assertEquals(2, i);
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeBotSchwer() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotSchwer();
            sr.addBotSchwer();
            sr.addBotNormal();
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.removeBotSchwer();
            List<String> playerNameList = sr.getPlayerNamelist();
            List<Spieler> playerList = sr.getPlayerlist();

            assertEquals(5, playerList.size());
            assertEquals(5, playerNameList.size());

            //teste, dass nur ein Bot herausgenommen wird, und dass dies auch ein schwerer Bot ist
            int i = 0;
            int j = 0;
            for (Spieler p : playerList) {
                if (p instanceof Bot_schwer) {
                    i++;
                }
                if (p instanceof Bot_normal) {
                    j++;
                }
            }
            assertEquals(1, i);
            assertEquals(1, j);
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeBotNormal() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotNormal();
            sr.addBotNormal();
            sr.addBotSchwer();
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.removeBotNormal();
            List<String> playerNameList = sr.getPlayerNamelist();
            List<Spieler> playerList = sr.getPlayerlist();

            assertEquals(5, playerList.size());
            assertEquals(5, playerNameList.size());

            //teste, dass nur ein Bot herausgenommen wird, und dass dies auch ein normaler Bot ist
            int i = 0;
            int j = 0;
            for (Spieler p : playerList) {
                if (p instanceof Bot_normal) {
                    i++;
                }
                if (p instanceof Bot_schwer) {
                    j++;
                }
            }
            assertEquals(1, i);
            assertEquals(1, j);
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeBotSchwer_ohneSchwerenBot() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotNormal();
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            List<String> playerNameList = sr.getPlayerNamelist();
            List<Spieler> playerList = sr.getPlayerlist();
            sr.removeBotSchwer();

            assertEquals(playerList, sr.getPlayerlist());
            assertEquals(playerNameList, sr.getPlayerNamelist());
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeBotNormal_ohneNormalenBot() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 6, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotSchwer();
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            List<String> playerNameList = sr.getPlayerNamelist();
            List<Spieler> playerList = sr.getPlayerlist();
            sr.removeBotNormal();

            assertEquals(playerList, sr.getPlayerlist());
            assertEquals(playerNameList, sr.getPlayerNamelist());
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void aussteigen() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 2, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.start();
            //der Spieler der startet steigt aus dem Spiel aus
            sr.aussteigen();
            int ausgestiegenZaehler = 0;
            Thread.sleep(1000);
            for (Spieler s : sr.getPlayerlist()) {
                if (s.getAusgestiegen()) {
                    ausgestiegenZaehler++;
                }
            }
            assertEquals(1, ausgestiegenZaehler);
        } catch (UnknownHostException | RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getBestenliste() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 4, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotSchwer();
            sr.addBotNormal();
            Map<String, Integer> bestenliste = new TreeMap<>();
            for (Spieler s : sr.getPlayerlist()) {

                bestenliste.put(s.getName(), s.getGewonneneSpiele());

            }
            assertEquals(bestenliste, sr.getBestenliste());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void delete() {
        try {
            lobby.addNutzer("host", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.addLobbyNutzer("host", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.createSpielraum("raum", 4, "host");
            SpielraumImpl sr = lobby.getSpielraumImplListe().get(0);
            sr.delete();
            assertTrue(lobby.getSpielraumImplListe().isEmpty());
            assertTrue(lobby.getSpielraumNamen().isEmpty());
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Test
    void getPlayerCards() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 4, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);

            sr.join(new Nutzer("spieler1", InetAddress.getByName("127.0.0.1"), 1099));
            sr.join(new Nutzer("spieler2", InetAddress.getByName("127.0.0.1"), 1099));
            sr.addBotSchwer();
            sr.addBotNormal();
            List<Karte> karten = new ArrayList<>();
            Spieler spieler1 = sr.getPlayerlist().get(1);
            Karte k1 = new Karte(1);
            Karte k2 = new Karte(2);
            Karte k3 = new Karte(3);
            Karte k4 = new Karte(2);
            Karte k5 = new Karte(4);
            Karte k6 = new Karte(5);
            Karte k7 = new Karte(6);
            Karte k8 = new Karte(10);
            spieler1.addCard(k1);
            spieler1.addCard(k2);
            spieler1.addCard(k3);
            spieler1.addCard(k4);
            spieler1.addCard(k5);
            spieler1.addCard(k6);
            spieler1.addCard(k7);
            spieler1.addCard(k8);
            karten.add(k1);
            karten.add(k2);
            karten.add(k3);
            karten.add(k4);
            karten.add(k5);
            karten.add(k6);
            karten.add(k7);
            karten.add(k8);
            assertEquals(karten, sr.getPlayercards("spieler1"));
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void playerDisconnect() {
        try {
            lobby.addLobbyNutzer("host", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addNutzer("host", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addLobbyNutzer("user", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addNutzer("user", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addSpielraumName("raum");
            Nutzer h = new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1);
            Nutzer u = new Nutzer("user", InetAddress.getByName("127.0.0.1"), 1);
            SpielraumImpl spielraum = new SpielraumImpl("raum", 4, h, lobby);
            spielraum.addBotNormal();
            spielraum.join(u);
            List<Nutzer> disconnetedPlayer = new ArrayList<>();
            disconnetedPlayer.add(u);
            spielraum.playerDisconnect(disconnetedPlayer);
            List<Spieler> spielraumnutzer = spielraum.getPlayerlist();
            assertTrue(spielraumnutzer.get(0) instanceof Benutzer);
            assertTrue(spielraumnutzer.get(1) instanceof Bot_normal);
            assertTrue(spielraumnutzer.get(2) instanceof Bot_normal);
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void playerDisconnectHost() {
        try {
            lobby.addLobbyNutzer("host", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addNutzer("host", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addLobbyNutzer("user", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addNutzer("user", 1, InetAddress.getByName("127.0.0.1"));
            lobby.addSpielraumName("raum");
            Nutzer h = new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1);
            Nutzer u = new Nutzer("user", InetAddress.getByName("127.0.0.1"), 1);
            SpielraumImpl spielraum = new SpielraumImpl("raum", 4, h, lobby);
            spielraum.join(u);
            List<Nutzer> disconnetedPlayer = new ArrayList<>();
            disconnetedPlayer.add(h);
            spielraum.playerDisconnect(disconnetedPlayer);
            List<String> testliste = new ArrayList<>();
            testliste.add("user");
            assertTrue(lobby.getSpielraumImplListe().isEmpty());
            assertEquals(lobby.getLobbyNutzerListe(), testliste);
            assertEquals(lobby.getNutzerListe(), testliste);
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void stapelErstellen() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 4, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            int[] testAnzahlKarten = {0, 0, 0, 0, 0, 0, 0};
            sr.stapelErstellen();
            for (Karte k : sr.getNachziehstapel()) {
                switch (k.kartenWert) {
                    case 1 -> testAnzahlKarten[0]++;
                    case 2 -> testAnzahlKarten[1]++;
                    case 3 -> testAnzahlKarten[2]++;
                    case 4 -> testAnzahlKarten[3]++;
                    case 5 -> testAnzahlKarten[4]++;
                    case 6 -> testAnzahlKarten[5]++;
                    case 10 -> testAnzahlKarten[6]++;
                }
            }
            assertEquals(56, sr.getNachziehstapel().size());
            for (Integer i : testAnzahlKarten) {
                assertEquals(8, i);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void kartenVerteilen() {
        try {
            SpielraumImpl sr = new SpielraumImpl("raum", 4, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.addBotNormal();
            sr.addBotSchwer();
            sr.stapelErstellen();
            sr.stapelMischen();
            sr.kartenVerteilen();
            for (Spieler s : sr.getPlayerlist()) {
                assertEquals(6, s.getCards().size());
            }
            assertEquals(37, sr.getNachziehstapel().size());
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }
    @Test
    void stapelMischen() {
        try {
            Stack<Karte> test = new Stack<Karte>();
            Stack<Karte> test2 = new Stack<Karte>();
            boolean value = false;
            SpielraumImpl sr = new SpielraumImpl("raum", 4, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
            sr.stapelErstellen();
            test = sr.getNachziehstapel();
            sr.stapelMischen();
            test2 = sr.getNachziehstapel();
            for (int i = 0; i < sr.getNachziehstapel().size(); i++) {
                int k = test.pop().kartenWert;
                int j = test2.pop().kartenWert;
                if (k == j) {
                    value = false;
                } else {
                    value = true;
                    break;
                }
            }
            assertEquals(true, value);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
