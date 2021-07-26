package SpielRaumVerwaltung;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KarteTest {

    @Test
    void LamaKarte(){
        Karte lama = new Karte(10);
        Karte andereKarte1 = new Karte(1);
        Karte andereKarte2 = new Karte(2);
        Karte andereKarte3 = new Karte(3);
        Karte andereKarte4 = new Karte(4);
        Karte andereKarte5 = new Karte(5);
        Karte andereKarte6 = new Karte(6);
        Karte andereKarteLama = new Karte(10);
        assertTrue(lama.canBePlacedOnTopOf(andereKarte6));
        assertTrue(lama.canBePlacedOnTopOf(andereKarteLama));
        assertFalse(lama.canBePlacedOnTopOf(andereKarte1));
        assertFalse(lama.canBePlacedOnTopOf(andereKarte2));
        assertFalse(lama.canBePlacedOnTopOf(andereKarte3));
        assertFalse(lama.canBePlacedOnTopOf(andereKarte4));
        assertFalse(lama.canBePlacedOnTopOf(andereKarte5));

    }
    @Test
    void Karte1(){
        Karte karte1 = new Karte(1);
        Karte andereKarte1 = new Karte(1);
        Karte andereKarte2 = new Karte(2);
        Karte andereKarte3 = new Karte(3);
        Karte andereKarte4 = new Karte(4);
        Karte andereKarte5 = new Karte(5);
        Karte andereKarte6 = new Karte(6);
        Karte andereKarteLama = new Karte(10);
        assertTrue(karte1.canBePlacedOnTopOf(andereKarte1));
        assertTrue(karte1.canBePlacedOnTopOf(andereKarteLama));
        assertFalse(karte1.canBePlacedOnTopOf(andereKarte6));
        assertFalse(karte1.canBePlacedOnTopOf(andereKarte2));
        assertFalse(karte1.canBePlacedOnTopOf(andereKarte3));
        assertFalse(karte1.canBePlacedOnTopOf(andereKarte4));
        assertFalse(karte1.canBePlacedOnTopOf(andereKarte5));
    }
    @Test
    void andereKarten(){
        for(int i = 2; i<=6; i++){
            Karte karte = new Karte(i);
            assertTrue(karte.canBePlacedOnTopOf(new Karte(i-1)));
            assertTrue(karte.canBePlacedOnTopOf(new Karte(i)));
            assertFalse(karte.canBePlacedOnTopOf(new Karte(10)));

            //andere Karten nocht testen
            for(int j = 1; j<=6; j++){
                if(j==i||j==i-1){
                    continue;
                }
                assertFalse(karte.canBePlacedOnTopOf(new Karte(j)));
            }
        }
    }
}