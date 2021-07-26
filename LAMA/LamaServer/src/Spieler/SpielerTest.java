package Spieler;

import SpielRaumVerwaltung.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests fuer Spieler.
 */
class SpielerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void setgetAussgestiegen() {
        Spieler s = new Benutzer("a");
        assertEquals(false, s.getAusgestiegen());
        s.setAusgestiegen(true);
        assertEquals(true, s.getAusgestiegen());
    }

    @Test
    void setName(){
        Spieler s = new Benutzer("a");
        s.setName("b");
        assertEquals("b", s.getName());
    }

    @Test
    void removeCard() {
        Spieler s = new Benutzer("a");
        List<Karte> karteListe = new ArrayList<>();
        Karte k1 = new Karte(4);
        Karte k2 = new Karte(5);
        Karte k3 = new Karte(4);
        Karte k4 = new Karte(6);
        karteListe.add(k1);
        karteListe.add(k2);
        karteListe.add(k4);

        s.addCard(k1);
        s.addCard(k2);
        s.addCard(k3);
        s.addCard(k4);
        s.removeCard(k3);

        assertEquals(karteListe, s.getCards());

    }

    @Test
    void clearcards() {
        Spieler s = new Benutzer("a");
        List<Karte> karteListe = new ArrayList<>();
        Karte k1 = new Karte(4);
        Karte k2 = new Karte(5);
        Karte k3 = new Karte(4);
        Karte k4 = new Karte(6);

        s.addCard(k1);
        s.addCard(k2);
        s.addCard(k3);
        s.addCard(k4);
        s.clearCards();

        assertEquals(karteListe, s.getCards());
    }

    @Test
    void chipskassieren() {
        Spieler s = new Benutzer("a");
        s.addCard(new Karte(5));
        s.addCard(new Karte(5));
        s.addCard(new Karte(5));
        s.addCard(new Karte(3));
        s.addCard(new Karte(3));
        s.addCard(new Karte(10));
        s.addCard(new Karte(10));
        s.addCard(new Karte(10));
        s.chipskassieren();
        assertEquals(8, s.getWeißeChips());
        assertEquals(1, s.getSchwarzeChips());
    }

    @Test
    void getName() {
        Spieler s = new Benutzer("a");
        assertEquals(s.getName(), "a");
    }

    @Test
    void getCards() {
        Spieler s = new Benutzer("a");
        List<Karte> karteListe = new ArrayList<>();
        Karte k1 = new Karte(4);
        Karte k2 = new Karte(5);
        Karte k3 = new Karte(4);
        Karte k4 = new Karte(6);
        karteListe.add(k1);
        karteListe.add(k2);
        karteListe.add(k3);
        karteListe.add(k4);
        s.addCard(k1);
        s.addCard(k2);
        s.addCard(k3);
        s.addCard(k4);
        assertEquals(s.getCards(), karteListe);
    }

    @Test
    void addCard() {
        Spieler s = new Benutzer("a");
        List<Karte> karteListe = new ArrayList<>();
        Karte k1 = new Karte(4);
        karteListe.add(k1);
        s.addCard(k1);
        assertEquals(s.getCards(), karteListe);
    }

    @Test
    void abgebenWeiß() {
        Spieler s = new Benutzer("a");
        s.addCard(new Karte(5));
        s.chipskassieren();
        s.abgebenWeiß();
        assertEquals(s.getWeißeChips(), 4);
    }

    @Test
    void getMinusPunkte() {
        Spieler s = new Benutzer("a");
        s.addCard(new Karte(10));
        s.addCard(new Karte(3));
        s.addCard(new Karte(3));
        s.addCard(new Karte(7));
        assertEquals(20,s.getMinusPunkte());
    }

    @Test
    void abgebenSchwarz() {
        Spieler s = new Benutzer("a");
        s.addCard(new Karte(10));
        s.chipskassieren();
        s.abgebenSchwarz();
        assertEquals(s.getSchwarzeChips(), 0);
    }

    @Test
    void clearChips() {
        Spieler s = new Benutzer("a");
        s.addCard(new Karte(5));
        s.addCard(new Karte(5));
        s.addCard(new Karte(5));
        s.addCard(new Karte(3));
        s.addCard(new Karte(3));
        s.addCard(new Karte(10));
        s.addCard(new Karte(10));
        s.addCard(new Karte(10));
        s.chipskassieren();
        s.clearChips();
        assertEquals(0, s.getWeißeChips());
        assertEquals(0, s.getSchwarzeChips());
    }
    @Test
    void incrementGetGewonneneSpiele(){
        Spieler s = new Benutzer("a");
        assertEquals(0,s.getGewonneneSpiele());
        s.incrementGewonneneSpiele();
        assertEquals(1,s.getGewonneneSpiele());
    }
}