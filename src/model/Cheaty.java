

package model;

/**
 * Tato trida se pouziva pro nastaveni Cheatu
 *
 * @author petrij2
 */
public class Cheaty {
    
    //Urcuje, zda jsou aktivni dane cheaty:
    
    /** Mod "dajma" -  V tomto modu ma dama zvlastni oznaceni */
    public static boolean cheatDajma=false;
    /** Mod editace */
    public static boolean cheatEdit=false;
    /** Hezky mod - premeni barvy na ruzovou a fialovou */
    public static boolean cheatHezky=false;
    
    /**
     * metoda ktera zjisti, zda je zadany retezec cheat a pripadne ho aplikuje
     *
     * @param s testovany retezec
     * @return true pokud je retezec cheatem, jinak false
     */
    public static boolean testCheat(String s)
    {
        //Spusti na 1 tah umelou inteligenci pro aktualniho hrace
        if(s.equals("jpaigo"))
        {
            if(AI.bezi())
            {
                view.HlavniFrame.pridatZpravu("AI je zaneprazdnen");
            }
            else
            {
            AI.hraj(Nastaveni.IQAI);
            view.HlavniFrame.pridatZpravu("AI zahral tah");
            }
            return true;
        }
                
        //Nastavi uroven umele inteligence
        if(s.startsWith("jpiq"))
        {
            try{
              Nastaveni.IQAI=Integer.parseInt(s.substring(4));
              view.HlavniFrame.pridatZpravu("IQ nastaveno na "+Nastaveni.IQAI);
            return true;
            }catch(NumberFormatException ex)
            {}            
        }
        
        //Mod dajma
        if(s.equals("jpdajma")) 
        {
            cheatDajma=!cheatDajma;
            view.View.hlavniFrame.repaint();
            if(cheatDajma)
               view.HlavniFrame.pridatZpravu("Mod dajmy zapnut");
            else
                view.HlavniFrame.pridatZpravu("Mod dajmy vypnut");
            return true;
        }
        
        //Mod editace
        if(s.equals("jpedit")) 
        {
            //funguje jen pri hre sam se sebou
            if(ModelHry.typHry==ModelHry.TYPHRY_SITOVA)
                return false;
            cheatEdit=!cheatEdit;
            if(cheatEdit)
               view.HlavniFrame.pridatZpravu("Mod editace zapnut");
            else
                view.HlavniFrame.pridatZpravu("Mod editace vypnut");
            return true;
        }
        
        //Hezky mod
        if(s.equals("jphezky")) 
        {
            cheatHezky=!cheatHezky;
            
            if(cheatHezky)
            {
                view.View.BARVY_HRACU[0]=new java.awt.Color(255,0,255);
                view.View.BARVY_HRACU[1]=new java.awt.Color(128,0,128);
                view.View.BARVA_TMAVA=java.awt.Color.PINK;
                view.View.jmenaHracu[0]="růžový";
                view.View.jmenaHracu[1]="fialový";
               view.HlavniFrame.pridatZpravu("Hezky mod zapnut");
            }
            else
            {
                view.View.BARVY_HRACU[0]=java.awt.Color.WHITE;
                view.View.BARVY_HRACU[1]=java.awt.Color.GRAY;
                view.View.BARVA_TMAVA=new java.awt.Color(130,52,0);
                view.View.jmenaHracu[0]="bílý";
                view.View.jmenaHracu[1]="černý";
                view.HlavniFrame.pridatZpravu("Navrat do oskliveho modu");
            }
            view.View.hlavniFrame.repaint();
            return true;
        }
        
        
        //Vymaze celou plochu (slouzi k editaci)
        if(s.equals("jpvymaz")) 
        {
            for (int x = 0; x < Nastaveni.velikostDesky; x++) {
            for (int y = 0; y < Nastaveni.velikostDesky; y++) {
                ModelHry.stav.nastavXY(x, y,0);
            }
            }
            view.View.hlavniFrame.repaint();
            return true;
        }
        
        
        return false;
    }
}
