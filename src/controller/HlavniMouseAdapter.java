package controller;

import view.*;
import model.*;
import java.awt.event.*;

/**
 * Adapter pro mys na hlavnim hernim formulari
 *  
 * @author petrij2
 */
public class HlavniMouseAdapter extends MouseAdapter {

    /**
     * kliknuti hrace na dane policko na mape
     * @param x xova souradnice
     * @param y yova souradnice
     */
    public static void kliknutoXY(int x, int y) {      
        
        if (!ModelHry.jeObyvatelneXY(x, y)) {
            return;
        }

        if (ModelHry.typyHracu[ModelHry.hracNaTahu] == ModelHry.TYPHRACE_ZDEJSI) {
            Sit.poslatTah(x, y);
        }

        //Výběr panáka:
        if (ModelHry.vlastnikXY(x, y) == ModelHry.hracNaTahu) {
            if (ModelHry.musiSkakatNa != null) {
                if (!ModelHry.musiSkakatNa.equals(x, y)) {
                    return;
                }
            }
            ModelHry.vyberPole(x, y);
            View.hlavniFrame.repaint();
            return;
        //Posun nebo skok:
        } else if (ModelHry.vlastnikXY(x, y) == ModelHry.ZADNY_HRAC) {
            if (ModelHry.vybranePole == null) {
                return;
            }
            int typ = ModelHry.typKameneXY(ModelHry.vybranePole);

            if (typ == ModelHry.TYP_PESAK) {
                //Pokud je vybrano pole v urcitem smeru od vybraneho:
                boolean ok = false;

                for (int i = 0; i < ModelHry.smeryPesakVpred[ModelHry.hracNaTahu].length; i++) {
                    ok = ((x == ModelHry.vybranePole.x + ModelHry.smeryPesakVpred[ModelHry.hracNaTahu][i].x) && (y == ModelHry.vybranePole.y + ModelHry.smeryPesakVpred[ModelHry.hracNaTahu][i].y));
                    if (ok) {
                        break;
                    }
                }
                //a pokud nemusi skakat
                if (ok && (ModelHry.musiSkakat == 0)) {
                    //Presun kamene:
                    ModelHry.pole[x][y] = ModelHry.pole[ModelHry.vybranePole.x][ModelHry.vybranePole.y];
                    ModelHry.pole[ModelHry.vybranePole.x][ModelHry.vybranePole.y] = 0;
                    ModelHry.konecTahu();
                } else //pokud nema ve skakani prednost dama:
                if (ModelHry.musiSkakat < ModelHry.TYP_DAMA) {

                    //skakani:
                    ok = false;
                    XY smeryPesakSkace[][] = ModelHry.smeryPesakVpred;
                    if (Nastaveni.pesakSkaceDozadu) {
                        smeryPesakSkace = ModelHry.smeryPesakVpredIVzad;
                    }

                    for (int i = 0; i < smeryPesakSkace[ModelHry.hracNaTahu].length; i++) {
                        XY poleZa = new XY(ModelHry.vybranePole.x + smeryPesakSkace[ModelHry.hracNaTahu][i].x * 2,
                                ModelHry.vybranePole.y + smeryPesakSkace[ModelHry.hracNaTahu][i].y * 2);
                        if (poleZa.equals(x, y)) {
                            XY poleMezi = new XY(ModelHry.vybranePole.x + smeryPesakSkace[ModelHry.hracNaTahu][i].x, ModelHry.vybranePole.y + smeryPesakSkace[ModelHry.hracNaTahu][i].y);
                            if (ModelHry.vlastnikXY(poleMezi) == ModelHry.druhyHrac()) {
                                ModelHry.nastavXY(poleZa, ModelHry.ziskejXY(ModelHry.vybranePole));
                                ModelHry.nastavXY(ModelHry.vybranePole, 0);
                                ModelHry.nastavXY(poleMezi, 0);
                                if (ModelHry.musiSkakatXY(poleZa)) {
                                    ModelHry.vyberPole(poleZa);
                                    ModelHry.musiSkakatNa = poleZa;
                                    ModelHry.debugWrite("Hrac " + View.jmenaHracu[ModelHry.hracNaTahu] + " musi pokracovat ve skakani", 0);
                                } else {
                                    ModelHry.konecTahu();
                                }
                            }
                            break;
                        }

                    }
                }
                View.hlavniFrame.repaint();
                return;
            }
            if ((typ == ModelHry.TYP_DAMA)&&(!Nastaveni.letajiciDamy)) {
                //Pokud je vybrano pole v urcitem smeru od vybraneho:
                boolean ok = false;

                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    ok = ((x == ModelHry.vybranePole.x + ModelHry.smeryDama[i].x) && (y == ModelHry.vybranePole.y + ModelHry.smeryDama[i].y));
                    if (ok) {
                        break;
                    }
                }
                //a pokud nemusi skakat
                if (ok && (ModelHry.musiSkakat == 0)) {
                    //Presun kamene:
                    ModelHry.pole[x][y] = ModelHry.pole[ModelHry.vybranePole.x][ModelHry.vybranePole.y];
                    ModelHry.pole[ModelHry.vybranePole.x][ModelHry.vybranePole.y] = 0;
                    ModelHry.konecTahu();
                } else 
                {

                    //skakani:
                    ok = false;

                    for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                        XY poleZa = new XY(ModelHry.vybranePole.x + ModelHry.smeryDama[i].x * 2,
                                ModelHry.vybranePole.y + ModelHry.smeryDama[i].y * 2);
                        if (poleZa.equals(x, y)) {
                            XY poleMezi = new XY(ModelHry.vybranePole.x + ModelHry.smeryDama[i].x, ModelHry.vybranePole.y + ModelHry.smeryDama[i].y);
                            if (ModelHry.vlastnikXY(poleMezi) == ModelHry.druhyHrac()) {
                                ModelHry.nastavXY(poleZa, ModelHry.ziskejXY(ModelHry.vybranePole));
                                ModelHry.nastavXY(ModelHry.vybranePole, 0);
                                ModelHry.nastavXY(poleMezi, 0);
                                if (ModelHry.musiSkakatXY(poleZa)) {
                                    ModelHry.vyberPole(poleZa);
                                    ModelHry.musiSkakatNa = poleZa;
                                    ModelHry.debugWrite("Hrac " + View.jmenaHracu[ModelHry.hracNaTahu] + " musi pokracovat ve skakani damou", 0);
                                } else {
                                    ModelHry.konecTahu();
                                }
                            }
                            break;
                        }

                    }
                }
                View.hlavniFrame.repaint();
                return;
            }
            if ((typ == ModelHry.TYP_DAMA)&&(Nastaveni.letajiciDamy)) {
                for (int i = 0; i < ModelHry.smeryDama.length; i++) {
                    boolean bylCizi = false;
                    XY ciziXY = null;
                    for (int j = 1; j < Nastaveni.velikostDesky; j++) {
                        XY oznacit = new XY(ModelHry.vybranePole.x + j * ModelHry.smeryDama[i].x, ModelHry.vybranePole.y + j * ModelHry.smeryDama[i].y);
                        if (ModelHry.jeObyvatelneXY(oznacit)) {
                            int oznVlastnik = ModelHry.vlastnikXY(oznacit);
                            if (oznVlastnik == ModelHry.ZADNY_HRAC) {
                                if (oznacit.equals(x, y)) {
                                    if (bylCizi) {
                                        int t = ModelHry.ziskejXY(ModelHry.vybranePole);
                                        ModelHry.nastavXY(ModelHry.vybranePole, 0);
                                        ModelHry.nastavXY(ciziXY, 0);
                                        ModelHry.nastavXY(x, y, t);
                                        if (ModelHry.musiSkakatXY(x, y)) {
                                            ModelHry.vyberPole(x, y);
                                            ModelHry.musiSkakatNa = new XY(x, y);
                                            ModelHry.debugWrite("Hrac " + View.jmenaHracu[ModelHry.hracNaTahu] + " musi pokracovat ve skakani damou", 0);
                                        } else {
                                            ModelHry.konecTahu();
                                        }
                                    } else if (ModelHry.musiSkakat == 0) {
                                        int t = ModelHry.ziskejXY(ModelHry.vybranePole);
                                        ModelHry.nastavXY(ModelHry.vybranePole, 0);
                                        ModelHry.nastavXY(x, y, t);
                                        ModelHry.konecTahu();
                                    }
                                    View.hlavniFrame.repaint();
                                    return;
                                }
                            }
                            if (oznVlastnik == ModelHry.hracNaTahu) {
                                break;
                            }
                            if (oznVlastnik == ModelHry.druhyHrac()) {
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


    }

    /**
     * odchytava kliknuti mysi
     * @param e udalost mysi
     */
    @Override
    public void mousePressed(MouseEvent e) {

        int x = (e.getX() - View.X_DESKY) / View.SIRKA_POLICKA;
        int y = (e.getY() - View.Y_DESKY) / View.VYSKA_POLICKA;
        
        if(ModelHry.typHry==ModelHry.TYPHRY_SITOVA)
                if(ModelHry.typyHracu[1]==ModelHry.TYPHRACE_ZDEJSI)
                {
                    x=Nastaveni.velikostDesky-x-1;
                    y=Nastaveni.velikostDesky-y-1;
                }
        
        if (!ModelHry.jeObyvatelneXY(x, y)) {
            return;
        }

        if (Cheaty.cheatEdit) {
            if (e.getButton() == 1) {
                ModelHry.nastavXY(x, y, 1);
            }
            if (e.getButton() == 3) {
                ModelHry.nastavXY(x, y, 3);
            }
            if (e.getButton() == 2) {
                ModelHry.nastavXY(x, y, 0);
            }
            View.hlavniFrame.repaint();
            return;
        }

        if (ModelHry.typyHracu[ModelHry.hracNaTahu] == ModelHry.TYPHRACE_SITOVY) {
            return;
        }
        kliknutoXY(x, y);

    }
    }
