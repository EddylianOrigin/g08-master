package Spieler;

import ServerRMI.SpielraumImpl;
import SpielRaumVerwaltung.Karte;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasse fuer Bots des normalen Schwierigkeitsgrades.
 * Fuehrt die Spielzuege der Bots aus.
 */
public class Bot_normal extends Spieler implements Bot{
    private SpielraumImpl sr;
    public Bot_normal(SpielraumImpl sr){
        super( "Bot(normal)");
        this.sr = sr;
    }

    /**
     * Fuehrt die Spielzuege der normalen Bots aus.
     */
    public void play(){
        //nur temporär zum testen
        /*
        try {
            sr.aussteigen();
            //sr.aufnehmen();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        */
        boolean kannAblegen = false;
        List<Karte> zumAblegenList = new ArrayList<>();
        Karte zumAblegen;
        boolean andereSpielerEineKarte = false;
        boolean[] chances = {true, true, true, true, true, true, true, true, false, false};
        int rnd = new Random().nextInt(chances.length);

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

        try {
            /* Wenn Bot ablegen kann, legt dabei die Karte mit dem höchsten Kartenwert ab*/
            if (kannAblegen && chances[rnd]) {
                zumAblegen = zumAblegenList.get(0);
                for (Karte karte : zumAblegenList) {
                    if (karte.kartenWert >= zumAblegen.kartenWert) {
                        zumAblegen = karte;
                    }
                }
                //Index der Karte herausfinden
                int kartenIndex = this.getCards().indexOf(zumAblegen);
                //und Karte ablegen
                sr.ablegen(kartenIndex);

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

                    if(!sr.isNachziehstapelEmpty() && i<(sr.getSize()-1)) {
                        sr.aufnehmen();
                    }
                    else {
                        sr.aussteigen();
                    }
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * Waehlt einen Chip zum abgeben aus und gibt zurueck, ob der ausgewaehlte Chip schwarz ist, oder nicht.
     * Der normale Bot gibt mit einer Wahrscheinlichkeit von 80% einen schwarzen Chips ab, sofern er einen besitzt.
     * @return true, falls der ausgewaehlte Chip schwarz ist, false wenn nicht.
     */
    @Override
    public boolean chipAbgeben() {
        if(getSchwarzeChips()>0 && getWeißeChips()>0){
            //Zufallsgenerator erzeugen (0-99)
            Random rng = new Random();
            int zufallsZahl = rng.nextInt(100);
            //falls die 80% Wahrscheinlichkeit eingetreten ist, gebe einen schwarzen Chip ab, wenn nicht einen weißen
            return zufallsZahl < 80;
        }
        else {
            //gebe Chip Typ ab, den der Spieler besitzt
            return getSchwarzeChips()>0;
        }

    }


}
