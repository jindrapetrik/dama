
package model;
import view.*;
import java.util.*;

/**
 * Tato trida obsahuje model hry - urcuje system, jak hra funguje
 *
 * @author petrij2
 */
public class ModelHry {

    /** uroven vypisovani zprav */
    public static int debugLevel = 1;
    /** Pokud pole nevlastni zadny hrac */
    public static final int ZADNY_HRAC = -1;
    /** Udava zda je policko vybarvene - zda tam hrac muze tahnout */
    public static final int OZNACENE_POLE = -1;
    /** Typ pole - pesak */
    public static final int TYP_PESAK = 1;
    /** Typ pole - dama */
    public static final int TYP_DAMA = 2; 
    /** Hrac na tomto pocitaci */
    public static final int TYPHRACE_ZDEJSI = 1;
    /** Hrac na vzdalenem pocitaci */
    public static final int TYPHRACE_SITOVY = 2;
    /** Umela inteligence */
    public static final int TYPHRACE_PC = 3;
    /** Typ hry po siti */
    public static final int TYPHRY_SITOVA = 1;
    /** Hra sam se sebou */
    public static final int TYPHRY_PC = 2; 
    /** Urcuje typ aktualni hry */
    public static int typHry = TYPHRY_PC;
    
    /** Typ pripojeni - zaklada hru */
    public static final int TYPPRIPOJENI_ZAKLADA = 1;
    /** Typ pripojeni - pripoju je se */
    public static final int TYPPRIPOJENI_PRIPOJUJESE = 2; 
    
    /** Typ pripojeni - vizte vyse */
    public static int typPripojeni = 0;
    /** Cislo hrace ktery prave hraje 0 nebo 1 */
    //public static int hracNaTahu = 0;
    /** Urcuje oznaceneho pesaka/damu */
    public static XY vybranePole = null;
    //public static int pole[][] = new int[Nastaveni.velikostDesky][Nastaveni.velikostDesky]; //Zde jsou ulozeny data mapy - pesaci a damy
    /** Relativni policka na ktera tahne pesak dopredu, prvni index je cislo hrace */
    public static XY smeryPesakVpred[][] = {{new XY(-1, -1), new XY(1, -1)}, {new XY(-1, 1), new XY(1, 1)}};    
    /** Relativni policka na ktera tahne pesak dopredu i dozadu, prvni index je cislo hrace*/
    public static XY smeryPesakVpredIVzad[][] = {{new XY(-1, -1), new XY(1, -1), new XY(-1, 1), new XY(1, 1)}, {new XY(-1, -1), new XY(1, -1), new XY(-1, 1), new XY(1, 1)}};
    /** Relativni smery, kam tahne dama */
    public static XY smeryDama[] = {new XY(-1, -1), new XY(1, -1), new XY(-1, 1), new XY(1, 1)};    
    
    
    /** typy hracu viz vyse */
    public static int typyHracu[] = {TYPHRACE_ZDEJSI, TYPHRACE_PC};
    /** pokud je true, hra se ukoncuje */
    public static boolean vypinani = false; 
    
    /** urcuje aktualni stav desky - pozice kamenu */
    public static Stav stav;

    
    /**
     * tato metoda vypise uzivateli zpravu
     *
     * @param s retezec na vypsani
     */
    public static void debugWrite(String s) {
        debugWrite(s, 0);
    }

    /**
     * tato metoda vypise uzivateli zpravu s urcitou urovni debugu
     *
     * @param s retezec na vypsani
     * @param k uroven debugu
     */
    public static void debugWrite(String s, int k) {
        
        //pokud je tato uroven vetsi nez debuglevel, nevypise se
        if (debugLevel < k) {
            return;
        }
        view.HlavniFrame.lstLog.add(s);
        view.HlavniFrame.lstLog.makeVisible(view.HlavniFrame.lstLog.getItemCount() - 1);
    }

    /**
     * vybere policko s urcitymi souradnicemi
     *
     * @param x xova souradnice
     * @param y yova souradnice
     */
    public static void vyberPole(int x, int y) {
        vyberPole(new XY(x, y));
    }

    /**
     * vybere policko s urcitymi souradnicemi
     *
     * @param novePole XY objekt se souradnicemi
     */
    public static void vyberPole(XY novePole) {
        odOznacPole();
        int vlastnik = stav.vlastnikXY(novePole);
        if (vlastnik == ZADNY_HRAC) {
            return;
        }
        vybranePole = novePole;
        LinkedList<Tah> list=stav.vratMozneTahyXY(novePole);         
        for ( Tah tah:list ) 
          stav.oznacPole(tah.kam);       
    }

    

    
    /**
     * metoda odoznaci vsechna pole
     */
    public static void odOznacPole() {
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                if (stav.pole[x][y] < 0) {
                    stav.pole[x][y] = 0;
                }
            }
        }
    }

    /**
     * metoda kontroluje, zda nekdo vyhral
     *
     * @return true pokud nekdo vyhral
     */
    public static boolean testVyhry() {
        boolean muzeHrat = false;  
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                int vlastnik = stav.vlastnikXY(x, y);
                if (vlastnik == stav.hracNaTahu) {
                    if (!muzeHrat) {                        
                        boolean mch=stav.muzeChoditXY(x, y);
                        boolean msk=stav.musiSkakatXY(x, y);
                        muzeHrat = mch||msk;
                    }
                }
            }
        }
        int vyhral = ZADNY_HRAC;
        if(!muzeHrat)
            vyhral=ModelHry.druhyHrac();

        if (vyhral > ZADNY_HRAC) {
            View.hlavniFrame.repaint();
            debugWrite("Hráč " + view.View.jmenaHracu[vyhral] + " vyhrál");
            if (typHry == TYPHRY_SITOVA) {
                if (typyHracu[vyhral] == TYPHRACE_ZDEJSI) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Vyhrál(a) jste, gratujuji!", "Výhra", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "Bohužel jste prohrál(a), smůla.", "Prohra", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Vyhrál " + view.View.jmenaHracu[vyhral] + " hráč", "Výhra", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
            ModelHry.konecHry();
            return true;
        }
        return false;
    }
    
    /**
     * metoda ukonci hru
     */
    public static void konecHry()
    {                 
        vypinani=true;
        if(typHry==TYPHRY_SITOVA)
        {
            Sit.ukoncit();                          
        }
        if(View.hlavniFrame!=null)
        View.hlavniFrame.setVisible(false);
        View.uvodniFrame.setVisible(true);
    }

    

    /**
     * metoda vraci cislo hrace, ktery neni na tahu
     *
     * @return cislo druheho hrace - ktery neni na tahu
     */
    public static int druhyHrac() {
        if (stav.hracNaTahu == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * metoda vraci cislo hrace, ktery neni zadany - toho druheho
     *
     * @param hrac aktualni hrac
     * @return cislo druheho hrace
     */
    public static int druhyHrac(int hrac) {
        if (hrac == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * nastavi model pro novou hru
     */
    public static void novaHra() {        
        HlavniFrame.lstZpravy.removeAll();
        HlavniFrame.lstLog.removeAll();
        vybranePole = null;
        stav=new Stav(0);
        /*pole=new int[Nastaveni.velikostDesky][Nastaveni.velikostDesky];
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                pole[x][y] = 0;
            }
        }*/

       /* stav.pole[0][7] = TYP_DAMA + 2;
        stav.pole[4][5] = TYP_PESAK;
        stav.pole[4][1] = TYP_PESAK;
        */
        //standardni rozestaveni:
        for (int y = 0; y < Nastaveni.pocetRadNaStartu; y++) {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
                if (stav.jeObyvatelneXY(x, y)) {
                    stav.pole[x][y] = TYP_PESAK + 2;
                }
            }
        }
        for (int y = Nastaveni.velikostDesky - Nastaveni.pocetRadNaStartu; y < Nastaveni.velikostDesky; y++) {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
                if (stav.jeObyvatelneXY(x, y)) {
                    stav.pole[x][y] = TYP_PESAK;
                }
            }
        }
        View.hlavniFrame=new HlavniFrame();
        View.hlavniFrame.setVisible(true);
    }

   
    

    
    
    
    
}
