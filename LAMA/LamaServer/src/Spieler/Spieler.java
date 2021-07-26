package Spieler;

import SpielRaumVerwaltung.Karte;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse fuer die Akteure des eigentlichen Spiels.
 */
public abstract class Spieler {
    private String name;
    private int weißeChips = 0;
    private int schwarzeChips = 0;
    private int gewonneneSpiele = 0;
    private boolean ausgestiegen = false;
    private List<Karte> karten = new ArrayList<>();

    public Spieler( String name){
        this.name = name;
    }

    /**
     * Kopiert die Informationen/Werte der Attribute eines anderes Spielers.
     * @param spieler Spieler, dessen Informationen kopiert werden sollen.
     */
    public void copySpielerInfo (Spieler spieler) {
        this.weißeChips = spieler.getWeißeChips();
        this.schwarzeChips = spieler.getSchwarzeChips();
        this.gewonneneSpiele = spieler.getGewonneneSpiele();
        this.ausgestiegen = spieler.getAusgestiegen();
        this.karten = spieler.getCards();
    }


    /**
     * Gibt aus die Name des Spielers.
     * @return Name des Spielers als String.
     */
    public String getName() {
        return name;
    }

    /**
     * Setze den Name des aktuellen Spieler.
     * @param name neuer Name des Spielers.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt aus eine Liste der Karten des Spielers.
     * @return Karten als Liste.
     */
    public List<Karte> getCards() {
        return karten;
    }
    /**
     * Fuegt eine Karte in der Hand des Spielers hinzu.
     * @param k Karte die hinzugefügt wird.
     */
    public void addCard(Karte k) {
        karten.add(k);
    }
    /**
     * Entfernt eine Karte aus der Hand des Spielers.
     * @param k Karte die zu entfernen ist.
     */
    public void removeCard(Karte k){
        karten.remove(k);
    }

    /**
     * Entfernt alle Karten aus der Hand des Spielers.
     */
    public void clearCards(){
        karten.clear();
    }
    /**
     * Berechnet die Minuspunkte des Spielers und erhoeht darauf basierend die Anzahl an weisse und schwarze Chips
     */
    public void chipskassieren() {
        int sum=getMinusPunkte();
        // Weiße- bzw Schwarzechips entsprechend kassieren
        while(sum>0){
            if (sum>9){
                schwarzeChips++;
                sum = sum - 10;
            } else {
                weißeChips++;
                sum = sum - 1;
            }
        }

        }
    /**
     * Reduziert die Anzahl an weisse Chips des Spielers um eins.
     */
    public void abgebenWeiß() {
        weißeChips--;
    }
    /**
     * Reduziert die Anzahl an schwarze Chips des Spielers um eins.
     */
    public void abgebenSchwarz() {
        schwarzeChips--;
    }
    /**
     * Gibt aus die Anzahl an gewonenne Spiele des Spielers.
     * @return die Anzahl an gewonenne Spiele des Spielers.
     */
    public int getGewonneneSpiele() {
        return gewonneneSpiele;
    }
    /**
     * Gibt aus die Anzahl an weisse Chips.
     * @return Anzahl an weisse Chips.
     */
    public int getWeißeChips() {
        return weißeChips;
    }
    /**
     * Gibt aus die Anzahl an schwarze Chips.
     * @return Anzahl an schwarze Chips.
     */
    public int getSchwarzeChips() {
        return schwarzeChips;
    }
    /**
     * Entfernt alle Chips des Spielers.
     */
    public void clearChips(){
        weißeChips=0;
        schwarzeChips=0;
    }
    /**
     * Gibt aus, ob der Spieler aus dem Durchgang ausgestiegen ist.
     * @return true wenn der Spieler bereits aus dem Durchgang ausgestiegen ist, sonst false
     */
    public boolean getAusgestiegen(){
        return ausgestiegen;
    }

    /**
     * Legt fest, ob der Spieler aus dem Durchgang ausgestiegen ist.
     * @param ausgestiegen legt fest, ob der Spieler ausgestiegen ist, oder nicht.
     */
    public void setAusgestiegen(boolean ausgestiegen){
        this.ausgestiegen = ausgestiegen;
    }

    /**
     * Erhoeht die Anzahl der gewonnene Spiele des Spielers um 1.
     */
    public void incrementGewonneneSpiele() {
        this.gewonneneSpiele++;
    }

    /**
     * Gibt aus die Anzahl der Minuspunkte von der Karten die der Spieler im Hand hat.
     * @return Summe der Minuspunkten.
     */
    public int getMinusPunkte(){
        int sum = 0;
        List<Integer> newList = new ArrayList<>();
        // Duplikaten aus der Hand des Spielers entfernen, da gleiche Karten nur einmal gezählt werden (zB. 3 vierer, 4 Minuspunkte)
        for (Karte karte: this.karten){
            if(!newList.contains(karte.kartenWert)){
                newList.add(karte.kartenWert);
            }
        }
        for (int i: newList){
            sum = sum + i;
        }
        return sum;
    }



}
