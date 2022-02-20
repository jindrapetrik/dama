package model;
import java.util.*;
/** Tato trida slouzi pro spousteni umele inteligence
 *
 * @author JPEXS
 */
public class AI extends Thread {
    /** Udava, zda je AI zaneprazdnen */
    private static boolean bezi=false;
    /** Urcuje uroven umele inteligence */
    private int IQ=100;
    private static boolean ukonci=false;
    
    /** Vraci zda je AI zaneprazdnen
     * @return true pokud je zaneprazdnen
     */
    public static boolean bezi()
    {
            return bezi;
    }
    
    
    private static class Ukoncovac extends TimerTask
    {

        @Override
        public void run() {
            ukonci=true;
        }
    }
    
    /**
     * Vytvori AI s danym IQ
     * @param IQ
     */
    public AI(int IQ)
    {
        this.IQ=IQ;
    }
    
    
    
    /**
     * Vymysli a zahraje 1 tah s danou inteligenci
     * @param IQ
     */
    public static void hraj(int IQ) {
        (new AI(IQ)).start();
    }
    
    
    @Override
    public void run()
    {
        bezi=true;
        ukonci=false;
        Timer tim=new Timer();
        Ukoncovac uk=new Ukoncovac();
        tim.schedule(uk, Nastaveni.AIMaxCasBehu);
        /*try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
              }*/
        LinkedList<Stav> stavy = new LinkedList<Stav>();  
        
        //Vypnout vypisovani
        ModelHry.debugLevel=-1;
        int hrac=ModelHry.ZADNY_HRAC;
        Stav stav = ModelHry.stav;
        hrac=stav.hracNaTahu;
        stavy.add(stav);

        int pocet = 0;
        int nejvyssiKvalita = Integer.MIN_VALUE;
        LinkedList<Stav> nejvicKvalitniStavy = new LinkedList<Stav>();
        nejvicKvalitniStavy.add(stav);

        do {
            stav = stavy.get(pocet);
            stav.spocitejMozneTahy();
            if(pocet==0)
            {
                //pokud je jen jeden mozny tah, proved ho
                if(stav.tahy.size()==1)
                {
                    nejvicKvalitniStavy = new LinkedList<Stav>();
                    stav.posledniTah=stav.tahy.get(0);
                    nejvicKvalitniStavy.add(stav);
                    break;
                }
            }
            for (Tah tah : stav.tahy) {
                if(ukonci) break;
                Stav novy = stav.kopieStavu();//stav.tahy.size());
                novy.spustitTah(tah);
                int kvalita = novy.zjistiKvalitu(hrac);
                if (kvalita == nejvyssiKvalita) {
                    nejvicKvalitniStavy.add(novy);
                }
                if (kvalita > nejvyssiKvalita) {
                    nejvyssiKvalita = kvalita;
                    nejvicKvalitniStavy = new LinkedList<Stav>();
                    nejvicKvalitniStavy.add(novy);
                }
                stavy.add(novy);
            }
            stav.tahy.clear();
            pocet++;
            if(ukonci) break;
        } while ((pocet < IQ) && (stavy.size() > pocet));

        
        
        //Z nejkvalitnejsich tahu vyber nahodne jeden
        Random rnd=new Random();
        Stav nejvicKvalitniStav=nejvicKvalitniStavy.get(rnd.nextInt(nejvicKvalitniStavy.size()));
        
        if(nejvicKvalitniStav.predeslyStav!=null)
        while(nejvicKvalitniStav.predeslyStav.predeslyStav!=null)
        {
            System.out.println(nejvicKvalitniStav);
            nejvicKvalitniStav=nejvicKvalitniStav.predeslyStav;                 
        }   
        System.out.println(nejvicKvalitniStav);
        System.out.println("Nejvyssi kvalita:"+nejvyssiKvalita);
        nejvicKvalitniStav.predeslyStav = null;
        nejvicKvalitniStav.hloubka=0;    
        
        //Zapnout vypisovani
        ModelHry.debugLevel=1;
        
        //Vybrat kamen
        controller.HlavniMouseAdapter.kliknutoXY(nejvicKvalitniStav.posledniTah.odkud.x, nejvicKvalitniStav.posledniTah.odkud.y);
        
        //Pockat pul sekundy
        try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
              }
        
        //Vybrat cilove pole
        controller.HlavniMouseAdapter.kliknutoXY(nejvicKvalitniStav.posledniTah.kam.x, nejvicKvalitniStav.posledniTah.kam.y);
                
        tim.cancel();
        tim=null;
        stavy.clear();
        nejvicKvalitniStavy.clear();
        stavy=null;
        nejvicKvalitniStavy=null;
        System.gc();
        view.View.hlavniFrame.repaint();                
        bezi=false;
        //Pokud jsem stale na tahu, hraju dal
        if(ModelHry.typHry==ModelHry.TYPHRY_PC)
         if(ModelHry.typyHracu[ModelHry.stav.hracNaTahu]==ModelHry.TYPHRACE_PC)
         {            
             hraj(IQ);
         }
    }
}
