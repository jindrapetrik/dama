/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.*;
import view.*;

/**
 *
 * @author petrij2
 */
public class Stav {

    /** Obsahuje pozice kamenu */
    public int pole[][];
    /** Urcuje cislo hrace, ktery prave hraje */
    public int hracNaTahu;
    /** Urcuje tahy, ktere hrac muze hrat */
    public LinkedList<Tah> tahy;
    /** Urcuje pocet figurek jednotlivych hracu na desce */
    private int pocetFigurek[];
    /** Urcuje pocet pesaku jednotlivych hracu na desce */
    private int pocetPesaku[];
    /** Urcuje pocet dam jednotlivych hracu na desce */
    private int pocetDam[];
    /** Urcuje zda musi hrac skakat - je zde TYP policka - dama nebo pesak */
    public int musiSkakat = 0;
    /** Policko, kde musi skakat (protoze tam v predchozim tahu skocil) */
    public XY musiSkakatNa = null;
    /** Urcuje predesly stav, ze ktereho je tento odvozen (Slouzi pro AI) */
    public Stav predeslyStav=null;
    /** Urcuje hloubku zanoreni stavu (Slouzi pro AI) */
    public int hloubka=0;
    /** Urcuje posledni zahrany tah (Slouzi pro AI) */
    public Tah posledniTah=null;

    /**
     * Konstruktor stavu..
     * @param hracNaTahu - hrac ktery zrovna hraje
     */
    public Stav(int hracNaTahu) {
        pole = new int[Nastaveni.velikostDesky][Nastaveni.velikostDesky]; //Zde jsou ulozeny data mapy - pesaci a damy    

        this.hracNaTahu = hracNaTahu;
        for (int x = 0; x < pole.length; x++) {
            for (int y = 0; y < pole[0].length; y++) {
                this.pole[x][y] = 0;
            }
        }
        spocitejMozneTahy();
    }

    /**
     * Konstruktor stavu
     * @param pole
     * @param hracNaTahu
     * @param musiSkakat
     * @param musiSkakatNa
     */
    public Stav(int[][] pole, int hracNaTahu, int musiSkakat, XY musiSkakatNa) {
        if (pole == null) {
            return;
        }
        this.musiSkakat = musiSkakat;
        this.musiSkakatNa = musiSkakatNa;
        this.pole = new int[pole.length][pole[0].length];
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                this.pole[x][y] = pole[x][y];
            }
        }
        this.hracNaTahu = hracNaTahu;
        spocitejMozneTahy();
    }

    /**
     * Zkopiruje aktualni stav
     * @param mnozstvi 
     * @return kopie stavu
     */
    public Stav kopieStavu(int mnozstvi) {
        Stav novy=new Stav(pole, hracNaTahu, musiSkakat, musiSkakatNa);
        novy.predeslyStav=this;
        novy.hloubka=this.hloubka+mnozstvi;
        return novy;
    }
    
    /**
     * Zkopiruje aktualni stav
     * @return
     */
    public Stav kopieStavu() {
        return kopieStavu(1);
    }

    /**
     * Vypocita vsechny mozne tahy aktualniho hrace a ulozi do pole "tahy"
     */
    public void spocitejMozneTahy() {
        LinkedList<Tah> ret = new LinkedList<Tah>();

        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                if (vlastnikXY(x, y) == hracNaTahu) {
                    LinkedList<Tah> rtahy = vratMozneTahyXY(x, y);
                    ret.addAll(rtahy);
                }
            }
        }
        tahy = ret;
    }

    /**
     * Spocita vsechny figurky a ulozi je do poli pocetFigurek,pocetPesaku,pocetDam
     */
    public void spocitejFigurky() {
        pocetFigurek = new int[2];
        pocetFigurek[0] = 0;
        pocetFigurek[1] = 0;
        pocetPesaku = new int[2];
        pocetPesaku[0] = 0;
        pocetPesaku[1] = 0;
        pocetDam = new int[2];
        pocetDam[0] = 0;
        pocetDam[1] = 0;

        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                int typ = typKameneXY(x, y);
                int vlastnik = vlastnikXY(x, y);
                if (vlastnik != ModelHry.ZADNY_HRAC) {
                    pocetFigurek[vlastnik]++;
                    if (typ == ModelHry.TYP_PESAK) {
                        pocetPesaku[vlastnik]++;
                    }
                    if (typ == ModelHry.TYP_DAMA) {
                        pocetDam[vlastnik]++;
                    }
                }
            }
        }
    }

    /**
     * Zjistuje pocet figurek daneho vlastnika
     * @param vlastnik
     * @return pocet figurek
     */
    public int zjistiPocetFigurek(int vlastnik) {
        return pocetFigurek[vlastnik];
    }

    /**
     * Zjistuje pocet figurek daneho typu vlastnika
     * @param typ (TYP_PESAK,TYP_DAMA)
     * @param vlastnik
     * @return pocet figurek
     */
    public int zjistiPocetFigurek(int typ, int vlastnik) {
        if (typ == ModelHry.TYP_PESAK) {
            return pocetPesaku[vlastnik];
        }
        if (typ == ModelHry.TYP_DAMA) {
            return pocetDam[vlastnik];
        }
        return 0;
    }

    /**
     * Vypocita kvalitu daneho stavu pro daneho hrace
     * @param vlastnik
     * @return index kvality
     */
    public int zjistiKvalitu(int vlastnik) {
        int CENA_DAMY = 200;  //1.1  200
        int CENA_PESAKA = 100; //1.1  100
        int CENA_HLOUBKY = 50; //1.1  50
        int CENA_CIZIHO_PESAKA=-200; 
        int CENA_CIZI_DAMY = -600;
        int hloubkaPlus=hloubka*CENA_HLOUBKY;//(int)Math.round(Math.pow(5, hloubka));
        int druhy = ModelHry.druhyHrac(vlastnik);
        spocitejFigurky();
        return pocetPesaku[vlastnik] * CENA_PESAKA + pocetDam[vlastnik] * CENA_DAMY - pocetPesaku[druhy] * CENA_PESAKA - pocetDam[druhy] * CENA_DAMY-CENA_HLOUBKY*hloubka;
        //return pocetPesaku[vlastnik] * CENA_PESAKA + pocetDam[vlastnik] * CENA_DAMY + pocetPesaku[druhy] * CENA_CIZIHO_PESAKA + pocetDam[druhy] * CENA_CIZI_DAMY-hloubkaPlus;
    }

    /**
     * metoda testuje zda je na zadanych souradnicich tmave pole
     *
     * @param xy souradnice pole
     * @return true pokud je pole tmave
     */
    public boolean jeObyvatelneXY(XY xy) {
        return jeObyvatelneXY(xy.x, xy.y);
    }

    /**
     * metoda testuje zda je na zadanych souradnicich volne obyvatelne pole
     *
     * @param xy souradnice pole
     * @return true pokud je pole volne
     */
    public boolean jeVolneXY(XY xy) {
        return jeVolneXY(xy.x, xy.y);
    }

    /**
     * metoda testuje zda je na zadanych souradnicich volne obyvatelne pole
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud je pole volne
     */
    public boolean jeVolneXY(int x, int y) {
        if (!jeObyvatelneXY(x, y)) {
            return false;
        }
        if (ziskejXY(x, y) == 0) {
            return true;
        }
        return false;
    }

    /**
     * metoda testuje zda je na zadanych souradnicich tmave pole
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud je pole tmave
     */
    public boolean jeObyvatelneXY(int x, int y) {
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
    public int vlastnikXY(XY xy) {
        return vlastnikXY(xy.x, xy.y);
    }

    /**
     * metoda urci vlastnika policka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return cislo hrace, ktery vlastni pole
     */
    public int vlastnikXY(int x, int y) {
        if (pole[x][y] <= 0) {
            return ModelHry.ZADNY_HRAC;
        }
        return (pole[x][y] - 1) / 2;
    }

    /**
     * metoda urci typ policka policka na souradnicich
     *
     * @param xy souradnice pole
     * @return TYP_DAMA nebo TYP_PESAK ci 0
     */
    public int typKameneXY(XY xy) {
        return typKameneXY(xy.x, xy.y);
    }

    /**
     * metoda urci typ policka policka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return TYP_DAMA nebo TYP_PESAK ci 0
     */
    public int typKameneXY(int x, int y) {
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
    public int ziskejXY(XY xy) {
        return pole[xy.x][xy.y];
    }

    /**
     * metoda vrati obsah pole na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return obsah pole na soradnicich
     */
    public int ziskejXY(int x, int y) {
        return pole[x][y];
    }

    /**
     * nastavuje obsah pole na soradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @param value obsah pole
     */
    public void nastavXY(int x, int y, int value) {
        pole[x][y] = value;
    }

    /**
     * nastavuje obsah pole na soradnicich
     *
     * @param xy souradnice pole
     * @param value obsah pole
     */
    public void nastavXY(XY xy, int value) {
        pole[xy.x][xy.y] = value;
    }

    /**
     * zjistuje zda musi skakat figurka na souradnicich
     *
     * @param xy souradnice pole
     * @return true pokud musi skakat
     */
    public boolean musiSkakatXY(XY xy) {
        return musiSkakatXY(xy.x, xy.y);
    }

    /**
     * zjistuje zda muze chodit figurka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud muze chodit
     */
    public boolean muzeChoditXY(int x, int y) {
        int vlastnik = vlastnikXY(x, y);
        int typ = typKameneXY(x, y);

        if (typ == ModelHry.TYP_PESAK) {
            for (int i = 0; i < ModelHry.smeryPesakVpred[vlastnik].length; i++) {
                if (jeVolneXY(x + ModelHry.smeryPesakVpred[vlastnik][i].x, y + ModelHry.smeryPesakVpred[vlastnik][i].y)) {
                    return true;
                }
            }
        }
        if (typ == ModelHry.TYP_DAMA) {

            if (Nastaveni.letajiciDamy) {
                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                        XY oznacit = new XY(x + j * ModelHry.smeryDama[i].x, y + j * ModelHry.smeryDama[i].y);
                        if (jeVolneXY(oznacit)) {
                            int oznVlastnik = vlastnikXY(oznacit);
                            if (oznVlastnik == ModelHry.ZADNY_HRAC) {
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
     * zjistuje zda muze chodit figurka na souradnicich
     *
     * @param xy souradnice pole
     * @return true pokud muze chodit
     */
    public boolean muzeChoditXY(XY xy) {
        return muzeChoditXY(xy.x, xy.y);
    }

    /**
     * zjistuje zda musi skakat figurka na souradnicich
     *
     * @param x xova souradnice pole
     * @param y yova souradnice pole
     * @return true pokud musi skakat
     */
    public boolean musiSkakatXY(int x, int y) {
        int vlastnik = vlastnikXY(x, y);
        int typ = typKameneXY(x, y);
        if (typ == 0) {
            return false;
        }
        if (typ == ModelHry.TYP_PESAK) {
            XY smeryPesakSkace[][] = ModelHry.smeryPesakVpred;
            if (Nastaveni.pesakSkaceDozadu) {
                smeryPesakSkace = ModelHry.smeryPesakVpredIVzad;
            }


            for (int i = 0; i < smeryPesakSkace[vlastnik].length; i++) {
                XY poleZa = new XY(x + smeryPesakSkace[vlastnik][i].x * 2, y + smeryPesakSkace[vlastnik][i].y * 2);
                XY poleMezi = new XY(x + smeryPesakSkace[vlastnik][i].x, y + smeryPesakSkace[vlastnik][i].y);
                if (jeObyvatelneXY(poleZa) && jeObyvatelneXY(poleMezi)) {
                    if ((ziskejXY(poleZa) == 0) && (vlastnikXY(poleMezi) == ModelHry.druhyHrac(vlastnik))) {
                        return true;
                    }
                }
            }
        }

        if ((typ == ModelHry.TYP_DAMA) && (!Nastaveni.letajiciDamy)) {
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                XY poleZa = new XY(x + ModelHry.smeryDama[i].x * 2, y + ModelHry.smeryDama[i].y * 2);
                XY poleMezi = new XY(x + ModelHry.smeryDama[i].x, y + ModelHry.smeryDama[i].y);
                if (jeObyvatelneXY(poleZa) && jeObyvatelneXY(poleMezi)) {
                    if ((ziskejXY(poleZa) == 0) && (vlastnikXY(poleMezi) == ModelHry.druhyHrac(vlastnik))) {
                        return true;
                    }
                }
            }
        }

        if ((typ == ModelHry.TYP_DAMA) && (Nastaveni.letajiciDamy)) {
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                boolean bylCizi = false;
                for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                    XY oznacit = new XY(x + j * ModelHry.smeryDama[i].x, y + j * ModelHry.smeryDama[i].y);
                    if (jeObyvatelneXY(oznacit)) {
                        int oznVlastnik = vlastnikXY(oznacit);
                        if (oznVlastnik == ModelHry.ZADNY_HRAC) {
                            if (bylCizi) {
                                return true;
                            }
                        }
                        if (oznVlastnik == vlastnik) {
                            break;
                        }
                        if (oznVlastnik == ModelHry.druhyHrac(vlastnik)) {
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
    public void zmenaNaDamu(XY xy) {
        zmenaNaDamu(xy.x, xy.y);
    }

    /**
     * zmena pesaka na damu na danych souradicich
     * 
     * @param x xova souradnice
     * @param y yova souradnice
     */
    public void zmenaNaDamu(int x, int y) {
        int vlastnik = vlastnikXY(x, y);
        nastavXY(x, y, (vlastnik + 1) * ModelHry.TYP_DAMA);
    }

    /**
     * metoda oznaci pole barvou (jakoze na nej lze tahnout)
     *
     * @param xy souradnice policka
     */
    public void oznacPole(XY xy) {
        if (jeObyvatelneXY(xy)) {
            if (vlastnikXY(xy) == ModelHry.ZADNY_HRAC) {
                nastavXY(xy, ModelHry.OZNACENE_POLE);
            }
        }
    }

    /**
     * Vrati vsechny mozne tahy figurky na dane pozici
     * @param x xova souradnice
     * @param y yova souradnice
     * @return kolekce moznych tahu
     */
    public LinkedList<Tah> vratMozneTahyXY(int x, int y) {
        return vratMozneTahyXY(new XY(x, y));
    }

    /**
     * Vrati vsechny mozne tahy figurky na dane pozici
     * @param xy souradnice figurky
     * @return kolekce moznych tahu
     */
    public LinkedList<Tah> vratMozneTahyXY(XY xy) {
        int vlastnik = vlastnikXY(xy);
        int typ = typKameneXY(xy);
        LinkedList<Tah> ret = new LinkedList<Tah>();
        if (vlastnik == ModelHry.ZADNY_HRAC) {
            return ret;
        }
        if (typ == ModelHry.TYP_PESAK) {
            if (musiSkakat == ModelHry.TYP_DAMA) {
                return ret;
            }
            boolean skaka = false;
            //skakani:

            XY smeryPesakSkace[][] = ModelHry.smeryPesakVpred;
            if (Nastaveni.pesakSkaceDozadu) {
                smeryPesakSkace = ModelHry.smeryPesakVpredIVzad;
            }

            for (int i = 0; i < smeryPesakSkace[vlastnik].length; i++) {
                XY poleZa = new XY(xy.x + smeryPesakSkace[vlastnik][i].x * 2,
                        xy.y + smeryPesakSkace[vlastnik][i].y * 2);
                XY poleMezi = new XY(xy.x + smeryPesakSkace[vlastnik][i].x, xy.y + smeryPesakSkace[vlastnik][i].y);

                if (jeObyvatelneXY(poleZa)) {
                    if (jeObyvatelneXY(poleMezi)) {
                        if (vlastnikXY(poleMezi) == ModelHry.druhyHrac(vlastnik)) {
                            if (vlastnikXY(poleZa) == ModelHry.ZADNY_HRAC) {
                                ret.add(new Tah(xy, poleZa, Tah.SKAKANI));
                                skaka = true;
                            }
                        }
                    }
                }
            }

            if (skaka) {
                return ret;
            }
            if (musiSkakat > 0) {
                return ret;
            }

            for (int i = 0; i < ModelHry.smeryPesakVpred[vlastnik].length; i++) {
                XY oznacit = new XY(xy.x + ModelHry.smeryPesakVpred[vlastnik][i].x, xy.y + ModelHry.smeryPesakVpred[vlastnik][i].y);
                if (jeVolneXY(oznacit)) {
                    ret.add(new Tah(xy, oznacit));
                }
            }
        }

        if ((typ == ModelHry.TYP_DAMA) && (!Nastaveni.letajiciDamy)) {
            boolean skaka = false;
            //skakani:
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                XY poleZa = new XY(xy.x + ModelHry.smeryDama[i].x * 2,
                        xy.y + ModelHry.smeryDama[i].y * 2);
                XY poleMezi = new XY(xy.x + ModelHry.smeryDama[i].x, xy.y + ModelHry.smeryDama[i].y);

                if (jeObyvatelneXY(poleZa)) {
                    if (jeObyvatelneXY(poleMezi)) {
                        if (vlastnikXY(poleMezi) == ModelHry.druhyHrac(vlastnik)) {
                            if (vlastnikXY(poleZa) == ModelHry.ZADNY_HRAC) {
                                ret.add(new Tah(xy, poleZa, Tah.SKAKANI_DAMOU));
                                skaka = true;
                            }
                        }
                    }
                }
            }

            if (skaka) {
                return ret;
            }
            if (musiSkakat > 0) {
                return ret;
            }

            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                XY oznacit = new XY(xy.x + ModelHry.smeryDama[i].x, xy.y + ModelHry.smeryDama[i].y);
                if (jeVolneXY(oznacit)) {
                    ret.add(new Tah(xy, oznacit));
                }
            }
        }

        if ((typ == ModelHry.TYP_DAMA) && (Nastaveni.letajiciDamy)) {
            //skakani:
            if (musiSkakat == ModelHry.TYP_PESAK) {
                return ret;
            }
            boolean skace = false;
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                boolean bylCizi = false;
                for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                    XY oznacit = new XY(xy.x + j * ModelHry.smeryDama[i].x, xy.y + j * ModelHry.smeryDama[i].y);
                    if (jeObyvatelneXY(oznacit)) {
                        int oznVlastnik = vlastnikXY(oznacit);
                        if (oznVlastnik == ModelHry.ZADNY_HRAC) {
                            if (bylCizi) {
                                ret.add(new Tah(xy, oznacit, Tah.SKAKANI_DAMOU));
                                skace = true;
                            }
                        }
                        if (oznVlastnik == vlastnik) {
                            break;
                        }
                        if (oznVlastnik == ModelHry.druhyHrac(vlastnik)) {
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
            if(musiSkakat==0)
            if (!skace) {
                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                        XY oznacit = new XY(xy.x + j * ModelHry.smeryDama[i].x, xy.y + j * ModelHry.smeryDama[i].y);
                        if (jeObyvatelneXY(oznacit)) {
                            int oznVlastnik = vlastnikXY(oznacit);
                            if (oznVlastnik == ModelHry.ZADNY_HRAC) {
                                ret.add(new Tah(xy, oznacit));
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
        return ret;
    }

    /**
     * Spusti dany tah
     * @param tah Tah pro spusteni
     */
    public void spustitTah(Tah tah) {
        posledniTah=tah;
        int typ = typKameneXY(tah.odkud);

        if (typ == ModelHry.TYP_PESAK) {
            //Pokud je vybrano pole v urcitem smeru od vybraneho:
            boolean ok = false;

            for (int i = 0; i < ModelHry.smeryPesakVpred[hracNaTahu].length; i++) {
                ok = ((tah.kam.x == tah.odkud.x + ModelHry.smeryPesakVpred[hracNaTahu][i].x) && (tah.kam.y == tah.odkud.y + ModelHry.smeryPesakVpred[hracNaTahu][i].y));
                if (ok) {
                    break;
                }
            }
            //a pokud nemusi skakat
            if (ok && (musiSkakat == 0)) {
                //Presun kamene:
                pole[tah.kam.x][tah.kam.y] = pole[tah.odkud.x][tah.odkud.y];
                pole[tah.odkud.x][tah.odkud.y] = 0;
                konecTahu();
            } else //pokud nema ve skakani prednost dama:
            if (musiSkakat < ModelHry.TYP_DAMA) {

                //skakani:
                ok = false;
                XY smeryPesakSkace[][] = ModelHry.smeryPesakVpred;
                if (Nastaveni.pesakSkaceDozadu) {
                    smeryPesakSkace = ModelHry.smeryPesakVpredIVzad;
                }

                for (int i = 0; i < smeryPesakSkace[hracNaTahu].length; i++) {
                    XY poleZa = new XY(tah.odkud.x + smeryPesakSkace[hracNaTahu][i].x * 2,
                            tah.odkud.y + smeryPesakSkace[hracNaTahu][i].y * 2);
                    if (poleZa.equals(tah.kam.x, tah.kam.y)) {
                        XY poleMezi = new XY(tah.odkud.x + smeryPesakSkace[hracNaTahu][i].x, tah.odkud.y + smeryPesakSkace[hracNaTahu][i].y);
                        if (vlastnikXY(poleMezi) == ModelHry.druhyHrac(hracNaTahu)) {
                            nastavXY(poleZa, ziskejXY(tah.odkud));
                            nastavXY(tah.odkud, 0);
                            nastavXY(poleMezi, 0);
                            if (musiSkakatXY(poleZa)) {
                                musiSkakatNa = poleZa;
                                if (this == ModelHry.stav) {
                                    ModelHry.vyberPole(poleZa);
                                }
                                    ModelHry.debugWrite("Hrac " + View.jmenaHracu[hracNaTahu] + " musi pokracovat ve skakani", 0);
                                
                            } else {
                                konecTahu();
                            }
                        }
                        break;
                    }

                }
            }
            if (this == ModelHry.stav) {
                View.hlavniFrame.repaint();
            }
            return;
        }
        if ((typ == ModelHry.TYP_DAMA) && (!Nastaveni.letajiciDamy)) {
            //Pokud je vybrano pole v urcitem smeru od vybraneho:
            boolean ok = false;

            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                ok = ((tah.kam.x == tah.odkud.x + ModelHry.smeryDama[i].x) && (tah.kam.y == tah.odkud.y + ModelHry.smeryDama[i].y));
                if (ok) {
                    break;
                }
            }
            //a pokud nemusi skakat
            if (ok && (musiSkakat == 0)) {
                //Presun kamene:
                pole[tah.kam.x][tah.kam.y] = pole[tah.odkud.x][tah.odkud.y];
                pole[tah.odkud.x][tah.odkud.y] = 0;
                konecTahu();
            } else {

                //skakani:
                ok = false;

                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    XY poleZa = new XY(tah.odkud.x + ModelHry.smeryDama[i].x * 2,
                            tah.odkud.y + ModelHry.smeryDama[i].y * 2);
                    if (poleZa.equals(tah.kam.x, tah.kam.y)) {
                        XY poleMezi = new XY(tah.odkud.x + ModelHry.smeryDama[i].x, tah.odkud.y + ModelHry.smeryDama[i].y);
                        if (vlastnikXY(poleMezi) == ModelHry.druhyHrac(hracNaTahu)) {
                            nastavXY(poleZa, ziskejXY(tah.odkud));
                            nastavXY(tah.odkud, 0);
                            nastavXY(poleMezi, 0);
                            if (musiSkakatXY(poleZa)) {

                                musiSkakatNa = poleZa;
                                if (this == ModelHry.stav) {
                                    ModelHry.vyberPole(poleZa);
                                }
                                    ModelHry.debugWrite("Hrac " + View.jmenaHracu[hracNaTahu] + " musi pokracovat ve skakani damou", 0);
                                
                            } else {
                                konecTahu();
                            }
                        }
                        break;
                    }

                }
            }
            if (this == ModelHry.stav) {
                View.hlavniFrame.repaint();
            }
            return;
        }
        if ((typ == ModelHry.TYP_DAMA) && (Nastaveni.letajiciDamy)) {
            for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                boolean bylCizi = false;
                XY ciziXY = null;
                for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                    XY oznacit = new XY(tah.odkud.x + j * ModelHry.smeryDama[i].x, tah.odkud.y + j * ModelHry.smeryDama[i].y);
                    if (jeObyvatelneXY(oznacit)) {
                        int oznVlastnik = vlastnikXY(oznacit);
                        if (oznVlastnik == ModelHry.ZADNY_HRAC) {
                            if (oznacit.equals(tah.kam.x, tah.kam.y)) {
                                if (bylCizi) {
                                    int t = ziskejXY(tah.odkud);
                                    nastavXY(tah.odkud, 0);
                                    nastavXY(ciziXY, 0);
                                    nastavXY(tah.kam.x, tah.kam.y, t);
                                    if (musiSkakatXY(tah.kam.x, tah.kam.y)) {

                                        musiSkakatNa = new XY(tah.kam.x, tah.kam.y);
                                        if (this == ModelHry.stav) {
                                            ModelHry.vyberPole(tah.kam.x, tah.kam.y);
                                        }
                                            ModelHry.debugWrite("Hrac " + View.jmenaHracu[hracNaTahu] + " musi pokracovat ve skakani damou", 0);
                                        
                                    } else {
                                        konecTahu();
                                    }
                                } else if (musiSkakat == 0) {
                                    int t = ziskejXY(tah.odkud);
                                    nastavXY(tah.odkud, 0);
                                    nastavXY(tah.kam.x, tah.kam.y, t);
                                    konecTahu();
                                }
                                if (this == ModelHry.stav) {
                                    View.hlavniFrame.repaint();
                                }
                                return;
                            }
                        }
                        if (oznVlastnik == hracNaTahu) {
                            break;
                        }
                        if (oznVlastnik == ModelHry.druhyHrac(hracNaTahu)) {
                            if (bylCizi) {
                                break;
                            }
                            bylCizi = true;
                            ciziXY = oznacit;
                        }
                    } else {
                        break;
                    }
                }
            }

        }
    }

    /**
     * metoda ukonci tah
     */
    public void konecTahu() {
        musiSkakatNa = null;





        if (hracNaTahu == 0) {
            hracNaTahu = 1;
        } else {
            hracNaTahu = 0;
        }




        //Pesak se zmeni na damu:
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            if ((typKameneXY(x, 0) == ModelHry.TYP_PESAK) && (vlastnikXY(x, 0) == 0)) {
                zmenaNaDamu(x, 0);
            }
            if ((typKameneXY(x, Nastaveni.velikostDesky - 1) == ModelHry.TYP_PESAK) && (vlastnikXY(x, Nastaveni.velikostDesky - 1) == 1)) {
                zmenaNaDamu(x, Nastaveni.velikostDesky - 1);
            }
        }






        //Testy zda musi hrac skakat
        musiSkakat = 0;
        c1:
        for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                if (vlastnikXY(x, y) == hracNaTahu) {
                    if (musiSkakatXY(x, y)) {
                        musiSkakat = typKameneXY(x, y);
                        if (musiSkakat == ModelHry.TYP_DAMA) {
                            ModelHry.debugWrite("Hrac " + View.jmenaHracu[hracNaTahu] + " musi skakat damou", 0);
                        } else {
                            ModelHry.debugWrite("Hrac " + View.jmenaHracu[hracNaTahu] + " musi skakat", 0);
                        }
                        if (musiSkakat == ModelHry.TYP_DAMA) {
                            break c1;
                        }
                    }
                }
            }
        }
        
        
        //if (this == ModelHry.stav) {
            ModelHry.debugWrite("Konec tahu hrace " + View.jmenaHracu[hracNaTahu], 1);
            ModelHry.vybranePole = null;
            ModelHry.odOznacPole();
            ModelHry.debugWrite("Nyni hraje hrac " + View.jmenaHracu[hracNaTahu], 1);
            if(ModelHry.testVyhry()) return;
        //}
        
        if(ModelHry.typHry==ModelHry.TYPHRY_PC)
        {
            if(!AI.bezi())
            {
                if(ModelHry.stav.hracNaTahu==1)
                    AI.hraj(Nastaveni.IQAI);
            }
        }

    }
    
    @Override
    public String toString()
    {
        String r="\r\nnatahu:"+hracNaTahu+"\r\n";
        int i=0;
        for (int y = 0; y < Nastaveni.velikostDesky; y++)
        {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
                if(pole[x][y]==0)
                    if (i % 2 == 1) {
                     r+="#";
                } else {
                    r+=" ";
                }
                else
                r+=pole[x][y];
                i++;
            }
            i++;
            r+="\r\n";
        }
        return r;                
    }
}
