package controller;

import java.awt.event.*;
import model.*;
import view.*;

/**
 * Naslouchac pro kliknuti na tlacitka ve formulari s nastavenim
 * @author petrij2
 */
public class NastaveniActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.NastaveniFrame.btnOK) {            
            Nastaveni.letajiciDamy=NastaveniFrame.chkLetajiciDamy.getState();
            Nastaveni.pesakSkaceDozadu=NastaveniFrame.chkPesakSkaceDozadu.getState();
            Nastaveni.velikostDesky=8+NastaveniFrame.choVelikost.getSelectedIndex()*2;
            Nastaveni.pocetRadNaStartu=2+NastaveniFrame.choPocetRad.getSelectedIndex();
            View.nastaveniFrame.setVisible(false); 
            if(ModelHry.typHry==ModelHry.TYPHRY_SITOVA)
            {                                
                Sit.zalozitHru(Integer.parseInt(ZalozFrame.txfPort.getText()));
            }
            else
            {
                Sit.samSeSebou();
            }                                   
        }

        if (e.getSource() == view.NastaveniFrame.btnStorno) {
            view.View.nastaveniFrame.setVisible(false);
            view.View.uvodniFrame.setVisible(true);
        }
    }
}
