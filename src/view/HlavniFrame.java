package view;

import controller.*;
import model.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Herni formular
 * 
 * @author petrij2
 */
public class HlavniFrame extends Frame {

    public static List lstLog = new List();
    public static List lstZpravy = new List();
    public static TextField txfZprava =new TextField();

    public HlavniFrame() {

        this.setLayout(null);
        int sirkaPole=View.SIRKA_POLICKA*Nastaveni.velikostDesky;
        int vyskaPole=View.VYSKA_POLICKA*Nastaveni.velikostDesky;
        
        lstZpravy.setBounds(View.X_DESKY+sirkaPole+10, View.Y_DESKY, 200, vyskaPole);
        txfZprava.setBounds(View.X_DESKY+sirkaPole+10, View.Y_DESKY+vyskaPole+5, 200, 25);
        txfZprava.addKeyListener(new controller.ZpravaKeyListener());
        lstLog.setBounds(5, View.Y_DESKY+vyskaPole+5, sirkaPole, 25);             
        this.setResizable(false);
        
        this.add(lstLog);
        this.add(lstZpravy);
        this.add(txfZprava);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                    ModelHry.konecHry();
                    view.View.hlavniFrame.setVisible(false);
                    view.View.uvodniFrame.setVisible(true);               
            }
            });
        this.addMouseListener(new HlavniMouseAdapter());
        this.setTitle("Dáma");
        this.setBounds(100, 100, View.X_DESKY+sirkaPole+10+200+10,View.Y_DESKY+vyskaPole+35);  
        Dimension dimScreen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimScreen.width/2-this.getWidth()/2, dimScreen.height/2-this.getHeight()/2);
        
    }

    /**
     * Prida do listu zpravu
     *        
     * @param s zprava na pridani
     */
    public static void pridatZpravu(String s)
    {
        lstZpravy.add(s);
        lstZpravy.makeVisible(lstZpravy.getItemCount()-1);
    }
    
    /**
     * Vykresleni policek     
     */
    @Override
    public void paint(Graphics g) {
        int i = 1;
        g.setColor(View.BARVA_OKRAJE);
        g.drawRect(View.X_DESKY - 1, View.Y_DESKY - 1, Nastaveni.velikostDesky * View.SIRKA_POLICKA + 1, Nastaveni.velikostDesky * View.VYSKA_POLICKA + 1);
        for (int y = 0; y < Nastaveni.velikostDesky; y++) {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
                
                int kx=x;
                int ky=y;
                if(ModelHry.typHry==ModelHry.TYPHRY_SITOVA)
                if(ModelHry.typyHracu[1]==ModelHry.TYPHRACE_ZDEJSI)
                {
                    kx=Nastaveni.velikostDesky-kx-1;
                    ky=Nastaveni.velikostDesky-ky-1;
                }
                    
                if (i % 2 == 1) {
                    g.setColor(View.BARVA_SVETLA);
                } else {
                    g.setColor(View.BARVA_TMAVA);
                }

                if (ModelHry.ziskejXY(x, y) == ModelHry.OZNACENE_POLE) {
                    if(ModelHry.typyHracu[ModelHry.hracNaTahu]==ModelHry.TYPHRACE_SITOVY)
                     g.setColor(View.BARVA_OZNACENEHO_CIZIHO);   
                    else
                     g.setColor(View.BARVA_OZNACENEHO);
                }
                if(ModelHry.vybranePole!=null)
                if (ModelHry.vybranePole.x == x) {
                    if (ModelHry.vybranePole.y == y) {
                        if(ModelHry.typyHracu[ModelHry.hracNaTahu]==ModelHry.TYPHRACE_SITOVY)
                          g.setColor(View.BARVA_VYBRANE_CIZI);   
                        else
                          g.setColor(View.BARVA_VYBRANE);
                    }
                }
                g.fillRect(View.X_DESKY + kx * View.SIRKA_POLICKA, View.Y_DESKY + ky * View.VYSKA_POLICKA, View.SIRKA_POLICKA, View.VYSKA_POLICKA);
                int kamen = ModelHry.typKameneXY(x, y);
                if (kamen > 0) {
                    g.setColor(View.BARVY_HRACU[ModelHry.vlastnikXY(x, y)]);
                    g.fillOval(View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2 - View.PRUMER_KAMENU / 2, View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA / 2 - View.PRUMER_KAMENU / 2, View.PRUMER_KAMENU, View.PRUMER_KAMENU);
                    g.setColor(View.BARVA_KRUZNICE);
                    g.drawOval(View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2 - View.PRUMER_KAMENU / 2, View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA / 2 - View.PRUMER_KAMENU / 2, View.PRUMER_KAMENU, View.PRUMER_KAMENU);

                    



                    if (kamen == ModelHry.TYP_DAMA) {
                        if (Cheaty.cheatDajma) {
                            int delta = View.SIRKA_POLICKA / 2 - View.PRUMER_KAMENU / 2;                            
                            int xBody[] = new int[5];
                            xBody[0] = View.X_DESKY + kx * View.SIRKA_POLICKA + delta;
                            xBody[1] = View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2;
                            xBody[2] = View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA - delta;
                            xBody[3] = View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2;
                            xBody[4] = View.X_DESKY + kx * View.SIRKA_POLICKA + delta;

                            int yBody[] = new int[5];
                            yBody[0] = View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA / 2;
                            yBody[1] = View.Y_DESKY + ky * View.VYSKA_POLICKA + delta;
                            yBody[2] = View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA / 2;
                            yBody[3] = View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA - delta;
                            yBody[4] = View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA / 2;

                            g.drawPolyline(xBody, yBody, 5);
                            g.drawLine(View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2, View.Y_DESKY + ky * View.VYSKA_POLICKA + 2*delta, View.X_DESKY + x * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2, View.Y_DESKY + y * View.VYSKA_POLICKA + View.VYSKA_POLICKA - 2*delta);
                        } else {
                            g.drawOval(View.X_DESKY + kx * View.SIRKA_POLICKA + View.SIRKA_POLICKA / 2 - View.PRUMER_KRUHU_DAMY / 2, View.Y_DESKY + ky * View.VYSKA_POLICKA + View.VYSKA_POLICKA / 2 - View.PRUMER_KRUHU_DAMY / 2, View.PRUMER_KRUHU_DAMY, View.PRUMER_KRUHU_DAMY);
                        }
                    }
                }
                i++;
            }
            i++;
        }
    }
}
