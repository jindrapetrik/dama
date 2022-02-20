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
        
        if (!ModelHry.stav.jeObyvatelneXY(x, y)) {
            return;
        }

        if (ModelHry.typyHracu[ModelHry.stav.hracNaTahu] == ModelHry.TYPHRACE_ZDEJSI) {
            Sit.poslatTah(x, y);
        }

        //Výběr panáka:
        if (ModelHry.stav.vlastnikXY(x, y) == ModelHry.stav.hracNaTahu) {
            if (ModelHry.stav.musiSkakatNa != null) {
                if (!ModelHry.stav.musiSkakatNa.equals(x, y)) {
                    return;
                }
            }
            ModelHry.vyberPole(x, y);
            View.hlavniFrame.repaint();
            return;
        //Posun nebo skok:
        } else if (ModelHry.stav.vlastnikXY(x, y) == ModelHry.ZADNY_HRAC) {
            if (ModelHry.vybranePole == null) {
                return;
            }
            ModelHry.stav.spustitTah(new Tah(ModelHry.vybranePole,new XY(x,y)));            
                
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
        
        if (!ModelHry.stav.jeObyvatelneXY(x, y)) {
            return;
        }

        if (Cheaty.cheatEdit) {
            int kamen=ModelHry.stav.typKameneXY(x,y);
            int vlastnik=ModelHry.stav.vlastnikXY(x,y);
            if(kamen==ModelHry.TYP_PESAK)
            {
                kamen=ModelHry.TYP_DAMA;
            }
            else
            if(kamen==ModelHry.TYP_DAMA)
            {
                kamen=0;
            }
            else
            {
                kamen=ModelHry.TYP_PESAK;
            }
            
            if (e.getButton() == 1) {  
                if(vlastnik==1) kamen=1;
                ModelHry.stav.nastavXY(x, y, kamen);
            }
            if (e.getButton() == 3) {
                if(vlastnik==0) kamen=1;
                if(kamen==0) kamen=-2;
                ModelHry.stav.nastavXY(x, y, kamen+2);
            }
            if (e.getButton() == 2) {
                ModelHry.stav.nastavXY(x, y, 0);
            }
            View.hlavniFrame.repaint();
            return;
        }

        if (ModelHry.typyHracu[ModelHry.stav.hracNaTahu] == ModelHry.TYPHRACE_SITOVY) {
            return;
        }
        if (ModelHry.typyHracu[ModelHry.stav.hracNaTahu] == ModelHry.TYPHRACE_PC) {
            return;
        }
        kliknutoXY(x, y);

    }
    }
