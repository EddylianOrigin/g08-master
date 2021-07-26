package ServerRMI;

import ServerRMIInterfaces.Spielraum;
import Spieler.Spieler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.rmi.server.UnicastRemoteObject.exportObject;
import static org.junit.jupiter.api.Assertions.*;

class MockLobby implements ClientRMIInterfaces.Lobby {

    @Override
    public void updateBestenliste(Map<String, Integer> bestenliste) throws RemoteException {

    }

    @Override
    public void updateSpielraeume(List<String> spielraumNamen) throws RemoteException {

    }

    @Override
    public void updateLobbyChat(String nutzername, String nachricht) throws RemoteException {

    }
}

class LobbyImplTest {

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
            ServerRMI.MockLobby mocklobby = new ServerRMI.MockLobby();
            registry.bind("Lobby", exportObject(mocklobby, 0));
            MockSpielraum mockSpielraum = new ServerRMI.MockSpielraum();
            registry.bind("Spielraum", exportObject(mockSpielraum, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void creatSpielraum_raumnameFehlt_MissingInputException() {
        assertThrows(Exceptions.SpielraumErstellen.MissingInputException.class, () -> new LobbyImpl().createSpielraum(null, 4, "nutzer"));
    }

    @Test
    void creatSpielraum_nutzernameFehlt_MissingInputException() {
        assertThrows(Exceptions.SpielraumErstellen.MissingInputException.class, () -> new LobbyImpl().createSpielraum("raum", 4, null));
    }

    @Test
    void creatSpielraum_Anzahlzugross_SpielerAnzahlException() {
        assertThrows(Exceptions.SpielraumErstellen.SpielerAnzahlException.class, () -> new LobbyImpl().createSpielraum("raum", 10, "nutzer"));
    }

    @Test
    void creatSpielraum_Anzahlzuklein_SpielerAnzahlException() {
        assertThrows(Exceptions.SpielraumErstellen.SpielerAnzahlException.class, () -> new LobbyImpl().createSpielraum("raum", 1, "nutzer"));
    }


    @Test
    void creatSpielraum_RaumnameVergeben_RaumnameVergebenException() {
        LobbyImpl lobby = new LobbyImpl();
        String raumname = "raumname";
        String nutzername = "nutzername";
        assertAll(() -> LocateRegistry.getRegistry(1099));
        assertAll(() -> lobby.addLobbyNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.createSpielraum(raumname, 4, nutzername));
        assertThrows(Exceptions.SpielraumErstellen.RaumnameVergebenException.class, () -> lobby.createSpielraum(raumname, 4, nutzername));
    }

    @Test
    void creatSpielraum_ErfolgreichesErstellen() {
        LobbyImpl lobby = new LobbyImpl();
        String raumname = "raumname";
        String host = "host";
        assertAll(() -> LocateRegistry.getRegistry(1099));
        assertAll(() -> lobby.addLobbyNutzer(host, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.createSpielraum(raumname, 4, host));
        assertAll(() -> {
            Spieler spieler = null;
            for (SpielraumImpl s : lobby.getSpielraumImplListe()) {
                if (s.getName().equals(raumname)) {
                    for (Spieler sp : s.getPlayerlist()) {
                        if (sp.getName().equals(host)) {
                            spieler = sp;
                        }
                    }
                }
            }
            if (spieler == null) {
                fail("Host not in Playerlist");
            }
        });
    }

    @Test
    void getNutzerListe_leer() {
        try {
            assertEquals(new ArrayList<>(), new LobbyImpl().getNutzerListe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getNutzerListe_nichtleer() {
        try {
            String nutzername = "nutzername";
            List<String> namelist = Collections.singletonList(nutzername);
            LobbyImpl lobby = new LobbyImpl();
            assertAll(() -> lobby.addNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
            assertEquals(namelist, lobby.getNutzerListe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getLobbyNutzerListe_leer() {
        try {
            assertEquals(new ArrayList<>(), new LobbyImpl().getLobbyNutzerListe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getLobbyNutzerListe_nichtleer() {
        try {
            String nutzername = "nutzername";
            List<String> namelist = Collections.singletonList(nutzername);
            LobbyImpl lobby = new LobbyImpl();
            assertAll(() -> lobby.addLobbyNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
            assertEquals(namelist, lobby.getLobbyNutzerListe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void addNutzer() {
        try {
            String nutzername = "nutzername";
            LobbyImpl lobby = new LobbyImpl();
            assertAll(() -> lobby.addNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
            String test = null;
            for (String n : lobby.getNutzerListe()) {
                if (n.equals(nutzername)) {
                    test = n;
                }
            }
            assertNotEquals(test, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void addLobbyNutzer() {
        try {
            String nutzername = "nutzername";
            LobbyImpl lobby = new LobbyImpl();
            assertAll(() -> lobby.addLobbyNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
            String test = null;
            for (String n : lobby.getLobbyNutzerListe()) {
                if (n.equals(nutzername)) {
                    test = n;
                }
            }
            assertNotEquals(test, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeNutzer() {
        try {
            String nutzername = "nutzername";
            LobbyImpl lobby = new LobbyImpl();
            assertAll(() -> lobby.addNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
            assertAll(() -> lobby.removeNutzer(nutzername));
            String test = null;
            for (String n : lobby.getNutzerListe()) {
                if (n.equals(nutzername)) {
                    test = n;
                }
            }
            assertNull(test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeLobbyNutzer() {
        try {
            String nutzername = "nutzername";
            LobbyImpl lobby = new LobbyImpl();
            assertAll(() -> lobby.addLobbyNutzer(nutzername, 1099, InetAddress.getByName("127.0.0.1")));
            assertAll(() -> lobby.removeLobbyNutzer(nutzername));
            String test = null;
            for (String n : lobby.getLobbyNutzerListe()) {
                if (n.equals(nutzername)) {
                    test = n;
                }
            }
            assertNull(test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void joinSpielraum_RaumnichtVorhanden_RaumnichtVorhandenException() {
        assertThrows(Exceptions.SpielraumBeitreten.RaumnichtVorhandenException.class, () -> new LobbyImpl().joinSpielraum("raumname", "nutzername"));
    }

    @Test
    void joinSpielraum_RaumVoll_RaumVollException() {
        String spielraumname = "spielraumname";
        LobbyImpl lobby = new LobbyImpl();
        String user1 = "user1";
        String user2 = "user2";
        String host = "host";
        assertAll(() -> lobby.addLobbyNutzer(user1, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.addLobbyNutzer(user2, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.addLobbyNutzer(host, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.createSpielraum(spielraumname, 2, host));
        assertAll(() -> lobby.joinSpielraum(spielraumname, user1));
        assertThrows(Exceptions.SpielraumBeitreten.RaumVollException.class, () -> lobby.joinSpielraum(spielraumname, user2));
    }

    @Test
    void joinSpielraum_SpielgestartetException_SpielgestartetException() {
        String spielraumname = "spielraumname";
        LobbyImpl lobby = new LobbyImpl();
        String user1 = "user1";
        String user2 = "user2";
        String host = "host";
        assertAll(() -> lobby.addLobbyNutzer(user1, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.addLobbyNutzer(user2, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.addLobbyNutzer(host, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.createSpielraum(spielraumname, 3, host));
        assertAll(() -> lobby.joinSpielraum(spielraumname, user1));
        assertAll(() -> {
            for (Spielraum s : lobby.getSpielraumImplListe()) {
                if (s.getName().equals(spielraumname)) {
                    s.start();
                }
            }
        });
        assertThrows(Exceptions.SpielraumBeitreten.SpielgestartetException.class, () -> lobby.joinSpielraum(spielraumname, user2));
    }

    @Test
    void joinSpielraum_ErfolgreichesBeitreten() {
        String spielraumname = "spielraumname";
        LobbyImpl lobby = new LobbyImpl();
        String user1 = "user1";
        String host = "host";
        assertAll(() -> lobby.addLobbyNutzer(user1, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.addLobbyNutzer(host, 1099, InetAddress.getByName("127.0.0.1")));
        assertAll(() -> lobby.createSpielraum(spielraumname, 3, host));
        assertAll(() -> lobby.joinSpielraum(spielraumname, user1));
        assertAll(() -> {
            for (Spielraum s : lobby.getSpielraumImplListe()) {
                if (s.getName().equals(spielraumname)) {
                    String spielername = null;
                    for (String sp : s.getPlayerNamelist()) {
                        if (sp.equals(user1)) {
                            spielername = sp;
                        }
                    }
                    if (spielername == null) {
                        fail("User not in Playerlist");
                    }
                }
            }

        });
    }

    @Test
    void removeSpielraumName() {
        LobbyImpl lobby = new LobbyImpl();
        assertAll(() -> {
            lobby.addLobbyNutzer("host1", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.addLobbyNutzer("host2", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.createSpielraum("raum1", 3, "host1");
            lobby.createSpielraum("raum2", 3, "host2");
            lobby.removeSpielraum("raum1");
            List<String> srList = new ArrayList<>();
            srList.add("raum2");
            assertEquals(srList, lobby.getSpielraumNamen());
        });
    }

    @Test
    void changeSpielraumName() {
        LobbyImpl lobby = new LobbyImpl();
        assertAll(() -> {
            lobby.addLobbyNutzer("host1", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.addLobbyNutzer("host2", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.addLobbyNutzer("host3", 1099, InetAddress.getByName("127.0.0.1"));
            lobby.createSpielraum("raum1", 3, "host1");
            lobby.createSpielraum("raum2", 3, "host2");
            lobby.createSpielraum("raum3", 3, "host3");
            SpielraumImpl sr = lobby.getSpielraumImplListe().get(1);
            lobby.changeSpielraumName("raum2", "testname", sr);
            List<String> srList = new ArrayList<>();
            srList.add("raum1");
            srList.add("testname");
            srList.add("raum3");
            assertEquals(srList, lobby.getSpielraumNamen());
        });
    }
}