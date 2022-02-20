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

    private int port=0;
    
    private class pripojovaciVlakno extends Thread
    {
        @Override
        public void run()
        {
            Sit.pripojitSeKeHre(PripojFrame.txfAdresa.getText(),port);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.PripojFrame.btnOK) {
            
            try
            {
                port=Integer.parseInt(PripojFrame.txfPort.getText());
            }catch(NumberFormatException ex)
            {
                return;
            }
                    
            view.View.pripojFrame.setVisible(false);            
            (new pripojovaciVlakno()).start();
            
        }

        if (e.getSource() == view.PripojFrame.btnStorno) {
            view.View.pripojFrame.setVisible(false);
            view.View.uvodniFrame.setVisible(true);
        }
    }
}
