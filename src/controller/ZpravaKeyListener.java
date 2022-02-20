package controller;

import java.awt.event.*;
import view.*;
import model.*;

/**
 * Odchytavani klavesy ENTER pri psani zpravy
 * 
 * @author petrij2
 */
public class ZpravaKeyListener extends KeyAdapter {
    private String posledniText="";
    @Override
    public void keyReleased(KeyEvent e) {
        int key=e.getKeyCode();
        if(key==KeyEvent.VK_UP)
        {
            HlavniFrame.txfZprava.setText(posledniText);
        }
        if (key == KeyEvent.VK_ENTER) {
            String text = HlavniFrame.txfZprava.getText();
            posledniText=text;
            if(text.equals("")) return;
            String vypsat=text;
            if(ModelHry.typHry==ModelHry.TYPHRY_SITOVA)
            {
                if(ModelHry.typyHracu[0]==ModelHry.TYPHRACE_ZDEJSI)
                  vypsat=view.View.jmenaHracu[0]+":"+vypsat;
                else 
                  vypsat=view.View.jmenaHracu[1]+":"+vypsat;
            }
            view.HlavniFrame.pridatZpravu(vypsat);
            if ((!Cheaty.testCheat(text)) && (ModelHry.typHry == ModelHry.TYPHRY_SITOVA)) {
                Sit.poslatZpravu(text);
            }
            HlavniFrame.txfZprava.setText("");
        }
    }
}
