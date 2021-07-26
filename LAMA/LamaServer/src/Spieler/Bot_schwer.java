package Spieler;

import ServerRMI.SpielraumImpl;
import SpielRaumVerwaltung.Karte;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse fuer Bots des schweren Schwierigkeitsgrades.
 * Fuehrt die Spielzuege der Bots aus.
 */
public class Bot_schwer extends Spieler implements Bot{
    private SpielraumImpl sr;
    public Bot_schwer(SpielraumImpl sr){
        super("Bot(schwer)");
        this.sr = sr;
    }
    /**
     * Fuehrt die Spielzuege der schweren Bots aus.
     */
    public void play(){

        boolean kannAblegen = false;
        List<Karte> zumAblegenList = new ArrayList<>();
        Karte zumAblegen=null;
        boolean andereSpielerEineKarte = false;
        int anzahlEinsen=0;
        int anzahlLamas=0;
        int anzahlZweier=0;
        int anzahlDreier=0;
        int anzahlVierer=0;
        int anzahlFuenfer=0;
        int anzahlSechser=0;
        boolean mehrereMoeglichkeiten = false;

        /* checken ob Bot eine Karte zum ablegen hat, wenn ja, füge die Karten die er ablegen kann in der zumAblegenList Liste  */
        for (Karte karte : this.getCards()){
            if (karte.canBePlacedOnTopOf(sr.getAblagestapelKarte())){
                zumAblegenList.add(karte);
            }
        }
        /* wenn zumAblegenList nicht leer ist, Bot kann ablegen  */
        if (!zumAblegenList.isEmpty()){
            kannAblegen=true;
        }

        for (Karte karte2 : zumAblegenList) {
            if (karte2.kartenWert == 1) {
                anzahlEinsen++;
            } else {
                if (karte2.kartenWert == 10) {
                    anzahlLamas++;
                } else {
                    if (karte2.kartenWert == 2) {
                        anzahlZweier++;
                    } else {
                        if (karte2.kartenWert == 3) {
                            anzahlDreier++;
                        } else {
                            if (karte2.kartenWert == 4) {
                                anzahlVierer++;
                            } else {
                                if (karte2.kartenWert == 5) {
                                    anzahlFuenfer++;
                                } else {
                                    if (karte2.kartenWert == 6) {
                                        anzahlSechser++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        try {
            /* Wenn Bot ablegen kann, legt dabei die Karte mit dem höchsten Kartenwert ab*/
            if (kannAblegen) {
                /* Fall 2: es liegt eine Lama-Karte auf dem Ablagestapel */
                if (sr.getAblagestapelKarte().kartenWert == 10) {
                    /* Falls er mehrere Moeglichkeiten hat */
                    if (anzahlEinsen > 0 && anzahlLamas > 0) {
                        /* mindestens 2 LamaKarten mehr auf der Hand hat als Karten des Kartenwertes 1 */
                        if (anzahlLamas >= anzahlEinsen + 2) {
                            for (Karte karte : zumAblegenList) {
                                if (karte.kartenWert == 1) {
                                    zumAblegen = karte;
                                    break;
                                }
                            }
                            //Index der Karte herausfinden
                            int kartenIndex = this.getCards().indexOf(zumAblegen);
                            //und Karte ablegen
                            sr.ablegen(kartenIndex);

                        }
                        /*Falls er mehrere Moeglichkeiten hat und er nicht mindestens 2 LamaKarten mehr auf der Hand hat als Karten des Kartenwertes 1, dann legt er eine Lama-Karte ab.*/
                        else {
                            for (Karte karte : zumAblegenList) {
                                if (karte.kartenWert == 10) {
                                    zumAblegen = karte;
                                    break;
                                }
                            }
                            //Index der Karte herausfinden
                            int kartenIndex = this.getCards().indexOf(zumAblegen);
                            //und Karte ablegen
                            sr.ablegen(kartenIndex);
                        }
                    }
                    /*– Falls er nicht mehrere Moeglichkeiten hat, legt er die Karte ab die er ablegen darf/kann.*/
                    else{
                        zumAblegen=zumAblegenList.get(0);
                        //Index der Karte herausfinden
                        int kartenIndex = this.getCards().indexOf(zumAblegen);
                        //und Karte ablegen
                        sr.ablegen(kartenIndex);
                    }

                }
                /*Fall 1: es liegt keine Lama-Karte auf dem Ablagestapel*/
                else {
                    Karte karteTest=zumAblegenList.get(0);
                    for (Karte karte : zumAblegenList){
                        if(karteTest.kartenWert>karte.kartenWert||karteTest.kartenWert<karte.kartenWert){
                            mehrereMoeglichkeiten=true;
                        }
                    }/*Falls er mehrere Moeglichkeiten hat, legt er zuerst eine der Karten ab, von denen er eine geringere Anzahl auf der Hand hat. */
                    if (mehrereMoeglichkeiten) {
                        if (sr.getAblagestapelKarte().kartenWert == 1) {
                            if (anzahlEinsen < anzahlZweier) {
                                for (Karte karte : zumAblegenList) {
                                    if (karte.kartenWert == 1) {
                                        zumAblegen = karte;
                                        break;
                                    }
                                }
                                //Index der Karte herausfinden
                                int kartenIndex = this.getCards().indexOf(zumAblegen);
                                //und Karte ablegen
                                sr.ablegen(kartenIndex);
                            } else {
                                for (Karte karte : zumAblegenList) {
                                    if (karte.kartenWert == 2) {
                                        zumAblegen = karte;
                                        break;
                                    }
                                }
                                //Index der Karte herausfinden
                                int kartenIndex = this.getCards().indexOf(zumAblegen);
                                //und Karte ablegen
                                sr.ablegen(kartenIndex);
                            }
                        } else {
                            if (sr.getAblagestapelKarte().kartenWert == 2) {
                                if (anzahlZweier < anzahlDreier) {
                                    for (Karte karte : zumAblegenList) {
                                        if (karte.kartenWert == 2) {
                                            zumAblegen = karte;
                                            break;
                                        }
                                    }
                                    //Index der Karte herausfinden
                                    int kartenIndex = this.getCards().indexOf(zumAblegen);
                                    //und Karte ablegen
                                    sr.ablegen(kartenIndex);
                                } else {
                                    for (Karte karte : zumAblegenList) {
                                        if (karte.kartenWert == 3) {
                                            zumAblegen = karte;
                                            break;
                                        }
                                    }
                                    //Index der Karte herausfinden
                                    int kartenIndex = this.getCards().indexOf(zumAblegen);
                                    //und Karte ablegen
                                    sr.ablegen(kartenIndex);
                                }
                            }
                            else {
                                if (sr.getAblagestapelKarte().kartenWert == 3) {
                                    if (anzahlDreier < anzahlVierer) {
                                        for (Karte karte : zumAblegenList) {
                                            if (karte.kartenWert == 3) {
                                                zumAblegen = karte;
                                                break;
                                            }
                                        }
                                        //Index der Karte herausfinden
                                        int kartenIndex = this.getCards().indexOf(zumAblegen);
                                        //und Karte ablegen
                                        sr.ablegen(kartenIndex);
                                    } else {
                                        for (Karte karte : zumAblegenList) {
                                            if (karte.kartenWert == 4) {
                                                zumAblegen = karte;
                                                break;
                                            }
                                        }
                                        //Index der Karte herausfinden
                                        int kartenIndex = this.getCards().indexOf(zumAblegen);
                                        //und Karte ablegen
                                        sr.ablegen(kartenIndex);
                                    }
                                }
                                else {
                                    if (sr.getAblagestapelKarte().kartenWert == 4) {
                                        if (anzahlVierer < anzahlFuenfer) {
                                            for (Karte karte : zumAblegenList) {
                                                if (karte.kartenWert == 4) {
                                                    zumAblegen = karte;
                                                    break;
                                                }
                                            }
                                            //Index der Karte herausfinden
                                            int kartenIndex = this.getCards().indexOf(zumAblegen);
                                            //und Karte ablegen
                                            sr.ablegen(kartenIndex);
                                        } else {
                                            for (Karte karte : zumAblegenList) {
                                                if (karte.kartenWert == 5) {
                                                    zumAblegen = karte;
                                                    break;
                                                }
                                            }
                                            //Index der Karte herausfinden
                                            int kartenIndex = this.getCards().indexOf(zumAblegen);
                                            //und Karte ablegen
                                            sr.ablegen(kartenIndex);
                                        }
                                    }
                                    else {
                                        if (sr.getAblagestapelKarte().kartenWert == 5) {
                                            if (anzahlFuenfer < anzahlSechser) {
                                                for (Karte karte : zumAblegenList) {
                                                    if (karte.kartenWert == 5) {
                                                        zumAblegen = karte;
                                                        break;
                                                    }
                                                }
                                                //Index der Karte herausfinden
                                                int kartenIndex = this.getCards().indexOf(zumAblegen);
                                                //und Karte ablegen
                                                sr.ablegen(kartenIndex);
                                            } else {
                                                for (Karte karte : zumAblegenList) {
                                                    if (karte.kartenWert == 6) {
                                                        zumAblegen = karte;
                                                        break;
                                                    }
                                                }
                                                //Index der Karte herausfinden
                                                int kartenIndex = this.getCards().indexOf(zumAblegen);
                                                //und Karte ablegen
                                                sr.ablegen(kartenIndex);
                                            }
                                        }
                                        else {
                                            if (sr.getAblagestapelKarte().kartenWert == 6) {
                                                if (anzahlSechser < anzahlLamas) {
                                                    for (Karte karte : zumAblegenList) {
                                                        if (karte.kartenWert == 6) {
                                                            zumAblegen = karte;
                                                            break;
                                                        }
                                                    }
                                                    //Index der Karte herausfinden
                                                    int kartenIndex = this.getCards().indexOf(zumAblegen);
                                                    //und Karte ablegen
                                                    sr.ablegen(kartenIndex);
                                                } else {
                                                    for (Karte karte : zumAblegenList) {
                                                        if (karte.kartenWert == 10) {
                                                            zumAblegen = karte;
                                                            break;
                                                        }
                                                    }
                                                    //Index der Karte herausfinden
                                                    int kartenIndex = this.getCards().indexOf(zumAblegen);
                                                    //und Karte ablegen
                                                    sr.ablegen(kartenIndex);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }/*– Falls er nicht mehrere Moeglichkeiten hat, legt er die Karte ab die er ablegen darf/kann.*/
                    else{
                        zumAblegen=zumAblegenList.get(0);
                        //Index der Karte herausfinden
                        int kartenIndex = this.getCards().indexOf(zumAblegen);
                        //und Karte ablegen
                        sr.ablegen(kartenIndex);
                    }
                }
            }
            /* Bot hat keine Karte zum ablegen  */
            else {
                /* Checken ob es ein Spieler gibt, der nur eine Karte auf der Hand hat*/
                for (Spieler sp : sr.getPlayerlist()) {
                    if (sp.getCards().size() == 1 && !(this==sp) && !sp.getAusgestiegen()) {
                        andereSpielerEineKarte = true;
                        break;
                    }
                }
                /* Bot aussteigen Condition*/
                if (andereSpielerEineKarte && ((this.getMinusPunkte() + this.getWeißeChips() + (this.getSchwarzeChips() * 10)) < 40)) {
                    sr.aussteigen();
                } else {
                    /* Bot nimmt eine Karte auf da er nicht ablegen kann und nicht aussteigen will*/
                    //es sei denn, er kann nicht aufnehmen, da keine Karte mehr auf dem Nachziehstapel ist.
                    int i=0;
                    for (Spieler sp : sr.getPlayerlist()){
                        if (sp.getAusgestiegen() && !(this==sp)){
                            i++;
                        }
                    }
                    if (!sr.isNachziehstapelEmpty() && i<(sr.getSize() - 1)) {
                        sr.aufnehmen();
                    } else {
                        sr.aussteigen();
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waehlt einen Chip zum abgeben aus und gibt zurueck, ob der ausgewaehlte Chip schwarz ist, oder nicht.
     * Der normale Bot immer einen schwarzen Chips ab, sofern er einen besitzt.
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     */
    @Override
    public boolean chipAbgeben() {
        return this.getSchwarzeChips() > 0;
    }
}