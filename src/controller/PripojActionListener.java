package controller;

import java.awt.event.*;
import model.*;
import view.*;

/**
 * Naslouchac pro kliknuti na tlacitka ve formulari s pripojovanim
 * 
 * @author petrij2
 */
public class PripojActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.PripojFrame.btnOK) {
            int port=0;
            try
            {
                port=Integer.parseInt(PripojFrame.txfPort.getText());
            }catch(NumberFormatException ex)
            {
                return;
            }
                    
            view.View.pripojFrame.setVisible(false);
            Sit.pripojitSeKeHre(PripojFrame.txfAdresa.getText(),port);
            View.pripojFrame.setVisible(false);
            
        }

        if (e.getSource() == view.PripojFrame.btnStorno) {
            view.View.pripojFrame.setVisible(false);
            view.View.uvodniFrame.setVisible(true);
        }
    }
}
