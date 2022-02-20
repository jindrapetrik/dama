package controller;

import java.awt.event.*;
import view.*;
import model.*;

/**
 * Naslouchac pro kliknuti na tlacitka v uvodnim formulari
 * 
 * @author petrij2
 */
public class UvodniActionListener implements ActionListener {

    
    public static UvodniActionListener instance=new UvodniActionListener();

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.UvodniFrame.btnZalozit) {
            ModelHry.typHry=ModelHry.TYPHRY_SITOVA;
            ModelHry.typPripojeni=ModelHry.TYPPRIPOJENI_ZAKLADA;
            View.zalozFrame.setVisible(true);
            View.uvodniFrame.setVisible(false);
        }

        if (e.getSource() == view.UvodniFrame.btnPripojit) {
            ModelHry.typHry=ModelHry.TYPHRY_SITOVA;
            ModelHry.typPripojeni=ModelHry.TYPPRIPOJENI_PRIPOJUJESE;
            View.pripojFrame.setVisible(true);
            View.uvodniFrame.setVisible(false);
        }
        
        if (e.getSource() == view.UvodniFrame.btnSam) {
            ModelHry.typHry=ModelHry.TYPHRY_SAM;            
            View.nastaveniFrame.zobrazit();
            View.uvodniFrame.setVisible(false);
        }

        if (e.getSource() == view.UvodniFrame.btnUkoncit) {
            Nastaveni.ulozit();
            System.exit(0);
        }
    }
}
