
package model;
import view.*;

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
    /** Typ hry po siti */
    public static final int TYPHRY_SITOVA = 1;
    /** Hra sam se sebou */
    public static final int TYPHRY_SAM = 2; 
    /** Urcuje typ aktualni hry */
    public static int typHry = TYPHRY_SAM;
    
    /** Typ pripojeni - zaklada hru */
    public static final int TYPPRIPOJENI_ZAKLADA = 1;
    /** Typ pripojeni - pripoju je se */
    public static final int TYPPRIPOJENI_PRIPOJUJESE = 2; 
    
    /** Typ pripojeni - vizte vyse */
    public static int typPripojeni = 0;
    /** Cislo hrace ktery prave hraje 0 nebo 1 */
    public static int hracNaTahu = 0;
    /** Urcuje oznaceneho pesaka/damu */
    public static XY vybranePole = null;
    public static int pole[][] = new int[Nastaveni.velikostDesky][Nastaveni.velikostDesky]; //Zde jsou ulozeny data mapy - pesaci a damy
    /** Relativni policka na ktera tahne pesak dopredu, prvni index je cislo hrace */
    public static XY smeryPesakVpred[][] = {{new XY(-1, -1), new XY(1, -1)}, {new XY(-1, 1), new XY(1, 1)}};    
    /** Relativni policka na ktera tahne pesak dopredu i dozadu, prvni index je cislo hrace*/
    public static XY smeryPesakVpredIVzad[][] = {{new XY(-1, -1), new XY(1, -1), new XY(-1, 1), new XY(1, 1)}, {new XY(-1, -1), new XY(1, -1), new XY(-1, 1), new XY(1, 1)}};
    /** Relativni smery, kam tahne dama */
    public static XY smeryDama[] = {new XY(-1, -1), new XY(1, -1), new XY(-1, 1), new XY(1, 1)};    
    
    /** Urcuje zda musi hrac skakat - je zde TYP policka - dama nebo pesak */
    public static int musiSkakat = 0;
    /** Policko, kde musi skakat (protoze tam v predchozim tahu skocil) */
    public static XY musiSkakatNa = null;
    /** typy hracu viz vyse */
    public static int typyHracu[] = {TYPHRACE_ZDEJSI, TYPHRACE_ZDEJSI};
    /** pokud je true, hra se ukoncuje */
    public static boolean vypinani = false; 

    
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
        int vlastnik = vlastnikXY(novePole);
        int typ = typKameneXY(novePole);
        if (vlastnik == ZADNY_HRAC) {
            return;
        }
        vybranePole = novePole;
        if (typ == TYP_PESAK) {
            if (musiSkakat == TYP_DAMA) {
                return;
            }
            boolean skaka = false;
            //skakani:

            XY smeryPesakSkace[][] = ModelHry.smeryPesakVpred;
            if (Nastaveni.pesakSkaceDozadu) {
                smeryPesakSkace = ModelHry.smeryPesakVpredIVzad;
            }

            for (int i = 0; i < smeryPesakSkace[vlastnik].length; i++) {
                XY poleZa = new XY(novePole.x + smeryPesakSkace[vlastnik][i].x * 2,
                        novePole.y + smeryPesakSkace[vlastnik][i].y * 2);
                XY poleMezi = new XY(novePole.x + smeryPesakSkace[vlastnik][i].x, novePole.y + smeryPesakSkace[vlastnik][i].y);

                if (jeObyvatelneXY(poleZa)) {
                    if (jeObyvatelneXY(poleMezi)) {
                        if (vlastnikXY(poleMezi) == ModelHry.druhyHrac(vlastnik)) {
                            if (ModelHry.vlastnikXY(poleZa) == ZADNY_HRAC) {
                                oznacPole(poleZa);
                                skaka = true;
                            }
                        }
                    }
                }
            }

            if (skaka) {
                return;
            }
            if (musiSkakat > 0) {
                return;
            }
            
            for (int i = 0; i < ModelHry.smeryPesakVpred[vlastnik].length; i++) {
                XY oznacit = new XY(novePole.x + ModelHry.smeryPesakVpred[vlastnik][i].x, novePole.y + ModelHry.smeryPesakVpred[vlastnik][i].y);
                oznacPole(oznacit);
            }
        }
        
        if ((typ == TYP_DAMA)&&(!Nastaveni.letajiciDamy))
        {
            boolean skaka=false;
            //skakani:
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                XY poleZa = new XY(novePole.x + ModelHry.smeryDama[i].x * 2,
                        novePole.y + ModelHry.smeryDama[i].y * 2);
                XY poleMezi = new XY(novePole.x + ModelHry.smeryDama[i].x, novePole.y + ModelHry.smeryDama[i].y);

                if (jeObyvatelneXY(poleZa)) {
                    if (jeObyvatelneXY(poleMezi)) {
                        if (vlastnikXY(poleMezi) == ModelHry.druhyHrac(vlastnik)) {
                            if (ModelHry.vlastnikXY(poleZa) == ZADNY_HRAC) {
                                oznacPole(poleZa);
                                skaka = true;
                            }
                        }
                    }
                }
            }

            if (skaka) {
                return;
            }
            if (musiSkakat > 0) {
                return;
            }
            
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                XY oznacit = new XY(novePole.x + ModelHry.smeryDama[i].x, novePole.y + ModelHry.smeryDama[i].y);
                oznacPole(oznacit);
            }
        }
        
        if ((typ == TYP_DAMA)&&(Nastaveni.letajiciDamy)) {
            //skakani:
            boolean skace = false;
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                boolean bylCizi = false;
                for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                    XY oznacit = new XY(novePole.x + j * ModelHry.smeryDama[i].x, novePole.y + j * ModelHry.smeryDama[i].y);
                    if (jeObyvatelneXY(oznacit)) {
                        int oznVlastnik = vlastnikXY(oznacit);
                        if (oznVlastnik == ZADNY_HRAC) {
                            if (bylCizi) {
                                oznacPole(oznacit);
                                skace = true;
                            }
                        }
                        if (oznVlastnik == vlastnik) {
                            break;
                        }
                        if (oznVlastnik == druhyHrac(vlastnik)) {
                            if (bylCizi) {
                                break;
                            }
                            bylCizi = true;
                        }
                    } else {
                        break;
                    }
                }
            }

            //chozeni:
            if (!skace) {
                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                        XY oznacit = new XY(novePole.x + j * ModelHry.smeryDama[i].x, novePole.y + j * ModelHry.smeryDama[i].y);
                        if (jeObyvatelneXY(oznacit)) {
                            int oznVlastnik = vlastnikXY(oznacit);
                            if (oznVlastnik == ZADNY_HRAC) {
                                oznacPole(oznacit);
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * metoda oznaci pole barvou (jakoze na nej lze tahnout)
     *
     * @param xy souradnice policka
     */
    public static void oznacPole(XY xy) {
        if (jeObyvatelneXY(xy)) {
            if (vlastnikXY(xy) == ZADNY_HRAC) {
                nastavXY(xy, OZNACENE_POLE);
            }
        }
    }

    
    /**
     * metoda odoznaci vsechna pole
     */
    public static void odOznacPole() {
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                if (pole[x][y] < 0) {
                    pole[x][y] = 0;
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
                int vlastnik = vlastnikXY(x, y);
                if (vlastnik == hracNaTahu) {
                    if (!muzeHrat) {                        
                        boolean mch=muzeChoditXY(x, y);
                        boolean msk=musiSkakatXY(x, y);
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
        View.hlavniFrame.setVisible(false);
        View.uvodniFrame.setVisible(true);
    }

    /**
     * metoda ukonci tah
     */
    public static void konecTahu() {
        musiSkakatNa = null;
        debugWrite("Konec tahu hrace " + View.jmenaHracu[hracNaTahu], 1);
        vybranePole = null;
        
        
        
        
        if (hracNaTahu == 0) {
            hracNaTahu = 1;
        } else {
            hracNaTahu = 0;
        }
        odOznacPole();

        //Pesak se zmeni na damu:
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            if ((typKameneXY(x, 0) == TYP_PESAK) && (vlastnikXY(x, 0) == 0)) {
                zmenaNaDamu(x, 0);
            }
            if ((typKameneXY(x, Nastaveni.velikostDesky - 1) == TYP_PESAK) && (vlastnikXY(x, Nastaveni.velikostDesky - 1) == 1)) {
                zmenaNaDamu(x, Nastaveni.velikostDesky - 1);
            }
        }

        ModelHry.debugWrite("Nyni hraje hrac " + View.jmenaHracu[hracNaTahu], 1);


        
        //Testy zda musi hrac skakat
        musiSkakat = 0;
        c1:
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                if (vlastnikXY(x, y) == hracNaTahu) {
                    if (musiSkakatXY(x, y)) {
                        musiSkakat = typKameneXY(x, y);
                        if (musiSkakat == TYP_DAMA) {
                            ModelHry.debugWrite("Hrac " + View.jmenaHracu[ModelHry.hracNaTahu] + " musi skakat damou", 0);
                        } else {
                            ModelHry.debugWrite("Hrac " + View.jmenaHracu[ModelHry.hracNaTahu] + " musi skakat", 0);
                        }
                        if (musiSkakat == TYP_DAMA) {
                            break c1;
                        }
                    }
                }
            }
        }
        testVyhry();
    }

    /**
     * metoda vraci cislo hrace, ktery neni na tahu
     *
     * @return cislo druheho hrace - ktery neni na tahu
     */
    public static int druhyHrac() {
        if (hracNaTahu == 0) {
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
        hracNaTahu=0;
        HlavniFrame.lstZpravy.removeAll();
        HlavniFrame.lstLog.removeAll();
        vybranePole = null;
        pole=new int[Nastaveni.velikostDesky][Nastaveni.velikostDesky];
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                pole[x][y] = 0;
            }
        }

        for (int y = 0; y < Nastaveni.pocetRadNaStartu; y++) {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
                if (jeObyvatelneXY(x, y)) {
                    pole[x][y] = TYP_PESAK + 2;
                }
            }
        }
        for (int y = Nastaveni.velikostDesky - Nastaveni.pocetRadNaStartu; y < Nastaveni.velikostDesky; y++) {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
                if (jeObyvatelneXY(x, y)) {
                    pole[x][y] = TYP_PESAK;
                }
            }
        }
        View.hlavniFrame=new HlavniFrame();
        View.hlavniFrame.setVisible(true);
    }

    /**
     * metoda testuje zda je na zadanych souradnicich tmave pole
     *
     * @param xy souradnice pole
     * @return true pokud je pole tmave
     */
    public static boolean jeObyvatelneXY(XY xy) {
        return jeObyvatelneXY(xy.x, xy.y);
    }

    /**
     * metoda testuje zda je na zadanych souradnicich volne obyvatelne pole
     *
     * @param xy souradnice pole
     * @return true pokud je pole volne
     */
    public static boolean jeVolneXY(XY xy) {
        return jeVolneXY(xy.x, xy.y);
    }

    /**
     * metoda testuje zda je na zadanych souradnicich volne obyvatelne pole
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud je pole volne
     */
    public static boolean jeVolneXY(int x, int y) {
        if(!jeObyvatelneXY(x,y)) return false;
        if(ziskejXY(x, y)==0) return true;
        return false;
    }
    
    /**
     * metoda testuje zda je na zadanych souradnicich tmave pole
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud je pole tmave
     */
    public static boolean jeObyvatelneXY(int x, int y) {
        if (x >= Nastaveni.velikostDesky) {
            return false;
        }
        if (y >= Nastaveni.velikostDesky) {
            return false;
        }
        if (x < 0) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        if (y % 2 == 0) //lichy radek
        {
            if (x % 2 == 1) //sudy sloupec
            {
                return true;
            }
        }
        if (y % 2 == 1) //sudy radek
        {
            if (x % 2 == 0) //lichy sloupec
            {
                return true;
            }
        }
        return false;
    }

    /**
     * metoda urci vlastnika policka na souradnicich
     *
     * @param xy souradnice pole
     * @return cislo hrace, ktery vlastni pole
     */
    public static int vlastnikXY(XY xy) {
        return vlastnikXY(xy.x, xy.y);
    }

    /**
     * metoda urci vlastnika policka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return cislo hrace, ktery vlastni pole
     */
    public static int vlastnikXY(int x, int y) {
        if (pole[x][y] <= 0) {
            return ZADNY_HRAC;
        }
        return (pole[x][y] - 1) / 2;
    }

    /**
     * metoda urci typ policka policka na souradnicich
     *
     * @param xy souradnice pole
     * @return TYP_DAMA nebo TYP_PESAK ci 0
     */
    public static int typKameneXY(XY xy) {
        return typKameneXY(xy.x, xy.y);
    }

    /**
     * metoda urci typ policka policka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return TYP_DAMA nebo TYP_PESAK ci 0
     */
    public static int typKameneXY(int x, int y) {
        if (pole[x][y] <= 0) {
            return 0;
        }
        return (pole[x][y] - 1) % 2 + 1;
    }

    /**
     * metoda vrati obsah pole na souradnicich
     *
     * @param xy souradnice pole
     * @return obsah pole na soradnicich
     */
    public static int ziskejXY(XY xy) {
        return pole[xy.x][xy.y];
    }

    /**
     * metoda vrati obsah pole na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return obsah pole na soradnicich
     */
    public static int ziskejXY(int x, int y) {
        return pole[x][y];
    }

    /**
     * nastavuje obsah pole na soradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @param value obsah pole
     */
    public static void nastavXY(int x, int y, int value) {
        pole[x][y] = value;
    }

    /**
     * nastavuje obsah pole na soradnicich
     *
     * @param xy souradnice pole
     * @param value obsah pole
     */
    public static void nastavXY(XY xy, int value) {
        pole[xy.x][xy.y] = value;
    }

    /**
     * zjistuje zda musi skakat figurka na souradnicich
     *
     * @param xy souradnice pole
     * @return true pokud musi skakat
     */
    public static boolean musiSkakatXY(XY xy) {
        return musiSkakatXY(xy.x, xy.y);
    }

    /**
     * zjistuje zda muze chodit figurka na souradnicich
     *
     * @param xy souradnice pole
     * @return true pokud muze chodit
     */
    public static boolean muzeChoditXY(XY xy) {
        return muzeChoditXY(xy.x, xy.y);
    }

    /**
     * zjistuje zda muze chodit figurka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud muze chodit
     */
    public static boolean muzeChoditXY(int x, int y) {
        int vlastnik = vlastnikXY(x, y);
        int typ = typKameneXY(x, y);

        if (typ == TYP_PESAK) {
            for (int i = 0; i < ModelHry.smeryPesakVpred[vlastnik].length; i++) {
                if (jeVolneXY(x + ModelHry.smeryPesakVpred[vlastnik][i].x, y + ModelHry.smeryPesakVpred[vlastnik][i].y)) {                  
                    return true;
                }
            }
        }
        if (typ == TYP_DAMA) {

            if (Nastaveni.letajiciDamy) {
                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                        XY oznacit = new XY(x + j * ModelHry.smeryDama[i].x, y + j * ModelHry.smeryDama[i].y);
                        if (jeVolneXY(oznacit)) {
                            int oznVlastnik = vlastnikXY(oznacit);
                            if (oznVlastnik == ZADNY_HRAC) {
                                return true;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    if (jeVolneXY(x + ModelHry.smeryDama[i].x, y + ModelHry.smeryDama[i].y)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * zjistuje zda musi skakat figurka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud musi skakat
     */
    public static boolean musiSkakatXY(int x, int y) {
        int vlastnik = vlastnikXY(x, y);
        int typ = typKameneXY(x, y);
        if(typ==0) return false;
        if (typ == TYP_PESAK) {
            XY smeryPesakSkace[][] = ModelHry.smeryPesakVpred;
            if(Nastaveni.pesakSkaceDozadu) {
                smeryPesakSkace = ModelHry.smeryPesakVpredIVzad;
            }


            for (int i = 0; i < smeryPesakSkace[vlastnik].length; i++) {
                XY poleZa = new XY(x + smeryPesakSkace[vlastnik][i].x * 2, y + smeryPesakSkace[vlastnik][i].y * 2);
                XY poleMezi = new XY(x + smeryPesakSkace[vlastnik][i].x, y + smeryPesakSkace[vlastnik][i].y);
                if (poleZa.jeObyvatelne() && poleMezi.jeObyvatelne()) {
                    if ((ziskejXY(poleZa) == 0) && (vlastnikXY(poleMezi) == druhyHrac(vlastnik))) {
                        return true;
                    }
                }
            }
        }
        
        if ((typ == TYP_DAMA)&&(!Nastaveni.letajiciDamy))
        {
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                XY poleZa = new XY(x + ModelHry.smeryDama[i].x * 2, y + ModelHry.smeryDama[i].y * 2);
                XY poleMezi = new XY(x + ModelHry.smeryDama[i].x, y + ModelHry.smeryDama[i].y);
                if (poleZa.jeObyvatelne() && poleMezi.jeObyvatelne()) {
                    if ((ziskejXY(poleZa) == 0) && (vlastnikXY(poleMezi) == druhyHrac(vlastnik))) {
                        return true;
                    }
                }
            }
        }            
        
        if ((typ == TYP_DAMA)&&(Nastaveni.letajiciDamy)) {
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                boolean bylCizi = false;
                for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                    XY oznacit = new XY(x + j * ModelHry.smeryDama[i].x, y + j * ModelHry.smeryDama[i].y);
                    if (jeObyvatelneXY(oznacit)) {
                        int oznVlastnik = vlastnikXY(oznacit);
                        if (oznVlastnik == ZADNY_HRAC) {
                            if (bylCizi) {
                                return true;
                            }
                        }
                        if (oznVlastnik == vlastnik) {
                            break;
                        }
                        if (oznVlastnik == druhyHrac(vlastnik)) {
                            if (bylCizi) {
                                break;
                            }
                            bylCizi = true;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * zmena pesaka na damu na danych souradicich
     * 
     * @param xy souradnice
     */
    public static void zmenaNaDamu(XY xy) {
        zmenaNaDamu(xy.x, xy.y);
    }

    /**
     * zmena pesaka na damu na danych souradicich
     * 
     * @param x xova souradnice
     * @param y yova souradnice
     */
    public static void zmenaNaDamu(int x, int y) 
    {
        int vlastnik = vlastnikXY(x, y);
        nastavXY(x, y, (vlastnik + 1) * TYP_DAMA);
    }
}
