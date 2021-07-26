package Spieler;

import ServerRMI.LobbyImpl;
import ServerRMI.Nutzer;
import ServerRMI.SpielraumImpl;
import SpielRaumVerwaltung.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer Bots des schweren Schwierigkeitsgrades.
 */
class Bot_schwerTest {
    private static LobbyImpl lobby;

    @BeforeEach
    void createLobbyImpl() {
        lobby = new LobbyImpl();
    }

    @Test
    void aussteigen() throws RemoteException, UnknownHostException, InterruptedException {
        List<Karte> kartenList = new ArrayList<>();
        int numBotCards = 0;
        List<Karte> kartenList2 = new ArrayList<>();
        boolean value = false;
        SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
        sr.addBotSchwer();
        sr.addBotNormal();
        sr.stapelErstellen();
        sr.kartenVerteilen();

        Karte x = sr.getAblagestapelKarte();
        for (Spieler p : sr.getPlayerlist()) {
            if (p instanceof Bot_schwer) {
                numBotCards = p.getCards().size();
                for (int i = 0; i < numBotCards; i++) {
                    p.removeCard(p.getCards().get(0));
                }
                switch (x.kartenWert) {
                    case 1:
                        p.addCard(new Karte(10));
                        p.addCard(new Karte(10));
                        kartenList = p.getCards();
                        break;
                    case 2:
                        p.addCard(new Karte(1));
                        p.addCard(new Karte(1));
                        kartenList = p.getCards();
                        break;
                    case 3:
                        p.addCard(new Karte(2));
                        p.addCard(new Karte(2));
                        kartenList = p.getCards();
                        break;
                    case 4:
                        p.addCard(new Karte(3));
                        p.addCard(new Karte(3));
                        kartenList = p.getCards();
                        break;
                    case 5:
                        p.addCard(new Karte(4));
                        p.addCard(new Karte(4));
                        kartenList = p.getCards();
                        break;
                    case 6:
                        p.addCard(new Karte(5));
                        p.addCard(new Karte(5));
                        kartenList = p.getCards();
                        break;
                    case 10:
                        p.addCard(new Karte(6));
                        p.addCard(new Karte(6));
                        kartenList = p.getCards();
                        break;
                }
            } else {
                numBotCards = p.getCards().size();
                for (int i = 0; i < numBotCards; i++) {
                    p.removeCard(p.getCards().get(0));
                }
                switch (x.kartenWert) {
                    case 1:
                        p.addCard(new Karte(10));
                        break;
                    case 2:
                        p.addCard(new Karte(1));
                        break;
                    case 3:
                        p.addCard(new Karte(2));
                        break;
                    case 4:
                        p.addCard(new Karte(3));
                        break;
                    case 5:
                        p.addCard(new Karte(4));
                        break;
                    case 6:
                        p.addCard(new Karte(5));
                        break;
                    case 10:
                        p.addCard(new Karte(6));
                        break;
                }
                kartenList2 = p.getCards();
            }
        }
        assertEquals(1, kartenList2.size());
        assertEquals(2, kartenList.size());

        for (Spieler spieler : sr.getPlayerlist()) {
            if (spieler instanceof Bot_schwer) {
                int j = sr.getPlayerlist().indexOf(spieler);
                sr.setAmZug(sr.getPlayerlist().indexOf(spieler));
                ((Bot_schwer) spieler).play();
                sleep(1000);
                sleep(1000);

                if (spieler.getAusgestiegen()) {
                    value = true;
                }
            }
        }

        assertEquals(true, value);
    }


    @Test
    void nichtAussteigen() throws RemoteException, UnknownHostException, InterruptedException {
        List<Karte> kartenList = new ArrayList<>();
        int numBotCards = 0;
        List<Karte> kartenList2 = new ArrayList<>();
        boolean value = false;
        SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
        sr.addBotSchwer();
        sr.addBotNormal();
        sr.stapelErstellen();
        sr.kartenVerteilen();

        Karte x = sr.getAblagestapelKarte();
        for (Spieler p : sr.getPlayerlist()) {
            if (p instanceof Bot_schwer) {
                numBotCards = p.getCards().size();
                for (int i = 0; i < numBotCards; i++) {
                    p.removeCard(p.getCards().get(0));
                }
                switch (x.kartenWert) {
                    case 1:
                        p.addCard(new Karte(10));
                        p.addCard(new Karte(10));
                        kartenList = p.getCards();
                        break;
                    case 2:
                        p.addCard(new Karte(1));
                        p.addCard(new Karte(1));
                        kartenList = p.getCards();
                        break;
                    case 3:
                        p.addCard(new Karte(2));
                        p.addCard(new Karte(2));
                        kartenList = p.getCards();
                        break;
                    case 4:
                        p.addCard(new Karte(3));
                        p.addCard(new Karte(3));
                        kartenList = p.getCards();
                        break;
                    case 5:
                        p.addCard(new Karte(4));
                        p.addCard(new Karte(4));
                        kartenList = p.getCards();
                        break;
                    case 6:
                        p.addCard(new Karte(5));
                        p.addCard(new Karte(5));
                        kartenList = p.getCards();
                        break;
                    case 10:
                        p.addCard(new Karte(6));
                        p.addCard(new Karte(6));
                        kartenList = p.getCards();
                        break;
                }
            }
        }

        assertEquals(2, kartenList.size());

        for (Spieler spieler : sr.getPlayerlist()) {
            if (spieler instanceof Bot_schwer) {
                int j = sr.getPlayerlist().indexOf(spieler);
                sr.setAmZug(sr.getPlayerlist().indexOf(spieler));
                ((Bot_schwer) spieler).play();
                sleep(1000);
                sleep(1000);
                if (!spieler.getAusgestiegen()) {
                    value = true;
                }
            }
        }
        assertEquals(true, value);
    }

    @Test
    void aufnehmen() throws RemoteException, InterruptedException,UnknownHostException {
        int numBotCards = 0;
        boolean value = false;
        SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
        sr.addBotSchwer();
        sr.addBotNormal();
        sr.stapelErstellen();
        sr.kartenVerteilen();
        int[] AnzahlKarten = {0,0};
        Karte x = sr.getAblagestapelKarte();
        for (Spieler p : sr.getPlayerlist()) {
            if (p instanceof Bot_schwer) {
                numBotCards = p.getCards().size();
                for (int i = 0; i < numBotCards; i++) {
                    p.removeCard(p.getCards().get(0));
                }
                switch (x.kartenWert) {
                    case 1:
                        p.addCard(new Karte(10));
                        p.addCard(new Karte(10));
                        break;
                    case 2:
                        p.addCard(new Karte(1));
                        p.addCard(new Karte(1));
                        break;
                    case 3:
                        p.addCard(new Karte(2));
                        p.addCard(new Karte(2));
                        break;
                    case 4:
                        p.addCard(new Karte(3));
                        p.addCard(new Karte(3));
                        break;
                    case 5:
                        p.addCard(new Karte(4));
                        p.addCard(new Karte(4));
                        break;
                    case 6:
                        p.addCard(new Karte(5));
                        p.addCard(new Karte(5));
                        break;
                    case 10:
                        p.addCard(new Karte(6));
                        p.addCard(new Karte(6));
                        break;
                }
            }
        }
        for (Spieler spieler : sr.getPlayerlist()) {
            if (spieler instanceof Bot_schwer) {
                AnzahlKarten[0] = spieler.getCards().size();
                int j = sr.getPlayerlist().indexOf(spieler);
                sr.setAmZug(sr.getPlayerlist().indexOf(spieler));
                ((Bot_schwer) spieler).play();
                sleep(1000);
                sleep(1000);
                AnzahlKarten[1] = spieler.getCards().size();
            }
        }
        assertNotEquals(AnzahlKarten[0], AnzahlKarten[1]);
    }

    @Test
    void ablegen() throws RemoteException, InterruptedException,UnknownHostException {
        int numBotCards = 0;
        boolean value = false;
        SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
        sr.addBotSchwer();
        sr.addBotNormal();
        sr.stapelErstellen();
        sr.kartenVerteilen();
        int[] AnzahlKarten = {0,0};
        Karte x = sr.getAblagestapelKarte();
        for (Spieler p : sr.getPlayerlist()) {
            if (p instanceof Bot_schwer) {
                numBotCards = p.getCards().size();
                for (int i = 0; i < numBotCards; i++) {
                    p.removeCard(p.getCards().get(0));
                }
                switch (x.kartenWert) {
                    case 1:
                        p.addCard(new Karte(1));
                        p.addCard(new Karte(2));
                        break;
                    case 2:
                        p.addCard(new Karte(2));
                        p.addCard(new Karte(3));
                        break;
                    case 3:
                        p.addCard(new Karte(3));
                        p.addCard(new Karte(4));
                        break;
                    case 4:
                        p.addCard(new Karte(4));
                        p.addCard(new Karte(5));
                        break;
                    case 5:
                        p.addCard(new Karte(5));
                        p.addCard(new Karte(6));
                        break;
                    case 6:
                        p.addCard(new Karte(6));
                        p.addCard(new Karte(10));
                        break;
                    case 10:
                        p.addCard(new Karte(10));
                        p.addCard(new Karte(1));
                        break;
                }
            }
        }
        for (Spieler spieler : sr.getPlayerlist()) {
            if (spieler instanceof Bot_schwer) {
                AnzahlKarten[0] = spieler.getCards().size();
                int j = sr.getPlayerlist().indexOf(spieler);
                sr.setAmZug(sr.getPlayerlist().indexOf(spieler));
                ((Bot_schwer) spieler).play();
                sleep(1000);
                sleep(1000);
                AnzahlKarten[1] = spieler.getCards().size();
            }
        }
        assertNotEquals(AnzahlKarten[0], AnzahlKarten[1]);
    }
    @Test
    void chipAbgeben() throws UnknownHostException {
        SpielraumImpl sr = new SpielraumImpl("raum", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), lobby);
        Bot_schwer bot_schwer = new Bot_schwer(sr);
        bot_schwer.addCard(new Karte(1));
        bot_schwer.chipskassieren();
        //bot hat nur einen weißen chip
        assertFalse(bot_schwer.chipAbgeben());
        //bot hat nur beide chip-Typen
        bot_schwer.addCard(new Karte(10));
        bot_schwer.chipskassieren();
        assertTrue(bot_schwer.chipAbgeben());
        bot_schwer.clearChips();
        bot_schwer.clearCards();
        bot_schwer.addCard(new Karte(10));
        bot_schwer.chipskassieren();
        //bot hat nur schwarzen Chip
        assertTrue(bot_schwer.chipAbgeben());
    }
}