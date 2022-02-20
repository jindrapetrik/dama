package model;

import java.io.*;

/**
 * Tato trida slouzi k ulozeni a nacteni nastaveni
 *
 * @author petrij2
 */
public class Nastaveni {

    /** zda dama muze o vice nez 1 pole */
    public static boolean letajiciDamy = true;
    /** zda pesak skace vzad */
    public static boolean pesakSkaceDozadu = false; 
    /** Pocet zaplnenych rad po spusteni */
    public static int pocetRadNaStartu = 3; 
    /** Velikost hraci desky def. 8x8 */
    public static int velikostDesky = 8;
    /** Nazev souboru s nastavenim */
    private static String nazevSouboru="dama.cfg";
    /** Nastaveni IQ umele inteligence*/
    public static int IQAI=100000;
    /** Nastaveni maximalniho casu behu AI*/
    public static int AIMaxCasBehu=2000;
    
    /**
     * metoda ulozi nastaveni do souboru
     */
    public static void ulozit() {
        try {
            FileOutputStream fos = new FileOutputStream(nazevSouboru);
            fos.write(Nastaveni.velikostDesky);
            fos.write(Nastaveni.pocetRadNaStartu);
            fos.write(Nastaveni.pesakSkaceDozadu ? 1 : 0);
            fos.write(Nastaveni.letajiciDamy ? 1 : 0);
            fos.close();
        } catch (IOException ioe) {
            System.err.println("Chyba: nelze uložit nastavení");
        }
    }

    /**
     * metoda nacte nastaveni ze souboru
     */
    public static void nacist() {
        try {
            if (!(new File("dama.cfg")).exists()) {
                return;
            }
            FileInputStream fis = new FileInputStream(nazevSouboru);
            Nastaveni.velikostDesky = fis.read();
            Nastaveni.pocetRadNaStartu = fis.read();
            Nastaveni.pesakSkaceDozadu = fis.read() == 1;
            Nastaveni.letajiciDamy = fis.read() == 1;           
                
            fis.close();
        } catch (IOException ioe) {
            System.err.println("Chyba: nelze načíst nastavení");
        }
    }
}
