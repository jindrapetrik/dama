package controller;

import java.awt.event.*;
import model.*;
import view.*;

/**
 * Naslouchac pro kliknuti na tlacitka ve formulari pro zalozeni hry
 * 
 * @author petrij2
 */
public class ZalozActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.ZalozFrame.btnOK) {
            int port=0;
            try
            {
                port=Integer.parseInt(ZalozFrame.txfPort.getText());
            }catch(NumberFormatException ex)
            {
                return;
            }
                    
            View.zalozFrame.setVisible(false);
            View.nastaveniFrame.zobrazit();
            
        }

        if (e.getSource() == view.ZalozFrame.btnStorno) {
            view.View.zalozFrame.setVisible(false);
            view.View.uvodniFrame.setVisible(true);
        }
    }
}
