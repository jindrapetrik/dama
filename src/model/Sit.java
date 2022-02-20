package model;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Trida pro praci se siti
 *
 * @author petrij2
 */
public class Sit {

    /** vstupni stream pro prijem dat */
    private static InputStream is;
    /** vystupni stream pro odchozi data */
    private static OutputStream os;
    /** Sitova operace - "Zadna operace" */
    public static final int SOP_NOP = 0;
    /** Sitova operace - tah */
    public static final int SOP_TAH = 1;
    /** Sitova operace - textova zprava */
    public static final int SOP_ZPRAVA = 2;
    /** Sitova operace - konec hry */
    public static final int SOP_KONEC = 10;
    
    /** Cislo sitoveho hrace*/
    public static int cisloSitoveho = -1;
    /**Socket pro klienta */
    static Socket ksock;
    /**Socket pro server */
    static ServerSocket ssock;
    /** Seznam akci k odeslani */
    static LinkedList akce=new LinkedList();

   
    /**
     * vlakno pro prijem tahu, zprav     
     */
    public static class cteniTahu extends Thread {

        @Override
        public void run() {
            try {
                int akce = -1;
                while ((akce != SOP_KONEC) && (!ModelHry.vypinani)) {                    
                    akce = is.read();
                    switch (akce) {
                        case -1:
                            akce = SOP_KONEC;                   
                            break;
                        case SOP_NOP:
                            break;
                        case SOP_TAH:
                            int x=-1;
                            while(x==-1)
                              x = is.read();
                            int y=-1;
                            while(y==-1)
                              y = is.read();
                            System.out.println("Prijat TAH: ["+x+","+y+"]");
                            boolean naTahuSit = ModelHry.stav.hracNaTahu == cisloSitoveho;
                            if (naTahuSit) {
                                controller.HlavniMouseAdapter.kliknutoXY(x, y);
                            }
                            break;
                        case SOP_ZPRAVA:
                            int delka = is.read();
                            byte pole[] = new byte[delka];
                            for (int p = 0; p < delka; p++) {
                                int i = -1;
                                while (i == -1) {
                                    i = is.read();
                                }
                                pole[p] = (byte) i;
                            }
                            String text = new String(pole);
                            System.out.println("Prijata ZPRAVA: \""+text+"\"");
                            text=view.View.jmenaHracu[cisloSitoveho]+":"+text;
                            view.HlavniFrame.lstZpravy.add(text);
                            break;
                        default:
                            System.out.println("Prijat nejaky shit:"+akce);
                            break;
                    }



                }
            } catch (Exception e) {

            }
            if(!ModelHry.vypinani)
            {
              javax.swing.JOptionPane.showMessageDialog(null, "Spojení ukončeno!", "Konec", javax.swing.JOptionPane.WARNING_MESSAGE);
              ModelHry.konecHry();              
            }
        }
    }

    /**
     * vlakno pro odesilani tahu, zprav
     */
    public static class poslaniTahu extends Thread {

        @Override
        public void run() {

            try {
                while (!ModelHry.vypinani) {                    
                    if(!akce.isEmpty())
                    {
                        Object o=akce.removeFirst();
                        if(o instanceof XY)
                        {
                            os.write(SOP_TAH);
                            os.write(((XY)o).x);
                            os.write(((XY)o).y);
                            System.out.println("Poslan TAH: ["+((XY)o).x+","+((XY)o).y+"]");
                            continue;
                        }
                        if(o instanceof String)
                        {
                            os.write(SOP_ZPRAVA);
                            byte pole[] = ((String)o).getBytes();
                            os.write(pole.length);
                            os.write(pole);
                            System.out.println("Poslana ZPRAVA: \""+(String)o+"\"");
                            continue;
                        }                        
                    }
                    
                    try {
                        poslaniTahu.sleep(200);
                    } catch (Exception e) {
                    }
                                        
                    os.write(SOP_NOP);

                }
            } catch (IOException e) {
                
            }

        }
    }

    /**
     * spusti cteni tahu/zprav
     */
    public static void cistTahy() {
        (new cteniTahu()).start();
    }

    /**
     * zaradi do fronty tah k odeslani
     * @param x xova souradnice
     * @param y yova souradnice
     */
    public static void poslatTah(int x, int y) {
        akce.add(new XY(x,y));
    }

    /**
     * zaradi do fronty zpravu k odeslani     
     * @param zprava zprava k odeslani
     */
    public static void poslatZpravu(String zprava) {
        if(zprava.equals("")) return;
        akce.add(zprava);
    }

    /**
     * spusti odesilani tahu/zprav
     */
    public static void startPosilani() {
        if (ModelHry.typHry != ModelHry.TYPHRY_SITOVA) {
            return;
        }
        (new poslaniTahu()).start();
    }
    
    /**
     * zalozi hru na danem portu
     * @param port port naslouchani
     */
    public static void zalozitHru(int port) {
        ModelHry.vypinani=false;
        ModelHry.typyHracu[0] = ModelHry.TYPHRACE_ZDEJSI;
        ModelHry.typyHracu[1] = ModelHry.TYPHRACE_SITOVY;
        cisloSitoveho = 1;
        ModelHry.typHry = ModelHry.TYPHRY_SITOVA;        
        try {
            ssock = new ServerSocket(port);
            System.out.println("Server spusten, cekam na klienta");            
            ksock = ssock.accept();
            if (!ModelHry.vypinani) {
                is = ksock.getInputStream();
                os = ksock.getOutputStream();

                os.write(Nastaveni.velikostDesky);
                os.write(Nastaveni.pocetRadNaStartu);
                os.write(Nastaveni.pesakSkaceDozadu ? 1 : 0);
                os.write(Nastaveni.letajiciDamy ? 1 : 0);
            }
            System.out.println("Klient pripojen");
        } catch (Exception e) {
           return;
        }
        view.View.cekaciFrame.setVisible(false);
        if (!ModelHry.vypinani) {
            ModelHry.novaHra();
            startPosilani();
            cistTahy();
        }
    }

    /**
     * pripoji hrace ke hre na dane adrese/portu
     * @param adresa sitova adresa
     * @param port cilovy port
     */
    public static void pripojitSeKeHre(String adresa, int port) {
        ModelHry.vypinani=false;
        ModelHry.typyHracu[1] = ModelHry.TYPHRACE_ZDEJSI;
        ModelHry.typyHracu[0] = ModelHry.TYPHRACE_SITOVY;
        cisloSitoveho = 0;
        ModelHry.typHry = ModelHry.TYPHRY_SITOVA;
        try {
            ksock = new Socket(adresa, port);
            System.out.println("Pripojeno na server");
            is = ksock.getInputStream();
            os = ksock.getOutputStream();
            Nastaveni.velikostDesky = is.read();
            Nastaveni.pocetRadNaStartu = is.read();
            Nastaveni.pesakSkaceDozadu = is.read() == 1;
            Nastaveni.letajiciDamy = is.read() == 1;
        } catch (Exception e) {
            System.out.println("Nelze se pripojit");
            view.View.pripojFrame.setVisible(true);
            return;
        }
        view.View.pripojFrame.setVisible(false);
        ModelHry.novaHra();
        cistTahy();
        startPosilani();
    }

    /** 
     * spusti hru hrace proti PC
     */
    public static void protiPC() {
        ModelHry.typyHracu[0] = ModelHry.TYPHRACE_ZDEJSI;
        ModelHry.typyHracu[1] = ModelHry.TYPHRACE_PC;
        ModelHry.typHry = ModelHry.TYPHRY_PC;
        ModelHry.novaHra();
    }
    
    /**
     * Ukonci sitovou komunikaci     
     */
    public static void ukoncit() {
        try {
            is.close();
            os.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        try {
            if (ModelHry.typPripojeni == ModelHry.TYPPRIPOJENI_ZAKLADA) {
                ssock.close();
                ksock.close();
            } else {
                ksock.close();
            }
        } catch (Exception e) {
        }
    }
}
